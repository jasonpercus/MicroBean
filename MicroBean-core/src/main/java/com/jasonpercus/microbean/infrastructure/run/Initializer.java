package com.jasonpercus.microbean.infrastructure.run;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.PACKAGE_ENTRYPOINTS;
import static com.jasonpercus.microbean.infrastructure.Constants.PACKAGE_SERVICES;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.Environment;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.infrastructure.exception.ExceptionManager;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper;
import com.jasonpercus.microbean.infrastructure.scanner.ClassScanner;

/**
 * Initialise les éléments de base du runtime MicroBean.
 * <p>
 * Cette classe vérifie d'abord la validité des paramètres de démarrage,
 * détermine les packages à scanner puis prépare un {@link Context} et la liste
 * des classes annotées détectées.
 */
public class Initializer {

    /**
     * Classe principale de l'application.
     */
    private final Class<?> appClass;

    /**
     * Arguments de la ligne de commande transmis au runtime.
     */
    private final String[] args;

    /**
     * Classes de type {@link ApplicationEntryPoint} déclarées au démarrage.
     */
    private final Class<? extends ApplicationEntryPoint>[] appEntryPoint;

    /**
     * Contexte d'exécution créé lors de l'initialisation.
     */
    private Context context;

    /**
     * Ensemble des classes annotées trouvées pendant le scan.
     */
    private Set<Class<?>> classes;

    /**
     * Construit un initialiseur avec les paramètres de démarrage.
     *
     * @param appClass      classe principale de l'application
     * @param args          arguments de la ligne de commande
     * @param appEntryPoint classes EntryPoint à valider
     */
    private Initializer(Class<?> appClass, String[] args, Class<? extends ApplicationEntryPoint>[] appEntryPoint) {
        this.appClass = appClass;
        this.args = args;
        this.appEntryPoint = appEntryPoint;
    }

    /**
     * Retourne le contexte d'exécution initialisé.
     *
     * @return contexte runtime courant
     */
    public Context getContext() {
        return context;
    }

    /**
     * Retourne l'ensemble des classes annotées détectées.
     *
     * @return classes scannées
     */
    public Set<Class<?>> getClasses() {
        return classes;
    }

    /**
     * Point d'entrée utilitaire pour créer et exécuter un {@link Initializer}.
     *
     * @param appClass      classe principale de l'application
     * @param args          arguments de la ligne de commande
     * @param appEntryPoint classes EntryPoint à valider
     * @return instance initialisée et prête à être consommée
     */
    public static Initializer init(Class<?> appClass, String[] args, Class<? extends ApplicationEntryPoint>[] appEntryPoint) {
        Initializer initializer = new Initializer(appClass, args, appEntryPoint);
        initializer.init();
        return initializer;
    }

    /**
     * Exécute la séquence complète d'initialisation.
     * <p>
     * Cette méthode valide les paramètres d'entrée, détermine les packages à
     * scanner, crée le contexte puis lance le scan des classes annotées.
     */
    void init() {

        checkParameters();

        String[] packages = getPackagesPathsToScan();

        Environment environment = new Environment(args);

        classes = new ClassScanner(packages, args).searchAnnotatedClass();

        context = new Context();
        context.registerSingleton(Environment.class, environment);
    }

    /**
     * Vérifie la cohérence des paramètres de démarrage.
     * <p>
     * Les erreurs sont levées via {@link ExceptionManager} si :
     * <ul>
     * <li>La classe application n'est pas annotée {@link MicroBeanApplication}</li>
     * <li>Aucune classe EntryPoint n'est fournie</li>
     * <li>La classe application est annotée {@code @EntryPointService}</li>
     * <li>Une classe EntryPoint n'est pas annotée {@code @EntryPointService}</li>
     * </ul>
     */
    void checkParameters() {

        if (AnnotationHelper.isNotAnnotatedWithMicroBeanApplication(appClass))
            throw ExceptionManager.missingMicroBeanApplicationOnClass(appClass);

        if (isNotEmptyEntryPoints(appEntryPoint))
            throw ExceptionManager.atLeastOneApplicationEntryPointClassMustBeProvided();

        if (AnnotationHelper.isAnnotatedEntryPointService(appClass))
            throw ExceptionManager.classShouldNotBeAnnotatedWithEntryPointService(appClass);

        for (Class<? extends ApplicationEntryPoint> entryPoint : appEntryPoint) {

            if (AnnotationHelper.isNotAnnotatedWithEntryPointService(entryPoint))
                throw ExceptionManager.missingEntryPointServiceOnClass(entryPoint);
        }
    }

    /**
     * Détermine les packages à scanner pour la détection des composants.
     * <p>
     * Si {@link MicroBeanApplication#scanPackages()} est renseigné, ces valeurs
     * sont utilisées. Sinon, le package de {@code appClass} est pris par défaut.
     *
     * @return chemins de packages à scanner
     */
    String[] getPackagesPathsToScan() {
        MicroBeanApplication annotation = appClass.getAnnotation(MicroBeanApplication.class);

        List<String> packages = new ArrayList<>();

        if (annotation.scanPackages().length > 0)
            packages.addAll(Arrays.asList(annotation.scanPackages()));
        else
            packages.add(appClass.getPackageName());

        if (!packages.contains(PACKAGE_ENTRYPOINTS))
            packages.add(PACKAGE_ENTRYPOINTS);

        if (!packages.contains(PACKAGE_SERVICES))
            packages.add(PACKAGE_SERVICES);

        return packages.toArray(new String[0]);
    }

    /**
     * Indique si la liste des entry points est absente ou vide.
     *
     * @param appEntryPoint tableau des classes EntryPoint à vérifier
     * @return {@code true} si le tableau est {@code null} ou vide, sinon {@code false}
     */
    static boolean isNotEmptyEntryPoints(Class<? extends ApplicationEntryPoint>[] appEntryPoint) {
        return appEntryPoint == null || appEntryPoint.length == 0;
    }
}
