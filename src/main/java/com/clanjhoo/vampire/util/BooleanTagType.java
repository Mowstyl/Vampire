package com.clanjhoo.vampire.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class BooleanTagType implements PersistentDataType<Byte, Boolean> {
    public static final BooleanTagType TYPE = new BooleanTagType();

    @Override
    public @NotNull Class<Byte> getPrimitiveType() {
        return Byte.class;
    }

    @Override
    public @NotNull Class<Boolean> getComplexType() {
        return Boolean.class;
    }

    @Override
    public @NotNull Byte toPrimitive(@NotNull Boolean complex, @NotNull PersistentDataAdapterContext context) {
        return (byte) (complex ? 1 : 0);
    }

    @Override
    public @NotNull Boolean fromPrimitive(@NotNull Byte primitive, @NotNull PersistentDataAdapterContext context) {
        return primitive != 0;
    }
}
