package imgui.moulberry92.glfw;

/**
 * Stand-in for the class of the same name in the original (moulberry90) ImGui binding.
 *
 * Flashback on 1.21.11 uses a GLFW backend and references this class, but the ImGui 1.92
 * binding bundled with this mod has no "glfw" package. FlashbackTransformer rewrites
 * Flashback's references from imgui.moulberry90 to imgui.moulberry92, so this class has
 * to exist under the new name or Flashback would hit a NoClassDefFoundError.
 *
 * The only member Flashback calls hides a window from the Windows taskbar, which has
 * no meaning on Android, so it does nothing here.
 */
public final class ImGuiImplGlfwNative {

    private ImGuiImplGlfwNative() {}

    public static void win32hideFromTaskBar(long viewportPlatformHandleRaw) {
        // Windows-only. Intentionally a no-op.
    }
}
