package com.jasonpercus.microbean.infrastructure.helpers;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.reflect.Method;

/**
 * Classe utilitaire pour la manipulation de chaînes de caractères dans le framework.
 */
public class StringHelper {

    /**
     * Abrège le nom complet d'une classe en utilisant les initiales des packages et le nom de la classe.
     * <p>
     * Par exemple, "com.example.MyClass" devient "c.e.MyClass".
     *
     * @param beanClass la classe dont le nom doit être abrégé
     * @return le nom abrégé de la classe
     */
    public static String abbreviateClassName(Class<?> beanClass) {

        String fullName = beanClass.getName();
        String[] parts = fullName.split("\\.");

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parts.length - 1; i++)
            sb.append(parts[i].charAt(0)).append('.');

        sb.append(parts[parts.length - 1]);

        return sb.toString();
    }

    /**
     * Abrège le nom d'une méthode en utilisant l'abréviation de la classe et le nom de la méthode.
     * <p>
     * Par exemple, pour la classe "com.example.MyClass" et la méthode "myMethod", le résultat sera "c.e.MyClass#myMethod".
     *
     * @param beanClass la classe dont le nom doit être abrégé
     * @param method    la méthode dont le nom doit être abrégé
     * @return le nom abrégé de la méthode
     */
    public static String abbreviateMethodName(Class<?> beanClass, Method method) {
        return abbreviateClassName(beanClass) + "#" + method.getName();
    }
}
