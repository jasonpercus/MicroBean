package com.jasonpercus.microbean.infrastructure.api;

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

/**
 * Marque une classe comme module d'initialisation du framework MicroBean.
 *
 * <p>
 * Une classe annotée avec {@code @ModuleInit} est détectée lors du scan des packages
 * et peut implémenter {@link IModuleInit} pour participer à la configuration du contexte
 * d'injection. Elle permet notamment de déclarer des annotations supplémentaires dont les classes
 * portant ces annotations doivent être transmises au contexte (via
 * {@link IModuleInit#keepAnnotatedClassForContext(java.util.Set)}).
 * </p>
 *
 * <p>
 * Les classes annotées {@code @ModuleInit} sont traitées en **priorité** lors du scan,
 * avant les autres composants applicatifs. Elles ne sont pas enregistrées comme beans injectables
 * dans le conteneur.
 * </p>
 *
 * <h2>Règles d'utilisation</h2>
 * <ul>
 *   <li>La classe annotée doit être concrète (non interface, non abstraite).</li>
 *   <li>Si elle implémente {@link IModuleInit}, elle doit posséder un constructeur sans argument public.</li>
 *   <li>Si le constructeur lève une exception, l'erreur est journalisée et le module ignoré.</li>
 *   <li>Si la classe n'implémente pas {@link IModuleInit}, elle est silencieusement ignorée.</li>
 * </ul>
 *
 * <h2>Exemple</h2>
 * <pre>{@code
 * @ModuleInit
 * public class MyModuleInitializer implements IModuleInit {
 *
 *     @Override
 *     public void keepAnnotatedClassForContext(Set<Class<? extends Annotation>> annotations) {
 *         annotations.add(MyCustomAnnotation.class);
 *     }
 * }
 * }</pre>
 *
 * @see IModuleInit
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInit {

}
