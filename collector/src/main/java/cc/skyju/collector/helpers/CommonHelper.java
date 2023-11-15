package cc.skyju.collector.helpers;

import javax.annotation.Nullable;

public class CommonHelper {
    public static <T> @Nullable T firstOrNull(T... args) {
        for (T arg : args) {
            if (arg != null) {
                return arg;
            }
        }
        return null;
    }
}
