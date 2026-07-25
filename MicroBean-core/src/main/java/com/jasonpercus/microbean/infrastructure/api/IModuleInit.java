package com.jasonpercus.microbean.infrastructure.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Contrat fonctionnel d'un module d'initialisation MicroBean.
 *
 * <p>
 * Les classes implémentant {@code IModuleInit} et annotées avec {@link ModuleInit} participent
 * à la configuration du contexte d'injection au moment du démarrage de l'application.
 * Elles permettent à un module externe ou interne d'indiquer au scanner quelles annotations
 * supplémentaires doivent être prise en compte lors de la résolution des classes non-composants.
 * </p>
 *
 * <p>
 * L'implémentation par défaut de {@link #keepAnnotatedClassForContext(Set)} ne fait rien :
 * elle peut être surchargée pour ajouter les annotations voulues à l'ensemble fourni.
 * </p>
 *
 * <h2>Mécanisme</h2>
 * <ol>
 *   <li>Au démarrage, {@link com.jasonpercus.microbean.infrastructure.scanner.ClassScanner} scanne les packages applicatifs.</li>
 *   <li>Il détecte les classes annotées avec {@link ModuleInit}.</li>
 *   <li>Pour chaque classe implémentant {@code IModuleInit}, il instancie la classe et appelle
 *       {@link #keepAnnotatedClassForContext(Set)}.</li>
 *   <li>L'ensemble résultant d'annotations est utilisé lors du filtrage des classes invalidées :
 *       si une classe possède l'une de ces annotations et est invalidée par le validateur,
 *       elle est transmise dans {@code otherClasses} du contexte.</li>
 * </ol>
 *
 * <h2>Exemple d'implémentation</h2>
 * <pre>{@code
 * @ModuleInit
 * public class SecurityModuleInit implements IModuleInit {
 *
 *     @Override
 *     public void keepAnnotatedClassForContext(Set<Class<? extends Annotation>> annotations) {
 *         annotations.add(SecurityFilter.class);
 *         annotations.add(AccessPolicy.class);
 *     }
 * }
 * }</pre>
 *
 * <h2>Règles</h2>
 * <ul>
 *   <li>La classe doit être concrète et posséder un constructeur public sans argument.</li>
 *   <li>Une exception levée dans le constructeur est journalisée ; le module est ignoré.</li>
 *   <li>L'ensemble {@code annotations} fourni en paramètre n'est jamais {@code null}.</li>
 * </ul>
 *
 * @see ModuleInit
 */
public interface IModuleInit {

    /**
     * Cette méthode est appelée pour permettre à un module d'ajouter des classes annotées à un ensemble de classes pour le contexte.
     *
     * @param clazz un ensemble de classes annotées que le module peut ajouter à l'ensemble pour le contexte
     */
    default void keepAnnotatedClassForContext(Set<Class<? extends Annotation>> clazz) {

    }
}
