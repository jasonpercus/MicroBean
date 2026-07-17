package com.jasonpercus.microbean.infrastructure.validator;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.SKIPPING_COMPONENT_CONDITION_IS_NOT_MET;
import static com.jasonpercus.microbean.infrastructure.Constants.SKIPPING_COMPONENT_NEGATE_CONDITION_IS_NOT_MET;
import static com.jasonpercus.microbean.infrastructure.Constants.SKIPPING_COMPONENT_PROFILE_CONDITION_IS_NOT_MET;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.classIsAnnotatedWithMultipleComponentAnnotations;
import static com.jasonpercus.microbean.infrastructure.helpers.LogHelper.debug;
import static com.jasonpercus.microbean.infrastructure.helpers.StringHelper.abbreviateClassName;
import java.lang.annotation.Annotation;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.api.Service;
import com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper;

/**
 * Valide qu'une classe scannée peut être retenue comme composant MicroBean.
 * <p>
 * Cette validation applique les règles suivantes:
 * </p>
 * <ul>
 *   <li>vérification qu'une seule annotation de composant est présente;</li>
 *   <li>application des contraintes {@link Profile} et {@link Condition} selon le type de composant;</li>
 *   <li>journalisation des composants ignorés lorsque leurs conditions ne sont pas satisfaites.</li>
 * </ul>
 */
public class ScanningValidator implements Validator<Void> {

    /**
     * Classe actuellement analysée pendant le scan.
     */
    private final Class<?> scannedClass;

    /**
     * Arguments applicatifs transmis aux validateurs de condition.
     */
    private final String[] args;

    /**
     * Crée un validateur de scan pour une classe donnée.
     *
     * @param scannedClass classe scannée
     * @param args arguments de l'application
     */
    public ScanningValidator(Class<?> scannedClass, String[] args) {
        this.scannedClass = scannedClass;
        this.args = args;
    }

    /**
     * Valide la classe scannée selon son annotation de composant.
     *
     * @param unused paramètre non utilisé (signature imposée par {@link Validator})
     * @return {@code true} si la classe est retenue, sinon {@code false}
     * @throws RuntimeException si plusieurs annotations de composant sont détectées
     */
    @Override
    public boolean validate(Void unused) {
        checkMultipleComponentAnnotation(scannedClass);

        if (AnnotationHelper.isAnnotatedEntryPointService(scannedClass))
            return validateEntryPointService(scannedClass, args);

        if (AnnotationHelper.isAnnotatedService(scannedClass))
            return validateService(scannedClass, args);

        if (AnnotationHelper.isAnnotatedAdapter(scannedClass))
            return validateAdapter(scannedClass, args);

        if (AnnotationHelper.isAnnotatedConfiguration(scannedClass))
            return validateConfiguration(scannedClass, args);

        return false;
    }

    /**
     * Valide une classe annotée {@link EntryPointService}.
     *
     * @param classToValidate classe à valider
     * @param args arguments de l'application
     * @return {@code true} dans l'implémentation actuelle
     */
    @SuppressWarnings("unused")
    private boolean validateEntryPointService(Class<?> classToValidate, String[] args) {
        return true;
    }

    /**
     * Valide une classe annotée {@link Service}.
     *
     * @param classToValidate classe à valider
     * @param args arguments de l'application
     * @return {@code true} si toutes les contraintes sont satisfaites, sinon {@code false}
     */
    private static boolean validateService(Class<?> classToValidate, String[] args) {

        if (classToValidate.isAnnotationPresent(Profile.class)) {
            Profile profileAnnotation = classToValidate.getAnnotation(Profile.class);

            ProfileValidator profileValidator = new ProfileValidator(profileAnnotation);
            if (profileValidator.invalidate(args)) {

                showMessageProfileSkipped(classToValidate, Service.class, MicroBean.getActiveProfile());

                return false;
            }
        }

        if (classToValidate.isAnnotationPresent(Condition.class)) {
            Condition conditionAnnotation = classToValidate.getAnnotation(Condition.class);

            ConditionValidator conditionValidator = new ConditionValidator(conditionAnnotation);
            if (conditionValidator.invalidate(args)) {

                showMessageConditionSkipped(classToValidate, Service.class, conditionAnnotation.negate());

                return false;
            }
        }
        return true;
    }

    /**
     * Valide une classe annotée {@link Adapter}.
     *
     * @param classToValidate classe à valider
     * @param args arguments de l'application
     * @return {@code true} si toutes les contraintes sont satisfaites, sinon {@code false}
     */
    private static boolean validateAdapter(Class<?> classToValidate, String[] args) {

        if (classToValidate.isAnnotationPresent(Profile.class)) {
            Profile profileAnnotation = classToValidate.getAnnotation(Profile.class);

            ProfileValidator profileValidator = new ProfileValidator(profileAnnotation);
            if (profileValidator.invalidate(args)) {

                showMessageProfileSkipped(classToValidate, Adapter.class, MicroBean.getActiveProfile());

                return false;
            }
        }

        if (classToValidate.isAnnotationPresent(Condition.class)) {
            Condition conditionAnnotation = classToValidate.getAnnotation(Condition.class);

            ConditionValidator conditionValidator = new ConditionValidator(conditionAnnotation);
            if (conditionValidator.invalidate(args)) {

                showMessageConditionSkipped(classToValidate, Adapter.class, conditionAnnotation.negate());

                return false;
            }
        }
        return true;
    }

    /**
     * Valide une classe annotée {@link Configuration}.
     *
     * @param classToValidate classe à valider
     * @param args arguments de l'application
     * @return {@code true} si toutes les contraintes sont satisfaites, sinon {@code false}
     */
    private static boolean validateConfiguration(Class<?> classToValidate, String[] args) {

        if (classToValidate.isAnnotationPresent(Profile.class)) {
            Profile profileAnnotation = classToValidate.getAnnotation(Profile.class);

            ProfileValidator profileValidator = new ProfileValidator(profileAnnotation);
            if (profileValidator.invalidate(args)) {

                showMessageProfileSkipped(classToValidate, Configuration.class, MicroBean.getActiveProfile());

                return false;
            }
        }

        if (classToValidate.isAnnotationPresent(Condition.class)) {
            Condition conditionAnnotation = classToValidate.getAnnotation(Condition.class);

            ConditionValidator conditionValidator = new ConditionValidator(conditionAnnotation);
            if (conditionValidator.invalidate(args)) {

                showMessageConditionSkipped(classToValidate, Configuration.class, conditionAnnotation.negate());

                return false;
            }
        }
        return true;
    }

    /**
     * Vérifie qu'une classe ne cumule pas plusieurs annotations de composant incompatibles.
     *
     * @param classToValidate classe à analyser
     * @throws RuntimeException si plusieurs annotations de composant sont présentes
     */
    private static void checkMultipleComponentAnnotation(Class<?> classToValidate) {

        int count = 0;

        if (AnnotationHelper.isAnnotatedService(classToValidate)) count++;
        if (AnnotationHelper.isAnnotatedEntryPointService(classToValidate)) count++;
        if (AnnotationHelper.isAnnotatedAdapter(classToValidate)) count++;
        if (AnnotationHelper.isAnnotatedConfiguration(classToValidate)) count++;

        if (count > 1)
            throw classIsAnnotatedWithMultipleComponentAnnotations(classToValidate);
    }

    /**
     * Journalise le fait qu'un composant est ignoré à cause d'une condition non satisfaite.
     *
     * @param classToValidate classe ignorée
     * @param annotation annotation de composant concernée
     * @param negate indique si la condition est négative
     */
    private static void showMessageConditionSkipped(Class<?> classToValidate, Class<? extends Annotation> annotation, boolean negate) {
        String message = negate
                ? SKIPPING_COMPONENT_NEGATE_CONDITION_IS_NOT_MET
                : SKIPPING_COMPONENT_CONDITION_IS_NOT_MET;

        debug(message, annotation.getSimpleName(), abbreviateClassName(classToValidate));
    }

    /**
     * Journalise le fait qu'un composant est ignoré à cause d'un profil non autorisé.
     *
     * @param classToValidate classe ignorée
     * @param annotation annotation de composant concernée
     * @param activeProfile profil actif courant
     */
    private static void showMessageProfileSkipped(Class<?> classToValidate, Class<? extends Annotation> annotation, String activeProfile) {
        debug(SKIPPING_COMPONENT_PROFILE_CONDITION_IS_NOT_MET, annotation.getSimpleName(), abbreviateClassName(classToValidate), activeProfile);
    }
}
