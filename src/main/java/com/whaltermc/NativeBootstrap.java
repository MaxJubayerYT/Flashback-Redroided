package com.whaltermc;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Loads the FFmpeg + JavaCPP natives bundled in this mod's jar (lib/arm64-v8a/*.so).
 *
 * JavaCPP only searches lib/arm64-v8a/ when it detects "android-arm64". Zalith's JVM
 * reports a desktop Linux VM, so JavaCPP detects "linux-arm64", finds nothing and falls
 * back to System.loadLibrary(), which only searches java.library.path.
 *
 * We System.load() each library by absolute path from a Knot-loaded class (so JNI binds
 * for org.bytedeco.* classes) and tell JavaCPP not to load anything itself.
 *
 * Preferred source: the directory Zalith already extracted the libs to
 * (<java.home>/lib/jli/javacpp/<jar name>/lib/arm64-v8a). Same file = same inode, so the
 * linker reuses the already-loaded copy instead of loading a second one.
 * Fallback: extract from the jar into java.io.tmpdir.
 */
public final class NativeBootstrap {

    private static final Logger LOGGER =
            LoggerFactory.getLogger("flashback-redroided/natives");

    private static final String MOD_ID = "flashback-redroided";
    private static final String RES_DIR = "lib/arm64-v8a/";

    // Order matters: JavaCPP core first, then each FFmpeg lib followed by its JNI wrapper.
    private static final String[] LIBS = {
            "libjnijavacpp.so",
            "libavutil.so", "libjniavutil.so",
            "libswresample.so", "libjniswresample.so",
            "libavcodec.so", "libjniavcodec.so",
            "libavformat.so", "libjniavformat.so",
            "libswscale.so", "libjniswscale.so"
    };

    private static boolean done;

    private NativeBootstrap() {}

    public static synchronized void init() {
        if (done) return;
        done = true;

        // Must be set before org.bytedeco.javacpp.Loader is initialised.
        System.setProperty("org.bytedeco.javacpp.loadlibraries", "false");

        try {
            ModContainer mod = FabricLoader.getInstance().getModContainer(MOD_ID)
                    .orElseThrow(() -> new IllegalStateException("mod container not found: " + MOD_ID));

            Path dir = launcherExtractedDir(mod);
            String source = "launcher-extracted";
            if (dir == null) {
                dir = Path.of(System.getProperty("java.io.tmpdir"))
                        .resolve("flashback-redroided-natives");
                Files.createDirectories(dir);
                extract(mod, dir);
                source = "extracted from jar";
            }

            int loaded = 0;
            for (String lib : LIBS) {
                Path p = dir.resolve(lib);
                if (!Files.isRegularFile(p)) {
                    LOGGER.warn("Native missing: {}", p);
                    continue;
                }
                System.load(p.toAbsolutePath().toString());
                loaded++;
            }

            LOGGER.info("Loaded {} FFmpeg/JavaCPP natives from {} ({})", loaded, dir, source);

        } catch (Throwable t) {
            // Let JavaCPP try (and report) on its own if we failed.
            System.clearProperty("org.bytedeco.javacpp.loadlibraries");
            LOGGER.error("Failed to load bundled FFmpeg natives", t);
        }
    }

    /** Returns the launcher's extraction dir if it exists and has all libs, else null. */
    private static Path launcherExtractedDir(ModContainer mod) {
        try {
            String javaHome = System.getProperty("java.home");
            if (javaHome == null) return null;

            for (Path origin : mod.getOrigin().getPaths()) {
                Path name = origin.getFileName();
                if (name == null) continue;
                Path dir = Path.of(javaHome, "lib", "jli", "javacpp", name.toString(), "lib", "arm64-v8a");
                boolean all = true;
                for (String lib : LIBS) {
                    if (!Files.isRegularFile(dir.resolve(lib))) { all = false; break; }
                }
                if (all) return dir;
            }
        } catch (Throwable t) {
            LOGGER.debug("Launcher dir lookup failed", t);
        }
        return null;
    }

    private static void extract(ModContainer mod, Path dir) throws Exception {
        for (String lib : LIBS) {
            Optional<Path> src = mod.findPath(RES_DIR + lib);
            if (src.isEmpty()) {
                LOGGER.warn("Bundled native missing from jar: {}{}", RES_DIR, lib);
                continue;
            }
            Path dst = dir.resolve(lib);
            if (!Files.exists(dst) || Files.size(dst) != Files.size(src.get())) {
                try (InputStream in = Files.newInputStream(src.get())) {
                    Files.copy(in, dst, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
