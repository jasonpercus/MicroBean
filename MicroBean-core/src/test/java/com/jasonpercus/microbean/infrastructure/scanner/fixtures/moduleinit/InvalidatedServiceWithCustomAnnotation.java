package com.jasonpercus.microbean.infrastructure.scanner.fixtures.moduleinit;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.api.Service;

/**
 * Fixture : classe annotée avec {@link Service} ET {@link CustomComponentAnnotation},
 * avec un profil non actif.
 * Lorsque le profil ne correspond pas, {@code ScanningValidator} l'invalide.
 * Si {@link ValidModuleInit} a exposé {@link CustomComponentAnnotation}, la classe doit
 * se retrouver dans {@code otherClasses} plutôt qu'être ignorée.
 */
@Service
@CustomComponentAnnotation
@Profile({"module-test-profile"})
public class InvalidatedServiceWithCustomAnnotation {
}
