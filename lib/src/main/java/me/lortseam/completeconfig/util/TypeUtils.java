package me.lortseam.completeconfig.util;

import com.google.common.reflect.TypeToken;
import io.leangen.geantyref.GenericTypeReflector;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

@UtilityClass
public final class TypeUtils {

    public static Type getFieldType(Field field) {
        return GenericTypeReflector.getExactFieldType(field, field.getDeclaringClass());
    }

    public static Class<?> getTypeClass(Type type) {
        return TypeToken.of(type).getRawType();
    }

}