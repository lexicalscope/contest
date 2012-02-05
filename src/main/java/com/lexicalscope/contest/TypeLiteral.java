package com.lexicalscope.contest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeLiteral<T> {
    private final Type type;
    private volatile Class<?> rawType;

    protected TypeLiteral() {
        final Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked") public Class<T> getRawType() {
        if (rawType == null) {
            this.rawType = type instanceof Class<?>
                    ? (Class<?>) type
                    : (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return (Class<T>) rawType;
    }

    public Type getType() {
        return this.type;
    }
}