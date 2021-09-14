package com.qiwenshare.file.util;

import java.util.HashMap;
import java.util.Map;

public class SessionUtil {
    private static ThreadLocal<Map<String, Object>> resource = new InheritableThreadLocal<>();
    public static void setSession(Object o) {
        Map<String, Object> map = new HashMap<>();
        map.put("session", o);
        resource.set(map);
    }
    public static Object getSession(){
        Map<String, Object> map = resource.get();
        if (map == null) {
            return null;
        }
        return map.get("session");
    }
}
