package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yao
 * @version 1.0
 * @date 2024/7/23 - 23:09
 * @className AutoFill
 * @since 1.0
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

public @interface AutoFill {
    // 数据库操作类型
    OperationType value();

}
