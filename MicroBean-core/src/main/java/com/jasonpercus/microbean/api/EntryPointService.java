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
 * Annotation pour déclarer un service comme point d'entrée dans une application MicroBean.
 * <p>
 * Cette annotation doit être placée sur une classe de service qui sera considérée comme un point d'entrée
 * lors du démarrage de l'application MicroBean, selon le cycle de vie spécifié.
 * </p>
 *
 * <b>Exemple d'utilisation :</b>
 * <pre>
 * {@code
 * @EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
 * public class MainService {
 *     // ...
 * }
 * }
 * </pre>
 *
 * @see LifecycleEntryPoint
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntryPointService {

    /**
     * Définit le cycle de vie auquel le service doit être exécuté. Exemple :
     * <li>ONE_SHOT -> Execution sur le thread courant (ce qui implique l'existance
     * d'un seul EntryPointService de type ONE_SHOT)</li>
     * <li>LONG_RUNNING -> Execution sur un thread dédié</li>
     *
     * @return le type de cycle de vie
     */
    LifecycleEntryPoint lifecycle();
}
