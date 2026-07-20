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
 * Annotation principale pour déclarer une application MicroBean.
 * <p>
 * Cette annotation doit être placée sur la classe principale de l'application (généralement la classe contenant le main).
 * Elle permet de configurer le comportement du framework MicroBean, notamment les packages à scanner pour la détection automatique des composants,
 * l'affichage d'une bannière de démarrage, et la ressource à utiliser pour cette bannière.
 * </p>
 *
 * <b>Exemple d'utilisation :</b>
 * <pre>
 * {@code
 * @MicroBeanApplication(
 *     scanPackages = {"com.example.app", "com.example.lib"},
 *     showBanner = true,
 *     bannerResource = "custom-banner.txt"
 * )
 * public class Application {
 *     public static void main(String[] args) {
 *         // ...
 *     }
 * }
 * }
 * </pre>
 *
 * @see com.jasonpercus.microbean.api.EntryPointService
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MicroBeanApplication {

    /**
     * Liste des packages à scanner pour la détection automatique des composants (beans, services, etc.).
     * Si vide, le package de la classe annotée sera utilisé par défaut.
     *
     * @return les noms de packages à scanner
     */
    String[] scanPackages() default {};

    /**
     * Indique si la bannière de démarrage doit être affichée lors du lancement de l'application.
     *
     * @return {@code true} pour afficher la bannière, {@code false} sinon
     */
    boolean showBanner() default true;

    /**
     * Nom de la ressource (dans le classpath) à utiliser pour la bannière de démarrage.
     *
     * @return le nom de la ressource de la bannière
     */
    String bannerResource() default "banner.txt";

    /**
     * Tableau de chemins de fichiers de configuration (yaml, yml, json) à charger au démarrage de l'application.
     *
     * @return les chemins des fichiers de configuration dans le classpath
     */
    String[] configurationProperties() default {};
}
