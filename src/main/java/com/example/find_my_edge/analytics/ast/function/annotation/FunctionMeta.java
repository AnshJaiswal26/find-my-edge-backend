package com.example.find_my_edge.analytics.ast.function.annotation;

import com.example.find_my_edge.analytics.ast.function.enums.FunctionMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FunctionMeta {

    String[] argTypes() default {};

    String returnType() default "any";

    ArgType[] semanticArgs() default {};

    String semanticReturn() default "any";

    String signature() default "";

    String description() default "";

    FunctionMode[] modes() default { FunctionMode.BASE };
}