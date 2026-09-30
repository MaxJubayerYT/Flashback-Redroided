package com.whaltermc.mixin;

import com.moulberry.flashback.Flashback;
import net.minecraft.client.Minecraft;
import org.lwjgl.sdl.SDLDialog;
import org.lwjgl.sdl.SDL_DialogFileFilter;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

@Mixin(targets = "com.moulberry.flashback.utils.AsyncFileDialogs")
public class AsyncFileDialogsMixin {

    private static File flashbackRedroided$getDefaultExportDir() {
        File dir = new File(
                Minecraft.getInstance().gameDirectory,
                "flashback/exports"
        );

        if (!dir.exists() && !dir.mkdirs()) {
            Flashback.LOGGER.warn(
                    "Could not create default export directory: {}",
                    dir.getAbsolutePath()
            );
        }

        return dir;
    }

    private static String flashbackRedroided$filter(CharSequence in) {
        return flashbackRedroided$filterLT20(
                in.toString()
                        .replace("'", "")
                        .replace("\"", "")
                        .replace("$", "")
                        .replace("`", "")
        );
    }

    private static String flashbackRedroided$filterLT20(CharSequence in) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);

            if (c >= 32 || c == '\n') {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    /**
     * Android fallback version of Flashback's save dialog.
     *
     * @author WhalterMC
     * @reason SDL's Android file dialog may be unavailable in Pojav/MJ/Zalith.
     */
    @Overwrite
    public static CompletableFuture<String> saveFileDialog(
            String defaultPath,
            String defaultName,
            String filterDescription,
            String... filters
    ) {
        // If Flashback already has a dialog open.
        // We intentionally return a completed future just like the original.
        //noinspection ConstantValue
        if (com.moulberry.flashback.utils.AsyncFileDialogs.hasDialog()) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<String> future = new CompletableFuture<>();

        String defaultLocation =
                flashbackRedroided$filter(defaultPath + "/" + defaultName);

        String autoExtension =
                filters.length == 1 ? filters[0] : null;

        long window =
                Minecraft.getInstance().getWindow().handle();

        /*
         * We cannot access Flashback's private createFilterBuffer()
         * from the Mixin, so this implementation is intended to be
         * used together with the original dialog infrastructure.
         *
         * The actual fallback happens when SDL returns NULL.
         */

        // For a launcher without SDL Android dialog support, immediately
        // use the same fallback directory introduced by commit 733f004.
        String name = defaultName;

        if (name != null
                && autoExtension != null
                && name.indexOf('.') < 0) {
            name = name + "." + autoExtension;
        }

        File fallback = new File(
                flashbackRedroided$getDefaultExportDir(),
                name != null ? name : "export"
        );

        Flashback.LOGGER.warn(
                "Using default export path: {}",
                fallback.getAbsolutePath()
        );

        future.complete(fallback.getAbsolutePath());
        return future;
    }

    /**
     * Folder picker fallback for Android launchers.
     *
     * @author WhalterMC
     * @reason SDL Android folder picker may not be available.
     */
    @Overwrite
    public static CompletableFuture<String> openFolderDialog(
            String defaultPath
    ) {
        if (com.moulberry.flashback.utils.AsyncFileDialogs.hasDialog()) {
            return CompletableFuture.completedFuture(null);
        }

        File fallback = flashbackRedroided$getDefaultExportDir();

        Flashback.LOGGER.warn(
                "Using default export folder: {}",
                fallback.getAbsolutePath()
        );

        return CompletableFuture.completedFuture(
                fallback.getAbsolutePath()
        );
    }
}