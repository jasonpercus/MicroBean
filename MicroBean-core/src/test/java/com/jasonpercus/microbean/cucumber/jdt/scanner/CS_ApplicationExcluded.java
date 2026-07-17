package com.jasonpercus.microbean.cucumber.jdt.scanner;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.Set;
import com.jasonpercus.microbean.infrastructure.scanner.ClassScanner;

public class CS_ApplicationExcluded {

    public static void main(String[] args) {
        System.clearProperty("app.profile");

        ClassScanner scanner = new ClassScanner(
                new String[]{"com.jasonpercus.microbean.infrastructure.scanner.fixtures.excluded"},
                new String[0]
        );

        print(scanner.searchAnnotatedClass());
    }

    private static void print(Set<Class<?>> classes) {
        classes.stream()
                .map(Class::getSimpleName)
                .sorted()
                .forEach(name -> System.out.println("SCANNED:" + name));

        System.out.println("SCANNED_COUNT:" + classes.size());
    }
}
