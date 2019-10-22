package com.mac.common.operation;

public class ReflectOperation {

    public static Object createObjectByClassName(String classname) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> object = Class.forName(classname);
        return object.newInstance();
    }
}
