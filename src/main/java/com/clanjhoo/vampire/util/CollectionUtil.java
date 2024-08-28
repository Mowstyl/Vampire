package com.clanjhoo.vampire.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CollectionUtil {
    @SafeVarargs
    public static <E> List<E> list(E... items) {
        List<E> lista = new LinkedList<>();

        if (items != null && items.length > 0) {
            Collections.addAll(lista, items);
        }

        return lista;
    }

    @SafeVarargs
    public static <E> Set<E> set(E... items) {
        Set<E> conjunto = new HashSet<>();

        if (items != null && items.length > 0) {
            Collections.addAll(conjunto, items);
        }

        return conjunto;
    }

    public static <K, V> Map<K, V> map() {
        return new HashMap<>();
    }

    public static <K, V> Map<K, V> map(@NotNull K key1, @NotNull V val1, Object... other) {
        Map<K, V> dict = new HashMap<>();

        dict.put(key1, val1);

        if (other != null && other.length > 0) {
            if (other.length % 2 == 0) {
                for (int i = 0; i < other.length; i+=2) {
                    try {
                        dict.put((K) other[i], (V) other[i+1]);
                    }
                    catch (ClassCastException ex) {
                        throw new IllegalArgumentException("Type mismatch. Syntax: map(K key1, V val1, K key2, V val2, ..., K keyN, V valN)");
                    }
                }
            }
            else {
                throw new IllegalArgumentException("Number of args mismatch. Syntax: map(K key1, V val1, K key2, V val2, ..., K keyN, V valN)");
            }
        }

        return dict;
    }
}
