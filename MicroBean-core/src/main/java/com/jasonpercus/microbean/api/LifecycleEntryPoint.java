package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

/**
 * Enumération définissant les types de cycle de vie pour un point d'entrée de service dans une application MicroBean.
 * <p>
 * Cette énumération est utilisée par l'annotation {@link EntryPointService} pour indiquer comment le service doit être exécuté.
 * </p>
 *
 * <ul>
 *   <li>{@link #ONE_SHOT} : Exécution sur le thread courant. Implique qu'il ne peut y avoir qu'un seul EntryPointService de ce type.</li>
 *   <li>{@link #LONG_RUNNING} : Exécution sur un thread dédié, adapté aux services de longue durée.</li>
 * </ul>
 *
 * <b>Exemple d'utilisation :</b>
 * <pre>
 * {@code
 * @EntryPointService(lifecycle = LifecycleEntryPoint.LONG_RUNNING)
 * public class BackgroundService {
 *     // ...
 * }
 * }
 * </pre>
 *
 * @see EntryPointService
 */
@SuppressWarnings("unused")
public enum LifecycleEntryPoint {

    /**
     * Exécution sur le thread courant (un seul EntryPointService de ce type autorisé).
     */
    ONE_SHOT,

    /**
     * Exécution sur un thread dédié, pour les services de longue durée.
     */
    LONG_RUNNING
}
