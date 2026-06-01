package com.assembly.core.source.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Cache
{
    public enum CacheStorage
    {
        MEMORY, DISK
    }

    public enum CacheType
    {
        INTEGER, REAL, TEXT
    }

    String column() default "";

    CacheType type() default CacheType.TEXT;
}
