package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Service
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerRest {

    /**
     * Définit un nom explicite pour le bean controller.
     *
     * <p>Si vide, le nom peut être déduit par le conteneur selon sa stratégie
     * interne de nommage.</p>
     *
     * @return le nom du bean controller ; chaîne vide par défaut.
     */
    String name() default "";
}
