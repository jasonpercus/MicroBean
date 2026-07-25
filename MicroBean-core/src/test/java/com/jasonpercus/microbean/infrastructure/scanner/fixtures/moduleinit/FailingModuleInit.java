package com.jasonpercus.microbean.infrastructure.scanner.fixtures.moduleinit;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.annotation.Annotation;
import java.util.Set;
import com.jasonpercus.microbean.infrastructure.api.IModuleInit;
import com.jasonpercus.microbean.infrastructure.api.ModuleInit;

/**
 * Fixture : module d'initialisation dont le constructeur lève une exception.
 * Le scanner doit absorber l'erreur sans propagation.
 */
@ModuleInit
public class FailingModuleInit implements IModuleInit {

    public FailingModuleInit() {
        throw new RuntimeException("Échec volontaire du constructeur pour les tests");
    }

    @Override
    public void keepAnnotatedClassForContext(Set<Class<? extends Annotation>> clazz) {
        clazz.add(CustomComponentAnnotation.class);
    }
}
