package com.jasonpercus.microbean.infrastructure.exception;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.AT_LEAST_ONE_APPLICATION_ENTRY_POINT_CLASS_MUST_BE_PROVIDED;
import static com.jasonpercus.microbean.infrastructure.Constants.CLASS_IS_ANNOTATED_WITH_MULTIPLE_COMPONENT_ANNOTATIONS;
import static com.jasonpercus.microbean.infrastructure.Constants.CLASS_IS_NOT_ANNOTATED_WITH_COMPONENT_ANNOTATION;
import static com.jasonpercus.microbean.infrastructure.Constants.CLASS_SHOULD_NOT_BE_ANNOTATED_WITH_ENTRY_POINT_SERVICE;
import static com.jasonpercus.microbean.infrastructure.Constants.CONFIGURATION_PROPERTIES_FILE_NOT_FOUND;
import static com.jasonpercus.microbean.infrastructure.Constants.CONTEXT_IS_NOT_INITIALIZED;
import static com.jasonpercus.microbean.infrastructure.Constants.CYCLIC_DEPENDENCY_DETECTED;
import static com.jasonpercus.microbean.infrastructure.Constants.FAILED_TO_CALL_POSTCONSTRUCT_METHOD;
import static com.jasonpercus.microbean.infrastructure.Constants.FAILED_TO_CREATE_BEAN;
import static com.jasonpercus.microbean.infrastructure.Constants.FAILED_TO_EVALUATE_CONDITION;
import static com.jasonpercus.microbean.infrastructure.Constants.FAILED_TO_INVOKE_METHOD;
import static com.jasonpercus.microbean.infrastructure.Constants.FAILED_TO_LOAD_CONFIGURATION_PROPERTIES;
import static com.jasonpercus.microbean.infrastructure.Constants.FAILED_TO_PROCESS_CONFIGURATION;
import static com.jasonpercus.microbean.infrastructure.Constants.INVALID_FILE_EXTENSION_FOR_CONFIGURATION_PROPERTIES;
import static com.jasonpercus.microbean.infrastructure.Constants.INVALID_MICROBEAN_OS_OVERRIDE;
import static com.jasonpercus.microbean.infrastructure.Constants.INVALID_PATH_FOR_CONFIGURATION_PROPERTIES;
import static com.jasonpercus.microbean.infrastructure.Constants.METHOD_IS_NOT_ANNOTATED;
import static com.jasonpercus.microbean.infrastructure.Constants.METHOD_MUST_BE_PUBLIC;
import static com.jasonpercus.microbean.infrastructure.Constants.METHOD_MUST_HAVE_RETURN_TYPE;
import static com.jasonpercus.microbean.infrastructure.Constants.MISSING_ENTRY_POINT_SERVICE_ON_CLASS;
import static com.jasonpercus.microbean.infrastructure.Constants.MISSING_MICRO_BEAN_APPLICATION_ON_CLASS;
import static com.jasonpercus.microbean.infrastructure.Constants.MULTIPLE_BEANS_FOUND_FOR_TYPE;
import static com.jasonpercus.microbean.infrastructure.Constants.NO_BEAN_FOUND_FOR_NAME;
import static com.jasonpercus.microbean.infrastructure.Constants.NO_BEAN_FOUND_FOR_TYPE;
import static com.jasonpercus.microbean.infrastructure.Constants.NO_BEAN_MATCHING_CURRENT_OS;
import static com.jasonpercus.microbean.infrastructure.Constants.ONLY_ONE_APPLICATION_ENTRY_POINT_CAN_BE_ONE_SHOT;
import static com.jasonpercus.microbean.infrastructure.Constants.UNRESOLVABLE_INJECTION_POINT;
import static com.jasonpercus.microbean.infrastructure.helpers.StringHelper.abbreviateClassName;
import java.lang.reflect.Method;
import java.util.List;
import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.api.PostConstruct;
import com.jasonpercus.microbean.infrastructure.factory.BeanDefinition;
import com.jasonpercus.microbean.infrastructure.helpers.StringHelper;

/**
 * Classe utilitaire centralisant la construction de RuntimeExceptions pour les différentes
 * erreurs pouvant survenir dans le framework. Permet d'avoir des messages d'erreur cohérents
 * et de réduire la duplication de code lors de la création d'exceptions.
 */
public class ExceptionManager {

    /**
     * Construit une RuntimeException indiquant que le contexte global n'est pas encore initialisé.
     *
     * @return RuntimeException à lancer
     */
    public static RuntimeException contextIsNotInitialized() {
        return new MicroBeanException(CONTEXT_IS_NOT_INITIALIZED);
    }

    /**
     * Construit une RuntimeException indiquant qu'une classe de configuration n'a
     * pas pu être traitée.
     *
     * @param configClass classe de configuration problématique
     * @param e           exception d'origine ayant causé l'échec
     * @return RuntimeException à lancer
     */
    public static RuntimeException analyseConfigurationClassFailed(Class<?> configClass, Exception e) {
        return new MicroBeanException(e, FAILED_TO_PROCESS_CONFIGURATION, configClass.getName());
    }

    /**
     * Construit une RuntimeException indiquant qu'une méthode annotée {@link Bean} doit être publique.
     *
     * @param method méthode invalide
     * @return RuntimeException à lancer
     */
    public static RuntimeException methodMustBePublic(Method method) {
        return new MicroBeanException(METHOD_MUST_BE_PUBLIC, getNameBeanAnnotation(), method.getName());
    }

    /**
     * Construit une RuntimeException indiquant qu'une méthode annotée {@link Bean} doit
     * retourner un type (non-void).
     *
     * @param method méthode invalide
     * @return RuntimeException à lancer
     */
    public static RuntimeException methodMustHaveReturnType(Method method) {
        return new MicroBeanException(METHOD_MUST_HAVE_RETURN_TYPE, getNameBeanAnnotation(), method.getName());
    }

    /**
     * Construit une RuntimeException indiquant qu'une méthode n'est pas annotée
     * avec l'annotation {@link Bean}.
     *
     * @param method méthode problématique
     * @return RuntimeException à lancer
     */
    public static RuntimeException methodIsNotAnnotated(Method method) {
        return new MicroBeanException(METHOD_IS_NOT_ANNOTATED, method.getName(), Bean.class.getSimpleName());
    }

    /**
     * Construit une RuntimeException indiquant qu'une méthode annotée n'a pas pu
     * être invoquée.
     *
     * @param method méthode problématique
     * @param e      exception d'origine ayant causé l'échec d'invocation
     * @return RuntimeException à lancer
     */
    public static RuntimeException invocationMethodFailed(Method method, Exception e) {
        return new MicroBeanException(e, FAILED_TO_INVOKE_METHOD, getNameBeanAnnotation(), method.getName());
    }

    /**
     * Construit une RuntimeException indiquant qu'une condition n'a pas pu être
     * évaluée.
     *
     * @param condition annotation de condition problématique
     * @param e         exception d'origine ayant causé l'échec d'évaluation
     * @return RuntimeException à lancer
     */
    public static RuntimeException failedToEvaluateCondition(Condition condition, Exception e) {
        return new MicroBeanException(e, FAILED_TO_EVALUATE_CONDITION, condition.value());
    }

    /**
     * Construit une {@link RuntimeException} descriptive pour une dépendance cyclique.
     *
     * @param <T>   type de la classe en cycle
     * @param clazz la classe détectée en cycle
     * @return l'exception à lancer
     */
    public static <T> RuntimeException cyclicDependencyDetected(Class<T> clazz) {
        return new MicroBeanException(CYCLIC_DEPENDENCY_DETECTED, clazz.getName());
    }

    /**
     * Construit une {@link RuntimeException} lorsque aucun bean n'est trouvé pour un type.
     *
     * @param <T>  type recherché
     * @param type la classe recherchée
     * @return l'exception à lancer
     */
    public static <T> RuntimeException noBeanFoundForType(Class<T> type) {
        return new MicroBeanException(NO_BEAN_FOUND_FOR_TYPE, type.getName());
    }

    /**
     * Construit une {@link RuntimeException} encapsulant l'échec de création d'un bean.
     *
     * @param <T>       type du bean
     * @param beanClass la classe du bean dont la création a échoué
     * @param e         l'exception d'origine
     * @return l'exception à lancer
     */
    public static <T> RuntimeException failedToCreateBean(Class<T> beanClass, Exception e) {
        return new MicroBeanException(e, FAILED_TO_CREATE_BEAN, beanClass.getName());
    }

    /**
     * Construit une {@link RuntimeException} lorsque aucun bean n'est trouvé pour un nom.
     *
     * @param name le nom recherché
     * @return l'exception à lancer
     */
    public static RuntimeException noBeanFoundForName(String name) {
        return new MicroBeanException(NO_BEAN_FOUND_FOR_NAME, name);
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'il existe plusieurs beans pour un type.
     *
     * @param forType    le type pour lequel plusieurs beans ont été trouvés
     * @param candidates la liste des beans candidats trouvés pour ce type
     * @return l'exception à lancer
     */
    public static RuntimeException multipleBeansFoundForType(Class<?> forType, List<BeanDefinition<?>> candidates) {

        List<String> candidateBeanNames = candidates.stream()
                .map(BeanDefinition::getType)
                .map(StringHelper::abbreviateClassName)
                .toList();

        String ambiguousBeanName = StringHelper.abbreviateClassName(forType) + " => " + candidateBeanNames;

        return new MicroBeanException(MULTIPLE_BEANS_FOUND_FOR_TYPE, ambiguousBeanName);
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'aucun bean n'est compatible avec l'OS.
     *
     * @return l'exception à lancer
     */
    public static RuntimeException noBeanMatchingCurrentOS() {
        return new MicroBeanException(NO_BEAN_MATCHING_CURRENT_OS);
    }

    /**
     * Construit une RuntimeException lorsqu'un override d'OS fourni à MicroBean est invalide.
     *
     * @param propertyName nom de la propriété système
     * @param value        valeur invalide rencontrée
     * @return l'exception à lancer
     */
    public static RuntimeException invalidOperatingSystemOverride(String propertyName, String value) {
        return new MicroBeanException(INVALID_MICROBEAN_OS_OVERRIDE, propertyName, value);
    }

    /**
     * Construit une exception fail-fast lorsqu'un point d'injection ne peut pas être résolu.
     *
     * @param injectionPoint emplacement de l'injection (constructeur/méthode + paramètre)
     * @param dependencyType type demandé à l'injection
     * @param cause          exception d'origine liée à la résolution
     * @return l'exception à lancer
     */
    public static RuntimeException unresolvableInjectionPoint(String injectionPoint, Class<?> dependencyType, RuntimeException cause) {
        return new MicroBeanException(cause, UNRESOLVABLE_INJECTION_POINT, injectionPoint, dependencyType.getName());
    }

    /**
     * Construit une {@link RuntimeException} lorsqu'un appel @PostConstruct échoue et encapsule
     * l'exception d'origine.
     *
     * @param method la méthode @PostConstruct qui a échoué
     * @param e      l'exception d'origine
     * @return l'exception à lancer
     */
    public static RuntimeException failedToCallPostConstructMethod(Method method, Exception e) {
        return new MicroBeanException(e, FAILED_TO_CALL_POSTCONSTRUCT_METHOD, PostConstruct.class.getSimpleName(), method);
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'il ne peut y avoir qu'un seul entry point ONE_SHOT.
     *
     * @return l'exception à lancer
     */
    public static RuntimeException onlyOneApplicationEntryPointCanBeOneShot() {
        return new MicroBeanException(ONLY_ONE_APPLICATION_ENTRY_POINT_CAN_BE_ONE_SHOT);
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'une classe annotée ne doit pas porter
     * {@link EntryPointService} (règle métier).
     *
     * @param appClass la classe fautive
     * @return l'exception à lancer
     */
    public static RuntimeException classShouldNotBeAnnotatedWithEntryPointService(Class<?> appClass) {
        return new MicroBeanException(CLASS_SHOULD_NOT_BE_ANNOTATED_WITH_ENTRY_POINT_SERVICE, StringHelper.abbreviateClassName(appClass));
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'au moins une classe d'entry point doit
     * être fournie.
     *
     * @return l'exception à lancer
     */
    public static RuntimeException atLeastOneApplicationEntryPointClassMustBeProvided() {
        return new MicroBeanException(AT_LEAST_ONE_APPLICATION_ENTRY_POINT_CLASS_MUST_BE_PROVIDED);
    }

    /**
     * Construit une {@link RuntimeException} indiquant que la classe principale n'est pas annotée
     * {@link MicroBeanApplication}.
     *
     * @param appClass la classe principale
     * @return l'exception à lancer
     */
    public static RuntimeException missingMicroBeanApplicationOnClass(Class<?> appClass) {
        return new MicroBeanException(MISSING_MICRO_BEAN_APPLICATION_ON_CLASS, StringHelper.abbreviateClassName(appClass));
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'une classe d'entry point n'est pas annotée
     * {@link EntryPointService}.
     *
     * @param entryPointClass la classe EntryPoint fautive
     * @return l'exception à lancer
     */
    public static RuntimeException missingEntryPointServiceOnClass(Class<?> entryPointClass) {
        return new MicroBeanException(MISSING_ENTRY_POINT_SERVICE_ON_CLASS, StringHelper.abbreviateClassName(entryPointClass));
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'une classe est annotée avec plusieurs
     * annotations de composant, ce qui est interdit.
     *
     * @param clazz la classe fautive
     * @return l'exception à lancer
     */
    public static RuntimeException classIsAnnotatedWithMultipleComponentAnnotations(Class<?> clazz) {
        return new MicroBeanException(CLASS_IS_ANNOTATED_WITH_MULTIPLE_COMPONENT_ANNOTATIONS, abbreviateClassName(clazz));
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'une classe n'est annotée avec aucune
     * annotation de composant, ce qui est interdit.
     *
     * @param clazz la classe fautive
     * @return l'exception à lancer
     */
    public static RuntimeException classIsNotAnnotatedWithComponentAnnotation(Class<?> clazz) {
        return new MicroBeanException(CLASS_IS_NOT_ANNOTATED_WITH_COMPONENT_ANNOTATION, abbreviateClassName(clazz));
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'un chemin de configuration est invalide.
     *
     * @param path le chemin de configuration invalide
     * @return l'exception à lancer
     */
    public static RuntimeException invalidPathForConfigurationProperties(String path) {
        return new MicroBeanException(INVALID_PATH_FOR_CONFIGURATION_PROPERTIES, path);
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'un fichier de configuration est introuvable.
     *
     * @param path le chemin du fichier de configuration
     * @return l'exception à lancer
     */
    public static RuntimeException configurationPropertiesFileNotFound(String path) {
        return new MicroBeanException(CONFIGURATION_PROPERTIES_FILE_NOT_FOUND, path);
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'un fichier de configuration a une extension invalide.
     *
     * @param path le chemin du fichier de configuration
     * @return l'exception à lancer
     */
    public static RuntimeException invalidFileExtensionForConfigurationProperties(String path) {
        return new MicroBeanException(INVALID_FILE_EXTENSION_FOR_CONFIGURATION_PROPERTIES, path);
    }

    /**
     * Construit une {@link RuntimeException} indiquant qu'un fichier de configuration n'a pas pu être chargé.
     *
     * @param path le chemin du fichier de configuration
     * @return l'exception à lancer
     */
    public static RuntimeException failedToLoadConfigurationProperties(String path, Exception e) {
        return new MicroBeanException(e, FAILED_TO_LOAD_CONFIGURATION_PROPERTIES, path);
    }

    /**
     * Retourne le nom utilisé pour référencer l'annotation {@link Bean} (utile
     * pour construire des messages d'erreur cohérents).
     *
     * @return nom simple de l'annotation Bean
     */
    private static String getNameBeanAnnotation() {
        return Bean.class.getSimpleName();
    }
}
