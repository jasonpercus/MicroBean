package com.jasonpercus.microbean.infrastructure.scanner;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.DEBUG_MESSAGE_COMPONENT_ANNOTATED_FOUND;
import static com.jasonpercus.microbean.infrastructure.helpers.LogHelper.debug;
import static com.jasonpercus.microbean.infrastructure.helpers.StringHelper.abbreviateClassName;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.api.Primary;
import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.api.Service;
import com.jasonpercus.microbean.infrastructure.validator.ScanningValidator;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

/**
 * Réalise le scan des packages applicatifs pour détecter les classes annotées
 * comme composants MicroBean.
 * <p>
 * Le scanner s'appuie sur ClassGraph pour retrouver les classes portant les
 * annotations définies dans {@code COMPONENT_ANNOTATIONS}, puis applique un
 * filtrage technique (interface/abstraite/annotation) et un filtrage métier
 * via {@link ScanningValidator}.
 * </p>
 */
public class ClassScanner {

    /**
     * Liste des packages racines à scanner.
     */
    private final String[] basePackages;

    /**
     * Arguments applicatifs pouvant être utilisés par les validateurs de scan.
     */
    private final String[] args;

    /**
     * Construit un scanner de classes annotées.
     *
     * @param basePackages packages racines à scanner
     * @param args         arguments applicatifs transmis aux validateurs
     */
    public ClassScanner(String[] basePackages, String[] args) {
        this.basePackages = basePackages;
        this.args = args;
    }

    /**
     * Lance le scan et retourne l'ensemble des classes annotées retenues.
     * <p>
     * Les classes retournées sont :
     * </p>
     * <ul>
     *   <li>détectées avec les annotations composants supportées ;</li>
     *   <li>non interfaces, non abstraites et non annotations ;</li>
     *   <li>validées par {@link ScanningValidator}.</li>
     * </ul>
     *
     * @return ensemble des classes composants retenues
     */
    public Set<Class<?>> searchAnnotatedClass() {

        Set<Class<?>> filteredClass = new LinkedHashSet<>();

        try (ScanResult scanResult = scanPackages(this.basePackages)) {
            filterScannedClass(scanResult, filteredClass);
        }

        return filteredClass;
    }

    /**
     * Exécute le scan ClassGraph sur les packages configurés.
     *
     * @param packages packages racines à scanner
     * @return résultat du scan ClassGraph
     */
    private ScanResult scanPackages(String[] packages) {
        return new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(packages)
                .scan();
    }

    /**
     * Parcourt les annotations composants et collecte les classes candidates.
     *
     * @param scanResult résultat brut du scan
     * @param classes    collection de sortie à enrichir
     */
    private void filterScannedClass(ScanResult scanResult, Set<Class<?>> classes) {
        getAnnotationClassToScan().forEach(annotation -> scanResult
                .getClassesWithAnnotation(annotation.getName())
                .filter(ClassScanner::checkingClass)
                .forEach(classInfo -> analyseAndPushAnnotatedClass(classes, annotation, classInfo)));
    }

    /**
     * Renvoie la liste des annotations composants à scanner.
     *
     * @return liste des annotations composants
     */
    @SuppressWarnings("unchecked")
    private List<? extends Class<? extends Annotation>> getAnnotationClassToScan() {
        try (ScanResult apiPackageResult = scanPackages(new String[]{Service.class.getPackageName()})) {
            return apiPackageResult.getAllClasses().stream()
                    .filter(ClassInfo::isAnnotation)
                    .map(ci -> (Class<? extends Annotation>) ci.loadClass())
                    .filter(ClassScanner::filterRetentionAndTarget)
                    .filter(annotation -> !annotation.getCanonicalName()
                            .equals(Condition.class.getCanonicalName()))
                    .filter(annotation -> !annotation.getCanonicalName()
                            .equals(MicroBeanApplication.class.getCanonicalName()))
                    .filter(annotation -> !annotation.getCanonicalName()
                            .equals(Primary.class.getCanonicalName()))
                    .filter(annotation -> !annotation.getCanonicalName()
                            .equals(Profile.class.getCanonicalName()))
                    .toList();
        }
    }

    /**
     * Analyse une classe candidate, applique la validation métier et l'ajoute
     * au résultat si elle est valide.
     *
     * @param results    ensemble de sortie à enrichir
     * @param annotation annotation composant ayant déclenché la détection
     * @param classInfo  métadonnées ClassGraph de la classe candidate
     */
    private void analyseAndPushAnnotatedClass(Set<Class<?>> results, Class<? extends Annotation> annotation, ClassInfo classInfo) {

        Class<?> loaded = classInfo.loadClass();

        ScanningValidator validator = new ScanningValidator(loaded, args);
        if (validator.invalidate())
            return;

        results.add(loaded);

        debug(DEBUG_MESSAGE_COMPONENT_ANNOTATED_FOUND, abbreviateClassName(loaded), annotation.getSimpleName());
    }

    /**
     * Vérifie si une classe candidate est une annotation avec une retention et un target compatibles avec le scan.
     *
     * @param annotation classe candidate
     * @return true si la classe candidate est une annotation avec une retention et un target compatibles
     */
    private static boolean filterRetentionAndTarget(Class<? extends Annotation> annotation) {
        Retention retention = annotation.getAnnotation(Retention.class);
        if (retention == null || retention.value() != RetentionPolicy.RUNTIME)
            return false;

        Target target = annotation.getAnnotation(Target.class);
        return target != null && Arrays.asList(target.value()).contains(ElementType.TYPE);
    }

    /**
     * Vérifie qu'une classe scannée est concrète et exploitable.
     *
     * @param classInfo métadonnées ClassGraph de la classe
     * @return {@code true} si la classe est acceptable pour analyse, sinon {@code false}
     */
    private static boolean checkingClass(ClassInfo classInfo) {
        return isNotInterface(classInfo) && isNotAbstract(classInfo) && isNotAnnotation(classInfo);
    }

    /**
     * Indique si l'élément scanné n'est pas une interface.
     *
     * @param classInfo métadonnées ClassGraph de la classe
     * @return {@code true} si ce n'est pas une interface
     */
    private static boolean isNotInterface(ClassInfo classInfo) {
        return !classInfo.isInterface();
    }

    /**
     * Indique si l'élément scanné n'est pas abstrait.
     *
     * @param classInfo métadonnées ClassGraph de la classe
     * @return {@code true} si la classe est concrète
     */
    private static boolean isNotAbstract(ClassInfo classInfo) {
        return !classInfo.isAbstract();
    }

    /**
     * Indique si l'élément scanné n'est pas une annotation.
     *
     * @param classInfo métadonnées ClassGraph de la classe
     * @return {@code true} si ce n'est pas une annotation
     */
    private static boolean isNotAnnotation(ClassInfo classInfo) {
        return !classInfo.isAnnotation();
    }
}
