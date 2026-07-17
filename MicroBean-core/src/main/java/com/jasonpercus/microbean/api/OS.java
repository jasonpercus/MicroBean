package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

/**
 * Enumère les systèmes d'exploitation supportés pour la configuration conditionnelle des {@code Adapter}.
 * <p>
 * Cette énumération permet de cibler l'exécution ou l'activation de composants selon le système d'exploitation.
 * <p>
 * <b>Utilisation typique :</b>
 * <pre>{@code
 * @Adapter(os = OS.WINDOWS)
 * public class MonAdapter {
 *     // ...
 * }
 * }
 * </pre>
 */
public enum OS {

    /**
     * Tous les systèmes d'exploitation.
     */
    ALL,

    /**
     * Système d'exploitation Microsoft Windows.
     */
    WINDOWS,

    /**
     * Système d'exploitation Linux.
     */
    LINUX,

    /**
     * Système d'exploitation macOS.
     */
    MAC,

    /**
     * Système d'exploitation non reconnu ou non détectable.
     */
    UNKNOWN
}
