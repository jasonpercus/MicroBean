package com.jasonpercus.microbean;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.function.Consumer;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.OS;
import com.jasonpercus.microbean.infrastructure.exception.ExceptionManager;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import com.jasonpercus.microbean.infrastructure.run.AppExecutor;
import com.jasonpercus.microbean.infrastructure.run.Banner;
import com.jasonpercus.microbean.infrastructure.run.Initializer;
import com.jasonpercus.microbean.infrastructure.run.Processor;

/**
 * Classe utilitaire principale pour le démarrage et la gestion du cycle de vie d'une application MicroBean.
 * <p>
 * Cette classe orchestre le bootstrap du framework : affichage de la bannière,
 * initialisation du contexte, traitement des classes détectées puis exécution
 * des points d'entrée applicatifs.
 * </p>
 * <p>
 * Elle fournit également des méthodes utilitaires pour activer le mode debug,
 * forcer l'OS courant utilisé par MicroBean et récupérer le profil actif.
 * </p>
 */
public class MicroBean {

    /**
     * Nom de la propriété système permettant d'activer le mode debug de MicroBean.
     */
    public static final String PROPERTY_MICROBEAN_DEBUG = "microbean.debug";

    /**
     * Nom de la propriété système permettant de forcer l'OS utilisé par MicroBean.
     */
    public static final String PROPERTY_MICROBEAN_OS = "microbean.os";

    /**
     * Contexte global de l'application, accessible après l'initialisation.
     */
    static Context context;

    /**
     * Démarre l'application MicroBean avec la classe principale spécifiée, les arguments et les points d'entrée.
     * <p>
     * Cette méthode est une surcharge qui ne prend pas de {@link Consumer} pour le contexte.
     * Elle délègue à {@link #run(Class, Consumer, String[], Class[])} avec un consommateur nul.
     * </p>
     *
     * @param appClass      la classe principale de l'application
     * @param args          les arguments de la ligne de commande
     * @param appEntryPoint les classes des points d'entrée de l'application
     * @return le {@link Context} initialisé de l'application
     */
    @SafeVarargs
    @SuppressWarnings("unused")
    public static Context run(Class<?> appClass,
                              String[] args,
                              Class<? extends ApplicationEntryPoint>... appEntryPoint) {
        return run(appClass, null, args, appEntryPoint);
    }

    /**
     * Démarre l'application MicroBean avec la classe principale, un consommateur de contexte, les arguments et les points d'entrée.
     * <p>
     * La séquence exécutée est la suivante :
     * </p>
     * <ol>
     *   <li>affichage de la bannière via {@link Banner}</li>
     *   <li>initialisation du runtime via {@link Initializer}</li>
     *   <li>traitement des classes détectées via {@link Processor}</li>
     *   <li>chargement et exécution des entry points via {@link AppExecutor}</li>
     * </ol>
     *
     * @param appClass        la classe principale de l'application
     * @param contextConsumer un consommateur permettant d'agir sur le {@link Context} après initialisation (peut être {@code null})
     * @param args            les arguments de la ligne de commande
     * @param appEntryPoint   les classes des points d'entrée de l'application
     * @return le {@link Context} initialisé de l'application
     */
    @SafeVarargs
    public static Context run(Class<?> appClass,
                              Consumer<Context> contextConsumer,
                              String[] args,
                              Class<? extends ApplicationEntryPoint>... appEntryPoint) {

        Banner.show(appClass);

        Initializer initializer = Initializer.init(appClass, args, appEntryPoint);

        context = initializer.getContext();

        Processor.execute(initializer.getClasses(), context, args);

        AppExecutor.loadAndExecuteEntryPointServices(contextConsumer, args, appEntryPoint, context);

        return context;
    }

    /**
     * Active ou désactive le mode debug de MicroBean.
     *
     * @param enabled {@code true} pour activer le debug, {@code false} pour le désactiver
     */
    public static void setEnabledDebugMicroBean(boolean enabled) {
        System.setProperty(PROPERTY_MICROBEAN_DEBUG, Boolean.toString(enabled));
    }

    /**
     * Indique si le mode debug de MicroBean est activé.
     *
     * @return {@code true} si le debug est activé, {@code false} sinon
     */
    public static boolean isEnabledDebugMicroBean() {
        return "true".equalsIgnoreCase(System.getProperty(PROPERTY_MICROBEAN_DEBUG));
    }

    /**
     * Force l'OS courant utilisé par MicroBean.
     * <p>
     * Cette méthode permet d'outrepasser la détection automatique de l'OS,
     * notamment pour les tests ou pour simuler un environnement cible.
     * </p>
     *
     * @param os OS à utiliser, ou {@code null} pour effacer l'override
     */
    public static void setCurrentOS(OS os) {
        if (os == null)
            clearCurrentOS();
        else
            System.setProperty(PROPERTY_MICROBEAN_OS, os.name());
    }

    /**
     * Supprime l'override explicite de l'OS courant utilisé par MicroBean.
     * <p>
     * Après cet appel, MicroBean revient à son mécanisme normal de détection
     * de l'OS courant.
     * </p>
     */
    public static void clearCurrentOS() {
        System.clearProperty(PROPERTY_MICROBEAN_OS);
    }

    /**
     * Retourne le profil actif de l'application, tel que défini par la propriété système {@code app.profile}.
     *
     * @return le nom du profil actif, ou {@code null} si non défini
     */
    public static String getActiveProfile() {
        return System.getProperty("app.profile");
    }

    /**
     * Retourne le contexte global de l'application.
     *
     * @return le {@link Context} de l'application
     * @throws IllegalStateException si le contexte n'est pas encore initialisé (c'est-à-dire si {@link #run} n'a pas encore été appelé)
     */
    public static Context getContext() {

        if (context == null)
            throw ExceptionManager.contextIsNotInitialized();

        return context;
    }
}
