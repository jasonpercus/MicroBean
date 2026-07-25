package com.jasonpercus.microbean.infrastructure.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.jasonpercus.microbean.api.Scope;
import com.jasonpercus.microbean.api.Service;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface HttpServer {

    Scope scope() default Scope.PROTOTYPE;

    String name();
}
