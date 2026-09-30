package com.whaltermc;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

public final class FlashbackTransformer {

    private static final String FROM = "imgui/moulberry90/";
    private static final String TO = "imgui/moulberry92/";

    private FlashbackTransformer() {}

    public static byte[] transform(byte[] classBytes) {
        if (classBytes == null || classBytes.length == 0) {
            return classBytes;
        }

        if (!contains(classBytes, FROM)) {
            return classBytes;
        }

        ClassReader reader = new ClassReader(classBytes);
        ClassWriter writer = new ClassWriter(reader, 0);

        Remapper remapper = new Remapper() {

            @Override
            public String map(String internalName) {
                if (internalName == null) {
                    return null;
                }

                if (internalName.startsWith(FROM)) {
                    return TO + internalName.substring(FROM.length());
                }

                return internalName;
            }

            @Override
            public Object mapValue(Object value) {
                if (value instanceof String s) {
                    if (s.contains("imgui.moulberry90")
                            || s.contains("imgui/moulberry90")) {

                        return s
                                .replace(
                                        "imgui.moulberry90",
                                        "imgui.moulberry92"
                                )
                                .replace(
                                        "imgui/moulberry90",
                                        "imgui/moulberry92"
                                );
                    }
                }

                return super.mapValue(value);
            }
        };

        ClassRemapper visitor =
                new ClassRemapper(writer, remapper);

        reader.accept(visitor, 0);

        return writer.toByteArray();
    }

    private static boolean contains(
            byte[] data,
            String ascii
    ) {
        byte[] needle =
                ascii.getBytes(
                        java.nio.charset.StandardCharsets.US_ASCII
                );

        outer:
        for (
                int i = 0;
                i <= data.length - needle.length;
                i++
        ) {
            for (
                    int j = 0;
                    j < needle.length;
                    j++
            ) {
                if (data[i + j] != needle[j]) {
                    continue outer;
                }
            }

            return true;
        }

        return false;
    }
}