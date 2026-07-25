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
 * Fixture : module d'initialisation valide qui expose une annotation à conserver.
 */
@ModuleInit
public class ValidModuleInit implements IModuleInit {

    @Override
    public void keepAnnotatedClassForContext(Set<Class<? extends Annotation>> clazz) {
        clazz.add(CustomComponentAnnotation.class);
    }
}
