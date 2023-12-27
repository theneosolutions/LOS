package com.seulah.los.request;

import java.util.HashMap;
import java.util.Map;

public class TenureManager {
    private static final Map<String, Tenure> tenureMap = new HashMap<>();

    public static void addTenure(String key) {
        tenureMap.put(key, new Tenure(key));
    }

    public static Tenure getTenure(String key) {
        return tenureMap.get(key);
    }

    public static class Tenure {
        private final String key;

        private Tenure(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}