# Flashback Redroided

Android compatibility patches for "[Flashback](https://modrinth.com/mod/flashback)".

Flashback Redroided is a small addon that patches parts of Flashback that don't work properly on Android Minecraft launchers.

### Features

- Android-compatible ImGui bindings
- ImGui package remapping
- Android FFmpeg support
- Android-friendly file exporting
- Removes/replaces desktop-only functionality where needed
- Fabric mixins instead of modifying the original Flashback jar

### Installation

1. Install Fabric for Minecraft 26.3.
2. Install Fabric API.
3. Install the original Flashback mod.
4. Download the matching Flashback Redroided version.
5. Put both ".jar" files in your "mods" folder.
6. Start Minecraft using your Android launcher.

**Flashback Redroided requires [Flashback](https://modrinth.com/mod/flashback). It does not replace [Flashback](https://modrinth.com/mod/flashback).**

### Android Launchers

It is mainly made for Android Minecraft launchers such as:

- Mojo/MJLauncher
- Amethyst Launcher
- ZalithLauncher2
- Etc.

Compatibility can vary depending on the launcher, LWJGL build and renderer.

### Exports

Android doesn't have the same desktop file-dialog support used by Flashback.

Flashback Redroided changes the export handling so files can be saved directly to the Minecraft game directory.

Exports are normally stored in:

flashback/exports/

### Why?

Some parts of Flashback rely on desktop libraries or functionality that isn't available on Android.

Instead of modifying and redistributing the original Flashback jar, this project uses Fabric mixins and additional compatibility code to patch those parts at runtime.

### Credits

[Flashback](https://modrinth.com/mod/flashback)
Created by "Moulberry".

Flashback Redroided is an unofficial project and is not affiliated with Flashback or Moulberry unless explicitly stated.

Please download Flashback separately and follow its license.

### Links

- Website: https://whaltermc.vercel.app/flashback-redroided
- Source: https://github.com/whaltermc/flashback-redroided
- Flashback: https://modrinth.com/mod/flashback

### Issues

If something doesn't work, open an issue and include:

- Minecraft version
- Flashback version
- Flashback Redroided version
- Fabric Loader version
- Android device
- Launcher
- Renderer
- Crash log / "latest.log"

### License

Flashback Redroided's source code is licensed separately from Flashback.

See the repository license for the terms applying to this project.

Flashback Redroided — Android support for [Flashback](https://modrinth.com/mod/flashback).
