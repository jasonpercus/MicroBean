package com.jasonpercus.microbean.infrastructure.exception;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

/**
 * Exception personnalisée de base pour le framework MicroBean.
 * <p>
 * Cette exception étend {@link RuntimeException} et permet de formater dynamiquement le message d'erreur
 * avec des arguments, facilitant ainsi la gestion des erreurs dans l'ensemble du framework.
 * </p>
 */
public class MicroBeanException extends RuntimeException {

    /**
     * Construit une nouvelle exception MicroBean avec un message formaté.
     *
     * @param message le message d'erreur, pouvant contenir des placeholders de format
     * @param args    les arguments à injecter dans le message formaté
     */
    public MicroBeanException(String message, Object... args) {
        super(message.formatted(args));
    }

    /**
     * Construit une nouvelle exception MicroBean avec une cause et un message formaté.
     *
     * @param cause   la cause initiale de l'exception
     * @param message le message d'erreur, pouvant contenir des placeholders de format
     * @param args    les arguments à injecter dans le message formaté
     */
    public MicroBeanException(Throwable cause, String message, Object... args) {
        super(message.formatted(args), cause);
    }

    /**
     * Construit une nouvelle exception MicroBean avec une cause uniquement.
     *
     * @param cause la cause initiale de l'exception
     */
    public MicroBeanException(Throwable cause) {
        super(cause);
    }
}
