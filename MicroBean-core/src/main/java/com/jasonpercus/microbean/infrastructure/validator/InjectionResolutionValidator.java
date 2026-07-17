package com.jasonpercus.microbean.infrastructure.validator;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.unresolvableInjectionPoint;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.getNamedValue;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isAnnotatedNamed;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isNotBeanMethod;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isNotComponentClass;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Set;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import com.jasonpercus.microbean.infrastructure.helpers.StringHelper;

/**
 * Valide en amont la résolvabilité des points d'injection d'un ensemble de classes.
 * <p>
 * Cette classe couvre deux cas:
 * </p>
 * <ul>
 *   <li>les paramètres des méthodes {@code @Bean} déclarées dans les classes {@code @Configuration};</li>
 *   <li>les paramètres du constructeur principal (celui avec le plus de paramètres) des classes composant.</li>
 * </ul>
 * <p>
 * Chaque dépendance est vérifiée auprès du {@link Context}. En cas d'échec, une exception enrichie
 * est levée avec la description précise du point d'injection concerné.
 * </p>
 */
public final class InjectionResolutionValidator {

    /**
     * Classe utilitaire statique: instanciation interdite.
     */
    private InjectionResolutionValidator() {
    }

    /**
     * Valide tous les points d'injection des classes fournies.
     *
     * @param classes classes à analyser
     * @param context contexte de résolution des dépendances
     * @throws RuntimeException si au moins un point d'injection n'est pas résoluble
     */
    public static void validate(Set<Class<?>> classes, Context context) {
        classes.forEach(clazz -> validateClass(clazz, context));
    }

    /**
     * Valide une classe unique selon son type:
     * méthodes {@code @Bean} pour une configuration, puis constructeur principal pour un composant.
     *
     * @param clazz classe à valider
     * @param context contexte de résolution des dépendances
     */
    private static void validateClass(Class<?> clazz, Context context) {

        if (clazz.isAnnotationPresent(Configuration.class))
            validateConfigurationBeanMethods(clazz, context);

        if (isNotComponentClass(clazz))
            return;

        Constructor<?> constructor = getBeanConstructorWithMaxParameters(clazz);
        validateExecutableDependencies(constructor, context, StringHelper.abbreviateClassName(clazz));
    }

    /**
     * Valide les dépendances des méthodes {@code @Bean} d'une classe de configuration.
     *
     * @param configurationClass classe de configuration
     * @param context contexte de résolution des dépendances
     */
    private static void validateConfigurationBeanMethods(Class<?> configurationClass, Context context) {
        Arrays.stream(configurationClass.getDeclaredMethods())
                .filter(method -> !isNotBeanMethod(method))
                .forEach(method -> validateExecutableDependencies(
                        method,
                        context,
                        StringHelper.abbreviateMethodName(configurationClass, method)
                ));
    }

    /**
     * Valide la résolvabilité de tous les paramètres d'un exécutable (constructeur ou méthode).
     *
     * @param executable exécutable à inspecter
     * @param context contexte de résolution des dépendances
     * @param ownerLabel libellé court du propriétaire du point d'injection (classe/méthode)
     * @throws RuntimeException si une dépendance est introuvable
     */
    private static void validateExecutableDependencies(Executable executable, Context context, String ownerLabel) {

        for (Parameter parameter : executable.getParameters()) {

            try {

                if (isAnnotatedNamed(parameter))
                    context.validateResolvable(parameter.getType(), getNamedValue(parameter));
                else
                    context.validateResolvable(parameter.getType());

            } catch (RuntimeException e) {
                throw unresolvableInjectionPoint(describeInjectionPoint(ownerLabel, parameter), parameter.getType(), e);
            }
        }
    }

    /**
     * Sélectionne le constructeur déclaré qui possède le plus grand nombre de paramètres.
     *
     * @param <T> type de la classe cible
     * @param clazz classe dont le constructeur principal est recherché
     * @return le constructeur retenu
     * @throws NoSuchElementException si aucun constructeur déclaré n'est trouvé
     */
    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> getBeanConstructorWithMaxParameters(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .map(constructor -> (Constructor<T>) constructor)
                .orElseThrow();
    }

    /**
     * Construit une description textuelle du point d'injection pour les messages d'erreur.
     *
     * @param ownerLabel libellé du propriétaire (classe ou méthode)
     * @param parameter paramètre injecté
     * @return description lisible du point d'injection
     */
    private static String describeInjectionPoint(String ownerLabel, Parameter parameter) {

        String namedSuffix = isAnnotatedNamed(parameter)
                ? " @Named(\"" + getNamedValue(parameter) + "\")"
                : "";

        return ownerLabel + " parameter '" + parameter.getName() + "'" + namedSuffix;
    }
}
