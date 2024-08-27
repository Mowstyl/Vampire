package com.clanjhoo.vampire.compat;

import com.clanjhoo.vampire.util.SemVer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class VersionHandler {

    private static final SemVer minVersion = new SemVer(1, 18);


    @NotNull
    public static AbstractVersionCompat getVersionCompat(@NotNull SemVer currentVersion) {
        if (currentVersion.compareTo(minVersion) < 0) {
            throw new IllegalArgumentException("The minimum supported version is " + minVersion);
        }
        String versionName = getVersionName(currentVersion);

        AbstractVersionCompat versionCompat;
        try {
            versionCompat = (AbstractVersionCompat) Class.forName("com.clanjhoo.vampire.compat." + versionName + ".VersionCompat").getConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException | ClassCastException e) {
            throw new IllegalArgumentException("There was an error while loading methods for version " + currentVersion);
        }
        return versionCompat;
    }

    @NotNull
    private static String getVersionName(@NotNull SemVer currentVersion) {
        String versionName;
        if (currentVersion.compareTo(new SemVer(1, 20, 2)) < 0)
            versionName = "v1_18";
        else if (currentVersion.compareTo(new SemVer(1, 20, 3)) < 0)
            versionName = "1_20_2";
        else if (currentVersion.compareTo(new SemVer(1, 20, 5)) < 0)
            versionName = "1_20_3";
        else if (currentVersion.compareTo(new SemVer(1, 20, 6)) < 0)
            versionName = "1_20_5";
        else if (currentVersion.compareTo(new SemVer(1, 21)) < 0)
            versionName = "1_20_6";
        else
            versionName = "1_21";
        return versionName;
    }
}
