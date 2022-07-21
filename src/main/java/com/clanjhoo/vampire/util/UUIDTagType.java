package com.clanjhoo.vampire.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UUIDTagType implements PersistentDataType<long[], UUID> {
    public static final UUIDTagType TYPE = new UUIDTagType();

    @Override
    public @NotNull Class<long[]> getPrimitiveType() {
        return long[].class;
    }

    @Override
    public @NotNull Class<UUID> getComplexType() {
        return UUID.class;
    }

    @Override
    public @NotNull long[] toPrimitive(@NotNull UUID complex, @NotNull PersistentDataAdapterContext context) {
        return new long[]{complex.getMostSignificantBits(), complex.getLeastSignificantBits()};
    }

    @Override
    public @NotNull UUID fromPrimitive(@NotNull long[] primitive, @NotNull PersistentDataAdapterContext context) {
        return new UUID(primitive[0], primitive[1]);
    }
}
