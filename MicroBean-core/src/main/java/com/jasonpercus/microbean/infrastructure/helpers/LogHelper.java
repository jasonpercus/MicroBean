package com.jasonpercus.microbean.infrastructure.helpers;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.MicroBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

/**
 * Classe utilitaire pour la gestion des logs dans le framework.
 */
public class LogHelper {

    /**
     * Logger SLF4J utilisé pour les logs du framework. Si aucun logger n'est configuré, un NOPLogger est utilisé.
     */
    private static final Logger log = LoggerFactory.getLogger("MicroBean");

    /**
     * Indique si le logger est actif (c'est-à-dire s'il n'est pas un NOPLogger).
     */
    private static final boolean active = !(log instanceof NOPLogger);

    /**
     * Affiche une bannière de log, généralement utilisée pour marquer le début d'une section importante dans les logs.
     *
     * @param banner le texte de la bannière à afficher
     */
    public static void banner(String banner) {
        if (active) {
            log.info("\n{}", banner);
        } else
            System.out.println(banner);
    }

    /**
     * Affiche un message de log de niveau trace.
     *
     * @param message le message de log à afficher
     * @param args    les arguments à formater dans le message
     */
    public static void trace(String message, Object... args) {

        String formattedMessage = message.formatted(args);

        if (active)
            log.trace(formattedMessage);
        else
            System.out.println(formattedMessage);
    }

    /**
     * Affiche un message de log de niveau info.
     *
     * @param message le message de log à afficher
     * @param args    les arguments à formater dans le message
     */
    public static void info(String message, Object... args) {
        String formattedMessage = message.formatted(args);

        if (active)
            log.info(formattedMessage);
        else
            System.out.println(formattedMessage);
    }

    /**
     * Affiche un message de log de niveau debug si le mode debug est activé.
     *
     * @param message le message de log à afficher
     * @param args    les arguments à formater dans le message
     */
    public static void debug(String message, Object... args) {
        if (MicroBean.isEnabledDebugMicroBean()) {

            String formattedMessage = message.formatted(args);

            if (active)
                log.debug(formattedMessage);
            else
                System.out.println(formattedMessage);
        }
    }

    /**
     * Affiche un message de log de niveau warn.
     *
     * @param message le message de log à afficher
     * @param args    les arguments à formater dans le message
     */
    public static void warn(String message, Object... args) {
        String formattedMessage = message.formatted(args);

        if (active)
            log.warn(formattedMessage);
        else
            System.out.println(formattedMessage);
    }

    /**
     * Affiche un message de log de niveau error. Si un Throwable est fourni, il est également affiché.
     *
     * @param message le message de log à afficher
     * @param t       le Throwable associé à l'erreur (peut être null)
     */
    public static void error(String message, Throwable t) {
        if (active)
            log.error(message, t);
        else if (t != null)
            t.printStackTrace(System.err);
        else
            System.err.println(message);
    }
}
