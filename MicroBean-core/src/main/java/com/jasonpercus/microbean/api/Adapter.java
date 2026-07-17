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
 * Indique qu'une classe est un adaptateur géré par le conteneur d'injection de dépendances.
 * <p>
 * Cette annotation permet de déclarer une classe comme composant d'adaptation,
 * pouvant être instanciée et injectée automatiquement selon le contexte d'exécution.
 * <p>
 * Elle permet également de restreindre l'activation de l'adaptateur à un ou plusieurs systèmes d'exploitation spécifiques.
 * <p>
 * <b>Exemple d'utilisation :</b>
 * <pre>{@code
 * @Adapter(name = "fileAdapter", scope = Scope.PROTOTYPE, os = OS.WINDOWS)
 * public class FileWindowsAdapter {
 *     // ...
 * }
 * }
 * </pre>
 *
 * @see Scope
 * @see OS
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Adapter {

    /**
     * Définit le scope (cycle de vie) de l'adaptateur.
     * <p>
     * Par défaut, {@link Scope#SINGLETON}.
     *
     * @return le scope de l'adaptateur
     */
    Scope scope() default Scope.SINGLETON;

    /**
     * Définit le nom explicite de l'adaptateur.
     * <p>
     * Si vide, le nom sera déduit automatiquement.
     *
     * @return le nom de l'adaptateur
     */
    String name() default "";

    /**
     * Spécifie le ou les systèmes d'exploitation pour lesquels l'adaptateur est actif.
     * <p>
     * Par défaut, {@link OS#ALL} (tous les systèmes).
     *
     * @return le système d'exploitation ciblé
     */
    OS[] os() default OS.ALL;
}
