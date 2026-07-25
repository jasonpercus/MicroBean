package com.jasonpercus.microbean.infrastructure.scanner;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.DEBUG_MESSAGE_COMPONENT_ANNOTATED_FOUND;
import static com.jasonpercus.microbean.infrastructure.Constants.ERROR_INSTANTIATING_IMODULE_INIT_CLASS;
import static com.jasonpercus.microbean.infrastructure.helpers.LogHelper.debug;
import static com.jasonpercus.microbean.infrastructure.helpers.StringHelper.abbreviateClassName;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.api.Primary;
import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.infrastructure.api.IModuleInit;
import com.jasonpercus.microbean.infrastructure.api.ModuleInit;
import com.jasonpercus.microbean.infrastructure.helpers.LogHelper;
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
     * Lance le scan et retourne l'ensemble des classes "components" annotées retenues ainsi que les autres classes
     * annotées non "components".
     * <p>
     * Les classes retournées sont :
     * </p>
     * <ul>
     *   <li>détectées avec les annotations supportées (component ou pas) ;</li>
     *   <li>non interfaces, non abstraites et non annotations ;</li>
     *   <li>validées par {@link ScanningValidator}.</li>
     * </ul>
     */
    public void searchAnnotatedClass(Set<Class<?>> componentClasses, Set<Class<?>> otherClasses) {
        try (ScanResult scanResult = scanPackages(this.basePackages)) {
            filterScannedClass(scanResult, componentClasses, otherClasses);
        }
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
     * @param scanResult        résultat brut du scan
     * @param componentsClasses ensemble de classes components à enrichir
     * @param othersClasses     ensemble de classes non components à enrichir
     */
    private void filterScannedClass(ScanResult scanResult, Set<Class<?>> componentsClasses, Set<Class<?>> othersClasses) {

        Map<String, Set<ClassInfo>> annotatedClassesMap = new TreeMap<>();

        getAnnotationClassToScan().stream()
                .sorted(getClassComparator())
                .forEach(annotation -> {
                            scanResult
                                    .getClassesWithAnnotation(annotation.getName())
                                    .filter(ClassScanner::checkingClass)
                                    .forEach(classInfo -> annotatedClassesMap
                                            .computeIfAbsent(annotation.getCanonicalName(), k -> new LinkedHashSet<>())
                                            .add(classInfo)
                                    );
                        }
                );

        Set<ClassInfo> moduleInitClassInfo = annotatedClassesMap.remove(ModuleInit.class.getCanonicalName());
        Set<Class<? extends Annotation>> annotationsSearchedToAddToOthersClasses = getOthersAnnotationsToKeep(moduleInitClassInfo);

        annotatedClassesMap.forEach((annotationName, classesSet) -> classesSet
                .forEach(classInfo -> analyseAndPushAnnotatedClass(
                        annotationsSearchedToAddToOthersClasses,
                        componentsClasses,
                        othersClasses,
                        annotationName,
                        classInfo
                ))
        );
    }

    /**
     * Renvoie la liste des annotations composants à scanner.
     *
     * @return liste des annotations composants
     */
    @SuppressWarnings("unchecked")
    private List<? extends Class<? extends Annotation>> getAnnotationClassToScan() {

        String microbeanPackage = MicroBeanApplication.class.getPackageName()
                .substring(0, MicroBeanApplication.class.getPackageName().lastIndexOf('.'));

        String apiPackage = microbeanPackage + ".api";
        String infrastructureApiPackage = microbeanPackage + ".infrastructure.api";

        try (ScanResult apiPackageResult = scanPackages(new String[]{apiPackage, infrastructureApiPackage})) {
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
     * Analyse une classe candidate, applique la validation métier et l'ajoute à l'une des deux listes de résultats.
     *
     * @param annotationsSearchedToAddToOthersClasses annotations à conserver pour les autres classes
     * @param componentClasses                        ensemble de classes components à enrichir
     * @param otherClasses                            ensemble de classes non components à enrichir
     * @param annotationName                          nom canonique de l'annotation ayant déclenché la détection
     * @param classInfo                               métadonnées ClassGraph de la classe candidate
     */
    private void analyseAndPushAnnotatedClass(
            Set<Class<? extends Annotation>> annotationsSearchedToAddToOthersClasses,
            Set<Class<?>> componentClasses,
            Set<Class<?>> otherClasses,
            String annotationName,
            ClassInfo classInfo
    ) {

        Class<?> loaded = classInfo.loadClass();

        ScanningValidator validator = new ScanningValidator(loaded, args);
        if (validator.invalidate()) {
            Arrays.stream(loaded.getAnnotations())
                    .map(Annotation::annotationType)
                    .filter(annotationsSearchedToAddToOthersClasses::contains)
                    .findFirst()
                    .ifPresent(annotation -> otherClasses.add(loaded));
            return;
        }

        componentClasses.add(loaded);

        debug(DEBUG_MESSAGE_COMPONENT_ANNOTATED_FOUND, abbreviateClassName(loaded), annotationName.substring(annotationName.lastIndexOf('.') + 1));
    }

    /**
     * Récupère la liste des annotations à conserver pour les classes annotées avec {@link ModuleInit}.
     *
     * @param moduleInitClassInfo ensemble des classes annotées avec {@link ModuleInit}
     * @return ensemble des annotations à conserver
     */
    private Set<Class<? extends Annotation>> getOthersAnnotationsToKeep(Set<ClassInfo> moduleInitClassInfo) {

        Set<Class<? extends Annotation>> annotationsSearchedToAdd = new LinkedHashSet<>();

        if (moduleInitClassInfo != null) {
            moduleInitClassInfo.forEach(classInfo -> {
                Class<?> loaded = classInfo.loadClass();

                if (IModuleInit.class.isAssignableFrom(loaded)) {
                    Set<Class<? extends Annotation>> annotationsToAdd = new LinkedHashSet<>();
                    try {
                        IModuleInit iModuleInit = (IModuleInit) loaded.getConstructor().newInstance();
                        iModuleInit.keepAnnotatedClassForContext(annotationsToAdd);
                        annotationsSearchedToAdd.addAll(annotationsToAdd);
                    } catch (Exception e) {
                        LogHelper.error(ERROR_INSTANTIATING_IMODULE_INIT_CLASS.formatted(loaded.getName()), e);
                    }
                }
            });
        }
        return annotationsSearchedToAdd;
    }

    /**
     * Renvoie un comparateur de classes basé sur le fait que les annotations de type {@link ModuleInit} doivent être
     * traitées en premier. En cas d'égalité, les classes sont triées par ordre alphabétique de leur nom canonique.
     *
     * @return comparateur de classes
     */
    private static Comparator<Class<? extends Annotation>> getClassComparator() {
        return (c1, c2) -> {
            if (c1.isAnnotationPresent(ModuleInit.class) && !c2.isAnnotationPresent(ModuleInit.class))
                return -1;
            else if (!c1.isAnnotationPresent(ModuleInit.class) && c2.isAnnotationPresent(ModuleInit.class))
                return 1;
            else
                return c1.getCanonicalName().compareTo(c2.getCanonicalName());
        };
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
