package com.clanjhoo.vampire.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static List<File> searchJson(final File folder) {
        List<File> result = null;
        File[] files;

        if (folder != null && folder.isDirectory() && (files = folder.listFiles()) != null && files.length > 0) {
            result = new ArrayList<>();

            for (final File f : files) {
                if (f != null && f.isFile() && f.getName().matches(".*\\.json")) {
                    result.add(f);
                }

            }
        }

        return result;
    }

    public static String stripExtension (String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        return str.substring(0, pos);
    }
}
