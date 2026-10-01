package com.whaltermc.mixin;

import com.moulberry.flashback.Flashback;
import com.moulberry.flashback.exporting.AsyncFileDialogs;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Mixin(targets = "com.moulberry.flashback.exporting.AsyncFileDialogs", remap = false)
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

    /**
     * Android fallback version of Flashback's save dialog.
     *
     * @author WhalterMC
     * @reason Native File Dialog (NFD) has no Android backend in Pojav/MJ/Zalith.
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
        if (AsyncFileDialogs.hasDialog()) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<String> future = new CompletableFuture<>();

        String autoExtension =
                filters.length == 1 ? filters[0] : null;

        // A launcher without a native dialog backend can't show a picker,
        // so immediately use the default export directory instead.
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
     * @reason Native File Dialog (NFD) has no Android backend in Pojav/MJ/Zalith.
     */
    @Overwrite
    public static CompletableFuture<String> openFolderDialog(
            String defaultPath
    ) {
        if (AsyncFileDialogs.hasDialog()) {
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
