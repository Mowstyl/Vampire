package com.clanjhoo.vampire.util;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtil {
    public static final Random random = new Random();

    public static <T extends Number> T limitNumber(T d, T min, T max) {
        if (d.doubleValue() < min.doubleValue()) {
            return min;
        }

        if (d.doubleValue() > max.doubleValue()) {
            return max;
        }

        return d;
    }

    public static long probabilityRound(double value) {
        long ret = (long) Math.floor(value);
        double probability = value % 1;
        if (random.nextDouble() < probability) ret += 1;
        return ret;
    }

    public static int probabilityRound(float value) {
        int ret = (int) Math.floor(value);
        float probability = value % 1;
        if (random.nextFloat() < probability) ret += 1;
        return ret;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // -------------------------------------------- //
    // EQUALSISH
    // -------------------------------------------- //

    public static boolean equals(@NotNull Object... objects) {
        boolean eq = true;

        if (objects.length % 2 == 0) {
            int index = 1;
            while (index < objects.length) {
                Object object1 = objects[index - 1];
                Object object2 = objects[index];

                if (!object1.equals(object2)) {
                    eq = false;
                    break;
                }

                index += 2;
            }
        } else {
            eq = false;
            throw new IllegalArgumentException("objects length not even");
        }

        return eq;
    }

    public static final double EQUALSISH_EPSILON = 0.0001;

    public static boolean equalsishObject(Object o1, Object o2) {
        boolean eq;

        if (o1 instanceof Number && o2 instanceof Number) {
            eq = equalsishNumber((Number) o1, (Number) o2);
        } else {
            eq = equals(o1, o2);
        }

        return eq;
    }

    public static boolean equalsishNumber(Number number1, Number number2) {
        boolean eq = false;

        if (number1 == null && number2 == null) {
            eq = true;
        }
        else if (number1 != null && number2 != null) {
            eq = Math.abs(number2.doubleValue() - number1.doubleValue()) < EQUALSISH_EPSILON;
        }

        return eq;
    }
}
