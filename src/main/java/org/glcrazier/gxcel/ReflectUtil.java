package org.glcrazier.gxcel;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {

    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static Object set(Method method, Object object, Object ...args) {
        try {
            return method.invoke(object, args);
        } catch (Exception e) {
            //
            return null;
        }
    }

    public static Method getSetter(Class<?> clazz, Field field) {
        String setter = "set" + StringUtils.capitalize(field.getName());
        Method method = null;
        try {
            method = clazz.getMethod(setter, field.getType());
        } catch (NoSuchMethodException e) {
            //
        }
        return method;
    }
}
