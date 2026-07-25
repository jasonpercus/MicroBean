package com.jasonpercus.microbean.infrastructure.scanner.fixtures.moduleinit;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.infrastructure.api.ModuleInit;

/**
 * Fixture : classe annotée {@link ModuleInit} mais n'implémentant pas {@link com.jasonpercus.microbean.infrastructure.api.IModuleInit}.
 * Le scanner doit l'ignorer silencieusement lors du traitement de {@code getOthersAnnotationsToKeep}.
 */
@ModuleInit
public class ModuleInitWithoutIModuleInit {

}
