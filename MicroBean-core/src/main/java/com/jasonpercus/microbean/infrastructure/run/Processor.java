package com.jasonpercus.microbean.infrastructure.run;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.DEBUG_MESSAGE_METHOD_ANNOTATED_FOUND;
import static com.jasonpercus.microbean.infrastructure.Constants.SKIPPING_ADAPTER_OS_CONDITION_IS_NOT_MET;
import static com.jasonpercus.microbean.infrastructure.Constants.SKIPPING_COMPONENT_PROFILE_CONDITION_IS_NOT_MET;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.analyseConfigurationClassFailed;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.failedToEvaluateCondition;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.methodMustBePublic;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.methodMustHaveReturnType;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isNotAnnotatedAdapter;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isNotBeanMethod;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isNotComponentClass;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isNotPublicMethod;
import static com.jasonpercus.microbean.infrastructure.helpers.LogHelper.debug;
import static com.jasonpercus.microbean.infrastructure.helpers.StringHelper.abbreviateClassName;
import static com.jasonpercus.microbean.infrastructure.helpers.StringHelper.abbreviateMethodName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.ConditionEvaluator;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.OS;
import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.api.Service;
import com.jasonpercus.microbean.infrastructure.exception.ExceptionManager;
import com.jasonpercus.microbean.infrastructure.factory.BeanDefinition;
import com.jasonpercus.microbean.infrastructure.factory.BeanFactory;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper;
import com.jasonpercus.microbean.infrastructure.helpers.OperatingSystemHelper;
import com.jasonpercus.microbean.infrastructure.helpers.StringHelper;
import com.jasonpercus.microbean.infrastructure.validator.InjectionResolutionValidator;
import com.jasonpercus.microbean.infrastructure.validator.ProfileValidator;

/**
 * Orchestrateur principal du traitement des composants détectés par le scanner.
 * <p>
 * Cette classe applique les règles d'enregistrement des beans issus :
 * </p>
 * <ul>
 *   <li>des méthodes {@link Bean} déclarées dans les classes {@link Configuration},</li>
 *   <li>des classes composantes ({@link Service}, {@link Adapter}).</li>
 * </ul>
 * <p>
 * Avant enregistrement, les contraintes de profil, de condition et de compatibilité OS
 * sont évaluées. En fin de traitement, la résolution des points d'injection est validée
 * via {@link InjectionResolutionValidator}.
 * </p>
 */
public class Processor {

    /**
     * Lance le traitement complet des classes candidates dans l'ordre suivant :
     * <ol>
     *   <li>analyse des méthodes {@link Bean} des classes {@link Configuration},</li>
     *   <li>analyse des classes composantes (service/adapter),</li>
     *   <li>validation de la résolvabilité des injections.</li>
     * </ol>
     *
     * @param classes ensemble des classes détectées
     * @param context contexte d'injection où enregistrer les définitions de bean
     * @param args arguments applicatifs transmis aux évaluateurs de condition
     */
    public static void execute(Set<Class<?>> classes, Context context, String[] args) {
        processMethodsBeans(classes, context, args);
        processClassServicesAndAdapters(classes, context, args);
        InjectionResolutionValidator.validate(classes, context);
    }

    /**
     * Traite les classes de configuration afin d'enregistrer les beans déclarés par méthode.
     *
     * @param classes ensemble des classes détectées
     * @param context contexte d'injection
     * @param args arguments applicatifs
     */
    private static void processMethodsBeans(Set<Class<?>> classes, Context context, String[] args) {
        getConfigurationClasses(classes).forEach(configurationClass -> analyseConfigClass(context, args, configurationClass));
    }

    /**
     * Traite les classes composantes (services et adaptateurs) pour enregistrement.
     *
     * @param classes ensemble des classes détectées
     * @param context contexte d'injection
     * @param args arguments applicatifs
     */
    private static void processClassServicesAndAdapters(Set<Class<?>> classes, Context context, String[] args) {
        classes.forEach(clazz -> analyseClass(context, args, clazz));
    }

    /**
     * Extrait uniquement les classes annotées {@link Configuration}.
     *
     * @param classes ensemble des classes détectées
     * @return sous-ensemble contenant les classes de configuration
     */
    private static Set<Class<?>> getConfigurationClasses(Set<Class<?>> classes) {
        return classes.stream()
                .filter(c -> c.isAnnotationPresent(Configuration.class))
                .collect(Collectors.toSet());
    }

    /**
     * Analyse une classe de configuration : instanciation puis parcours des méthodes déclarées.
     * Toute erreur est encapsulée via
     * {@link ExceptionManager#analyseConfigurationClassFailed(Class, Exception)}.
     *
     * @param context contexte d'injection
     * @param args arguments applicatifs
     * @param configClass classe de configuration à analyser
     */
    private static void analyseConfigClass(Context context, String[] args, Class<?> configClass) {
        try {
            Object configurationInstance = createConfigInstance(configClass);

            Method[] methods = getConfigurationClassMethod(configClass);

            Arrays.stream(methods).forEach(method -> analyseConfigMethod(context, args, configurationInstance, method));

        } catch (Exception e) {
            throw analyseConfigurationClassFailed(configClass, e);
        }
    }

    /**
     * Retourne les méthodes déclarées de la classe de configuration.
     *
     * @param configClass classe de configuration cible
     * @return tableau des méthodes déclarées
     */
    private static Method[] getConfigurationClassMethod(Class<?> configClass) {
        return configClass.getDeclaredMethods();
    }

    /**
     * Analyse une méthode de configuration et enregistre son bean si elle est valide
     * et autorisée par les règles de filtrage.
     *
     * @param context contexte d'injection
     * @param args arguments applicatifs
     * @param configurationInstance instance de la classe de configuration
     * @param method méthode à analyser
     */
    private static void analyseConfigMethod(Context context, String[] args, Object configurationInstance, Method method) {

        if (isNotBeanMethod(method))
            return;

        if (isNotPublicMethod(method))
            throw methodMustBePublic(method);

        if (returnTypeMethodIsVoid(method))
            throw methodMustHaveReturnType(method);

        if (shouldNotRegister(configurationInstance.getClass(), method, context, args))
            return;

        debug(DEBUG_MESSAGE_METHOD_ANNOTATED_FOUND, StringHelper.abbreviateMethodName(configurationInstance.getClass(), method), Bean.class.getSimpleName());

        BeanDefinition<?> def = new BeanDefinition<>(configurationInstance, method, context);

        context.register(def);
    }

    /**
     * Analyse une classe composante et l'enregistre si elle passe tous les filtres.
     *
     * @param context contexte d'injection
     * @param args arguments applicatifs
     * @param clazz classe composante à analyser
     */
    private static void analyseClass(Context context, String[] args, Class<?> clazz) {

        if (isNotComponentClass(clazz))
            return;

        if (shouldNotRegister(clazz, context, args))
            return;

        BeanDefinition<?> def = new BeanDefinition<>(clazz, context);

        context.register(def);
    }

    /**
     * Crée une instance de classe de configuration via son constructeur sans argument.
     *
     * @param configClass classe de configuration à instancier
     * @return instance créée
     * @throws InstantiationException si la classe ne peut pas être instanciée
     * @throws IllegalAccessException si le constructeur n'est pas accessible
     * @throws InvocationTargetException si le constructeur lève une exception
     * @throws NoSuchMethodException si aucun constructeur sans argument n'est disponible
     */
    private static Object createConfigInstance(Class<?> configClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return configClass.getDeclaredConstructor().newInstance();
    }

    /**
     * Détermine si un bean de méthode doit être ignoré selon le profil puis la condition.
     *
     * @param configClass classe de configuration propriétaire de la méthode
     * @param method méthode {@link Bean} candidate
     * @param context contexte d'injection
     * @param args arguments applicatifs
     * @return {@code true} si la méthode ne doit pas être enregistrée, sinon {@code false}
     */
    private static boolean shouldNotRegister(Class<?> configClass, Method method, Context context, String[] args) {

        boolean profileInvalid = shouldNotRegisterProfile(configClass, method);

        if (profileInvalid)
            return true;

        return shouldNotRegisterCondition(method, context, args);
    }

    /**
     * Évalue la règle de profil appliquée à une méthode {@link Bean}.
     * <p>
     * Si la méthode n'est pas annotée {@link Profile}, le profil de son type de retour est évalué.
     * </p>
     *
     * @param configClass classe de configuration propriétaire de la méthode
     * @param method méthode candidate
     * @return {@code true} si le profil invalide l'enregistrement, sinon {@code false}
     */
    private static boolean shouldNotRegisterProfile(Class<?> configClass, Method method) {

        if (AnnotationHelper.isNotAnnotatedProfile(method))
            return shouldNotRegisterProfile(method.getReturnType());

        Profile profile = method.getAnnotation(Profile.class);
        ProfileValidator profileValidator = new ProfileValidator(profile);

        boolean invalidate = profileValidator.invalidate();

        if (invalidate)
            showMessageProfileSkipped(configClass, method, MicroBean.getActiveProfile());

        return invalidate;
    }

    /**
     * Évalue la règle de condition appliquée à une méthode {@link Bean}.
     * <p>
     * Si la méthode n'est pas annotée {@link Condition}, la condition est évaluée
     * sur le type de retour de la méthode.
     * </p>
     *
     * @param method méthode candidate
     * @param context contexte d'injection
     * @param args arguments applicatifs
     * @return {@code true} si la méthode ne doit pas être enregistrée, sinon {@code false}
     */
    private static boolean shouldNotRegisterCondition(Method method, Context context, String[] args) {

        if (AnnotationHelper.isNotAnnotatedCondition(method)) {
            return shouldNotRegisterCondition(method.getReturnType(), context, args);
        }

        Condition condition = method.getAnnotation(Condition.class);

        return condition.negate() == evaluate(condition, context, args);
    }

    /**
     * Détermine si une classe composante doit être ignorée selon les règles OS,
     * profil et condition.
     *
     * @param clazz classe composante candidate
     * @param context contexte d'injection
     * @param args arguments applicatifs
     * @return {@code true} si la classe ne doit pas être enregistrée, sinon {@code false}
     */
    private static boolean shouldNotRegister(Class<?> clazz, Context context, String[] args) {

        return shouldNotRegisterOperatingSystem(clazz) || shouldNotRegisterProfile(clazz) || shouldNotRegisterCondition(clazz, context, args);
    }

    /**
     * Évalue la compatibilité OS d'un adaptateur.
     *
     * @param clazz classe candidate
     * @return {@code true} si la classe doit être ignorée pour incompatibilité OS,
     * sinon {@code false}
     */
    private static boolean shouldNotRegisterOperatingSystem(Class<?> clazz) {

        if (isNotAnnotatedAdapter(clazz))
            return false;

        Adapter annotation = clazz.getAnnotation(Adapter.class);

        OS[] os = annotation.os();
        OS currentOs = OperatingSystemHelper.getCurrentOS();

        if (OperatingSystemHelper.isCompatibleWithCurrentOS(os))
            return false;

        debug(SKIPPING_ADAPTER_OS_CONDITION_IS_NOT_MET, Adapter.class.getSimpleName(), abbreviateClassName(clazz), currentOs);
        return true;
    }

    /**
     * Évalue la règle de profil appliquée à une classe composante.
     *
     * @param clazz classe candidate
     * @return {@code true} si le profil invalide l'enregistrement, sinon {@code false}
     */
    private static boolean shouldNotRegisterProfile(Class<?> clazz) {

        if (AnnotationHelper.isNotAnnotatedProfile(clazz))
            return false;

        Profile profile = clazz.getAnnotation(Profile.class);
        ProfileValidator profileValidator = new ProfileValidator(profile);

        boolean invalidate = profileValidator.invalidate();

        if (invalidate)
            showMessageProfileSkipped(clazz, MicroBean.getActiveProfile());

        return invalidate;
    }

    /**
     * Évalue la règle de condition appliquée à une classe composante.
     *
     * @param clazz classe candidate
     * @param context contexte d'injection
     * @param args arguments applicatifs
     * @return {@code true} si la classe ne doit pas être enregistrée, sinon {@code false}
     */
    private static boolean shouldNotRegisterCondition(Class<?> clazz, Context context, String[] args) {

        if (AnnotationHelper.isNotAnnotatedCondition(clazz))
            return false;

        Condition condition = clazz.getAnnotation(Condition.class);

        return condition.negate() == evaluate(condition, context, args);
    }

    /**
     * Évalue une condition applicative en instanciant son évaluateur.
     *
     * @param condition annotation de condition à évaluer
     * @param context contexte d'injection
     * @param args arguments applicatifs
     * @return résultat de l'évaluation
     */
    private static boolean evaluate(Condition condition, Context context, String[] args) {

        try {
            Class<? extends ConditionEvaluator> evaluatorClass = condition.value();

            ConditionEvaluator evaluator = BeanFactory.create(evaluatorClass, context);

            return evaluator.validate(args);

        } catch (Exception e) {
            throw failedToEvaluateCondition(condition, e);
        }
    }

    /**
     * Vérifie si la méthode retourne {@code void}.
     *
     * @param method méthode à vérifier
     * @return {@code true} si le type de retour est {@code void}, sinon {@code false}
     */
    private static boolean returnTypeMethodIsVoid(Method method) {
        return method.getReturnType() == void.class || method.getReturnType() == Void.class;
    }

    /**
     * Émet un message de debug indiquant qu'une classe est ignorée pour raison de profil.
     *
     * @param classToValidate classe ignorée
     * @param activeProfile profil actif courant
     */
    private static void showMessageProfileSkipped(Class<?> classToValidate, String activeProfile) {
        debug(SKIPPING_COMPONENT_PROFILE_CONDITION_IS_NOT_MET, Profile.class.getSimpleName(), abbreviateClassName(classToValidate), activeProfile);
    }

    /**
     * Émet un message de debug indiquant qu'une méthode est ignorée pour raison de profil.
     *
     * @param classToValidate classe propriétaire de la méthode
     * @param method méthode ignorée
     * @param activeProfile profil actif courant
     */
    private static void showMessageProfileSkipped(Class<?> classToValidate, Method method, String activeProfile) {
        debug(SKIPPING_COMPONENT_PROFILE_CONDITION_IS_NOT_MET, Profile.class.getSimpleName(), abbreviateMethodName(classToValidate, method) , activeProfile);
    }
}
