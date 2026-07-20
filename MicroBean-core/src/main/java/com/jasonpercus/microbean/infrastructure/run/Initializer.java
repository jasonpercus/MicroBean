package com.jasonpercus.microbean.infrastructure.run;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.PACKAGE_ENTRYPOINTS;
import static com.jasonpercus.microbean.infrastructure.Constants.PACKAGE_SERVICES;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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

        treatConfigurationProperties(environment);

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
     * Traite les propriétés de configuration à partir du fichier YAML/JSON.
     * <p>
     * Les propriétés sont chargées dans l'environnement et peuvent être
     * utilisées par les composants annotés.
     *
     * @param environment environnement d'exécution
     */
    void treatConfigurationProperties(Environment environment) {

        MicroBeanApplication microBeanApplication = appClass.getAnnotation(MicroBeanApplication.class);

        if (microBeanApplication.configurationProperties() != null && microBeanApplication.configurationProperties().length > 0) {

            String[] configurationProperties = microBeanApplication.configurationProperties();

            for (String path : configurationProperties) {

                if (path != null) {

                    URL url = checkConfigurationProperties(path);

                    ObjectMapper objectMapper = createObjectMapper(path);

                    Map<String, Object> map = deserializeToMap(url, objectMapper, path);

                    if (map != null && !map.isEmpty()) {

                        environment.putProperties(map);

                        for (Map.Entry<String, Object> entry : map.entrySet())
                            setupConfigurationProperties(environment, entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    /**
     * Recherche et crée chaque propriété de configuration dans l'environnement.
     *
     * @param environment environnement d'exécution
     * @param key         clé de la propriété
     * @param value       valeur de la propriété
     */
    @SuppressWarnings("unchecked")
    void setupConfigurationProperties(Environment environment, String key, Object value) {
        if (value instanceof Map) {

            Map<String, Object> nestedMap = (Map<String, Object>) value;

            for (Map.Entry<String, Object> entry : nestedMap.entrySet())
                setupConfigurationProperties(environment, key + "." + entry.getKey(), entry.getValue());

        } else environment.putProperty(key, value);
    }

    /**
     * Crée un {@link ObjectMapper} configuré pour la désérialisation des fichiers de configuration.
     * Si le fichier est au format YAML, un {@link YAMLFactory} est utilisé. Sinon, un {@link JsonFactory} est utilisé.
     *
     * @param path chemin du fichier de configuration
     * @return instance d'ObjectMapper configurée
     */
    ObjectMapper createObjectMapper(String path) {
        return (isJson(path)
                ? new ObjectMapper()
                : new ObjectMapper(new YAMLFactory()))
            .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
    }

    /**
     * Vérifie si le chemin et le type de configuration sont valides.
     *
     * @param path chemin du fichier de configuration
     * @return URL du fichier de configuration
     */
    URL checkConfigurationProperties(String path) {

        if (path == null || path.isEmpty())
            throw ExceptionManager.invalidPathForConfigurationProperties(path);

        URL url = appClass.getClassLoader().getResource(path);

        if (url == null)
            throw ExceptionManager.configurationPropertiesFileNotFound(path);

        if ( !(isYaml(path) || isJson(path)) )
            throw ExceptionManager.invalidFileExtensionForConfigurationProperties(path);

        return url;
    }

    /**
     * Vérifie si le chemin du fichier de configuration se termine par l'extension YAML.
     *
     * @param path chemin du fichier de configuration
     * @return {@code true} si le chemin se termine par ".yaml" ou ".yml", sinon {@code false}
     */
    private boolean isYaml(String path) {

        String pathLowerCase = path.toLowerCase();

        return pathLowerCase.endsWith(".yaml") || pathLowerCase.endsWith(".yml");
    }

    /**
     * Vérifie si le chemin du fichier de configuration se termine par l'extension JSON.
     *
     * @param path chemin du fichier de configuration
     * @return {@code true} si le chemin se termine par ".json", sinon {@code false}
     */
    private boolean isJson(String path) {
        return path.toLowerCase().endsWith(".json");
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

    /**
     * Désérialise le contenu d'un fichier de configuration en une Map.
     *
     * @param url          URL du fichier de configuration
     * @param objectMapper instance d'ObjectMapper pour la désérialisation
     * @param path         chemin du fichier de configuration (pour les messages d'erreur)
     * @return la Map désérialisée
     */
    static Map<String, Object> deserializeToMap(URL url, ObjectMapper objectMapper, String path) {
        try (InputStream inputStream = url.openStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            throw ExceptionManager.failedToLoadConfigurationProperties(path, e);
        }
    }
}
