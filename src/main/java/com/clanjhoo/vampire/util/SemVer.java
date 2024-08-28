package com.clanjhoo.vampire.util;

public class SemVer implements Comparable<SemVer> {
    private final int major;
    private final int minor;
    private final int patch;

    public SemVer(int major) {
        this(major, 0, 0);
    }

    public SemVer(int major, int minor) {
        this(major, minor, 0);
    }

    public SemVer(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public int compareTo(SemVer other) {
        int result = this.major - other.major;
        if (result == 0)
            result = this.minor - other.minor;
        if (result == 0)
            result = this.patch - other.patch;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SemVer))
            return false;
        SemVer other = (SemVer) obj;
        return this.major == other.major && this.minor == other.minor && this.patch == other.patch;
    }

    @Override
    public int hashCode() {
        return toString().hashCode() + 6470;
    }

    @Override
    public String toString() {
        if (patch > 0)
            return major + "." + minor + "." + patch;
        return major + "." + minor;
    }
}
