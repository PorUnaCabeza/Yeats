package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Collections {

    private static final Logger logger = LoggerFactory.getLogger(Collections.class);

    @SafeVarargs
    public static <K, V> Map<K, V> newMap(Class<? extends Map>... clazz) {
        try {
            return clazz == null || clazz.length == 0 || clazz[0] == null ? new HashMap<>() : clazz[0].newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(K k1, V v1, Class<? extends Map>... clazz) {
        Map<K, V> map = newMap(clazz);
        map.put(k1, v1);
        return map;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, Class<? extends Map>... clazz) {
        Map<K, V> map = mapOf(k1, v1, clazz);
        map.put(k2, v2);
        return map;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3, Class<? extends Map>... clazz) {
        Map<K, V> map = mapOf(k1, v1, k2, v2, clazz);
        map.put(k3, v3);
        return map;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, Class<? extends Map>... clazz) {
        Map<K, V> map = mapOf(k1, v1, k2, v2, k3, v3, clazz);
        map.put(k4, v4);
        return map;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, Class<? extends Map>... clazz) {
        Map<K, V> map = mapOf(k1, v1, k2, v2, k3, v3, k4, v4, clazz);
        map.put(k5, v5);
        return map;
    }

    /**
     * @param clazz 构建Map的类型，默认为HashMap
     * @param ts    构建Map的参数列表
     * @param <T>   构建Map内的键值类型
     * @return 成功构建的 Map
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<T, T> mapOf(Class<? extends Map> clazz, T... ts) {
        Map<T, T> map = newMap(clazz);
        if (null == ts) return map;
        for (int i = 0, len = ts.length; i < len; i++) {
            map.put(ts[i], i == len - 1 ? null : ts[++i]);
        }
        return map;
    }

    @SafeVarargs
    public static <T> Map<T, T> mapOf(T... ts) {
        return mapOf(null, ts);
    }

    @SafeVarargs
    public static <T> Map asMap(Class<? extends Map> clazz, T... ts) {
        return mapOf(clazz, (Object[]) ts);
    }

    @SafeVarargs
    public static <T> Map asMap(T... ts) {
        return asMap(null, ts);
    }
}
