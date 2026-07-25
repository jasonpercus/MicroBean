package com.jasonpercus.microbean.infrastructure.scanner.fixtures.moduleinit;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Service;

/**
 * Fixture : classe annotée avec {@link Service} ET {@link CustomComponentAnnotation}.
 * Utilisée pour vérifier que le scanner place les classes annotées par des annotations
 * déclarées via {@link ValidModuleInit#keepAnnotatedClassForContext} dans {@code otherClasses}
 * lorsque leur profil ou condition les invalide.
 * Cette classe est concrète et valide du point de vue du scan ; elle atterrit normalement dans
 * {@code componentClasses}.
 */
@Service
@CustomComponentAnnotation
public class ServiceWithCustomAnnotation {

}
