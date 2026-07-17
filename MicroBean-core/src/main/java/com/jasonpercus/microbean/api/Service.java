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

/**
 * Indique qu'une classe est un service géré par le conteneur d'injection de dépendances.
 * <p>
 * Cette annotation permet de déclarer une classe comme composant métier ou service,
 * afin qu'elle soit automatiquement détectée, instanciée et injectée là où nécessaire.
 * <p>
 * <b>Exemple d'utilisation :</b>
 * <pre>{@code
 * @Service(name = "monService", scope = Scope.PROTOTYPE)
 * public class MonService {
 *     // ...
 * }
 * }
 * </pre>
 *
 * @see Scope
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {

    /**
     * Définit le scope (cycle de vie) du service.
     * <p>
     * Par défaut, {@link Scope#SINGLETON}.
     *
     * @return le scope du service
     */
    Scope scope() default Scope.SINGLETON;

    /**
     * Définit le nom explicite du service.
     * <p>
     * Si vide, le nom sera déduit automatiquement.
     *
     * @return le nom du service
     */
    String name() default "";
}
