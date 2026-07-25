package com.jasonpercus.microbean.infrastructure.factory;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.multipleBeansFoundForType;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.noBeanFoundForName;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.noBeanFoundForType;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.noBeanMatchingCurrentOS;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import com.jasonpercus.microbean.api.Scope;
import com.jasonpercus.microbean.infrastructure.api.ModuleInit;
import com.jasonpercus.microbean.infrastructure.helpers.OperatingSystemHelper;

/**
 * Contexte d'injection du conteneur MicroBean.
 * <p>
 * Cette classe centralise l'enregistrement et la résolution des définitions de
 * beans, puis délègue leur création via {@link BeanDefinition#createBean()}.
 * Elle gère également :
 * </p>
 * <ul>
 *   <li>la résolution par type ou par nom,</li>
 *   <li>la sélection d'un bean primaire en cas d'ambiguïté,</li>
 *   <li>la compatibilité OS des candidats,</li>
 *   <li>le cache et le verrouillage des singletons.</li>
 * </ul>
 */
public class Context {

    /**
     * Liste des classes (annotées) "components" détectées par le scanner.
     */
    private final SortedSet<Class<?>> componentClasses;

    /**
     * Liste des autres classes (annotées) détectées par le scanner (non components).
     */
    private final SortedSet<Class<?>> otherClasses;

    /**
     * Index des définitions de beans par type.
     */
    private final Map<Class<?>, List<BeanDefinition<?>>> BEANS_BY_TYPE = new ConcurrentHashMap<>();

    /**
     * Index des définitions de beans par nom.
     */
    private final Map<String, List<BeanDefinition<?>>> BEANS_BY_NAME = new ConcurrentHashMap<>();

    /**
     * Cache des instances singleton déjà créées.
     */
    private final Map<BeanDefinition<?>, Object> SINGLETON_CACHE = new ConcurrentHashMap<>();

    /**
     * Verrous par définition pour protéger la création des singletons.
     */
    private final Map<BeanDefinition<?>, Object> SINGLETON_LOCKS = new ConcurrentHashMap<>();

    /**
     * Construit un contexte d'injection.
     *
     * @param componentClasses classes annotées "component" détectées par le scanner
     * @param otherClasses     autres classes annotées détectées par le scanner (non components)
     */
    public Context(TreeSet<Class<?>> componentClasses, TreeSet<Class<?>> otherClasses) {
        this.componentClasses = Collections.unmodifiableSortedSet(componentClasses);
        this.otherClasses = Collections.unmodifiableSortedSet(otherClasses);
    }

    /**
     * Retourne l'ensemble des classes annotées "component" détectées par le scanner.
     *
     * @return l'ensemble des classes annotées "component"
     */
    public Set<Class<?>> getComponentClasses() {
        return componentClasses;
    }

    /**
     * Retourne l'ensemble des autres classes annotées (et autorisées par les classes annotées {@link ModuleInit})
     * détectées par le scanner (non components).
     *
     * @return l'ensemble des autres classes annotées
     */
    public Set<Class<?>> getOtherClasses() {
        return otherClasses;
    }

    /**
     * Renvoie toutes les instances ayant leur classe annotée
     *
     * @param annotationType type d'annotation à rechercher
     * @return liste des instances correspondantes
     */
    public List<Object> getBeansByAnnotation(Class<? extends Annotation> annotationType) {

        Set<Object> beans = Collections.newSetFromMap(new IdentityHashMap<>());

        for (List<BeanDefinition<?>> definitions : BEANS_BY_TYPE.values()) {
            for (BeanDefinition<?> definition : definitions) {
                Class<?> beanClass = definition.getType();
                if (beanClass.isAnnotationPresent(annotationType))
                    beans.add(getBean(beanClass));
            }
        }

        return beans.stream()
                .sorted(getClassNameComparator())
                .toList();
    }

    /**
     * Renvoie tous les types de beans ayant leur classe annotée
     *
     * @param annotationType type d'annotation à rechercher
     * @return liste des types correspondants
     */
    public List<Class<?>> getBeanTypesByAnnotation(Class<? extends Annotation> annotationType) {

        Set<Class<?>> beanTypes = new HashSet<>();

        for (List<BeanDefinition<?>> definitions : BEANS_BY_TYPE.values()) {
            for (BeanDefinition<?> definition : definitions) {
                Class<?> beanClass = definition.getType();
                if (beanClass.isAnnotationPresent(annotationType))
                    beanTypes.add(beanClass);
            }
        }

        return beanTypes.stream()
                .sorted(getClassNameComparator())
                .toList();
    }

    /**
     * Résout un bean par type.
     *
     * @param type type demandé
     * @return instance résolue
     * @throws RuntimeException si aucun bean n'est trouvé ou en cas d'ambiguïté
     */
    public <O> O getBean(Class<?> type) {
        return getBean(type, () -> {
            throw noBeanFoundForType(type);
        });
    }

    /**
     * Résout un bean par type silencieusement (renvoie null si non trouvé).
     *
     * @param type type demandé
     * @return instance résolue ou null si non trouvée
     * @throws RuntimeException en cas d'ambiguïté
     */
    public <O> O getBeanSilently(Class<?> type) {
        return getBean(type, () -> null);
    }

    /**
     * Résout un bean par type.
     *
     * @param type type demandé
     * @param callbackIfNotExists callback à exécuter si aucun bean n'est trouvé
     * @return instance résolue
     * @throws RuntimeException en cas d'ambiguïté
     */
    public <O> O getBean(Class<?> type, Supplier<O> callbackIfNotExists) {

        List<BeanDefinition<?>> beanDefinitions = BEANS_BY_TYPE.get(type);

        if (beanDefinitions == null || beanDefinitions.isEmpty())
            return callbackIfNotExists.get();

        BeanDefinition<?> beanDefinition = resolve(beanDefinitions, type);
        return createSingletonOrPrototypeBean(beanDefinition);
    }

    /**
     * Résout un bean par nom, puis vérifie sa compatibilité avec le type attendu.
     *
     * @param type type attendu
     * @param name nom du bean
     * @return instance résolue
     * @throws RuntimeException si le nom est introuvable, incompatible, ou ambigu
     */
    public <O> O getBean(Class<?> type, String name) {
        return getBean(type, name, () -> {
            throw noBeanFoundForName(name);
        });
    }

    /**
     * Résout un bean par nom silencieusement (renvoie null si non trouvé), puis vérifie sa compatibilité avec le type attendu.
     *
     * @param type type attendu
     * @param name nom du bean
     * @return instance résolue ou null si non trouvée
     * @throws RuntimeException si le nom est introuvable, incompatible, ou ambigu
     */
    public <O> O getBeanSilently(Class<?> type, String name) {
        return getBean(type, name, () -> null);
    }

    /**
     * Résout un bean par nom, puis vérifie sa compatibilité avec le type attendu.
     *
     * @param type type attendu
     * @param name nom du bean
     * @param callbackIfNotExists callback à exécuter si aucun bean n'est trouvé
     * @return instance résolue
     * @throws RuntimeException si le nom est introuvable, incompatible, ou ambigu
     */
    public <O> O getBean(Class<?> type, String name, Supplier<O> callbackIfNotExists) {
        List<BeanDefinition<?>> beanDefinitions = BEANS_BY_NAME.get(name);

        if (beanDefinitions == null || beanDefinitions.isEmpty())
            return callbackIfNotExists.get();

        List<BeanDefinition<?>> typedBeanDefinitions = getBeanDefinitionsAssignableToType(beanDefinitions, type);

        if (typedBeanDefinitions.isEmpty())
            return callbackIfNotExists.get();

        BeanDefinition<?> beanDefinition = resolve(typedBeanDefinitions, type);
        return createSingletonOrPrototypeBean(beanDefinition);
    }

    /**
     * Vérifie qu'un bean est résoluble par type, sans le créer.
     *
     * @param type type à valider
     * @throws RuntimeException si aucune résolution valide n'est possible
     */
    public void validateResolvable(Class<?> type) {

        List<BeanDefinition<?>> beanDefinitions = BEANS_BY_TYPE.get(type);

        if (beanDefinitions == null || beanDefinitions.isEmpty())
            throw noBeanFoundForType(type);

        resolve(beanDefinitions, type);
    }

    /**
     * Vérifie qu'un bean est résoluble par nom et type attendu, sans le créer.
     *
     * @param type type attendu
     * @param name nom du bean
     * @throws RuntimeException si la résolution est impossible
     */
    public void validateResolvable(Class<?> type, String name) {

        List<BeanDefinition<?>> beanDefinitions = BEANS_BY_NAME.get(name);

        if (beanDefinitions == null || beanDefinitions.isEmpty())
            throw noBeanFoundForName(name);

        List<BeanDefinition<?>> typedBeanDefinitions = getBeanDefinitionsAssignableToType(beanDefinitions, type);

        if (typedBeanDefinitions.isEmpty())
            throw noBeanFoundForName(name);

        resolve(typedBeanDefinitions, type);
    }

    /**
     * Enregistre une définition de bean et ses alias de résolution
     * (interfaces + superclasses).
     *
     * @param beanDefinition définition à enregistrer
     */
    public void register(BeanDefinition<?> beanDefinition) {

        registerBeanDefinitionForType(beanDefinition.getType(), beanDefinition);

        if (isNotEmptyBeanDefinitionName(beanDefinition))
            registerBeanDefinitionForName(beanDefinition);

        Class<?>[] interfaces = getInterfacesForBeanDefinition(beanDefinition);
        Arrays.stream(interfaces).forEach(
                iface -> register(iface, beanDefinition)
        );

        Class<?> superclass = getSuperclassForBeanDefinition(beanDefinition);
        if (superclass != null && superclass != Object.class)
            register(superclass, beanDefinition);
    }

    /**
     * Enregistre une définition pour un type explicite, puis propage
     * l'enregistrement à ses interfaces et superclasses.
     *
     * @param type type d'enregistrement
     * @param beanDefinition définition associée
     */
    public void register(Class<?> type, BeanDefinition<?> beanDefinition) {

        registerBeanDefinitionForType(type, beanDefinition);

        Class<?>[] interfaces = getInterfacesForType(type);
        Arrays.stream(interfaces).forEach(
                iface -> register(iface, beanDefinition)
        );

        Class<?> superclass = getSuperclassForType(type);
        if (superclass != null && superclass != Object.class)
            register(superclass, beanDefinition);
    }

    /**
     * Enregistre une instance déjà créée comme singleton injectable.
     *
     * @param type type exposé dans le contexte
     * @param instance instance singleton à enregistrer
     * @param <T> type de l'instance
     */
    public <T> void registerSingleton(Class<T> type, T instance) {
        register(new BeanDefinition<>(type, instance));
    }

    /**
     * Enregistre une instance déjà créée comme singleton injectable.
     *
     * @param type type exposé dans le contexte
     * @param instance instance singleton à enregistrer
     * @param name nom du bean
     * @param <T> type de l'instance
     */
    public <T> void registerSingleton(Class<T> type, T instance, String name) {
        register(new BeanDefinition<>(type, instance, name));
    }

    /**
     * Crée un bean selon sa portée ({@link Scope#SINGLETON} ou prototype).
     *
     * @param beanDefinition définition ciblée
     * @param <T> type de retour attendu
     * @return instance créée ou récupérée
     */
    <T> T createSingletonOrPrototypeBean(BeanDefinition<?> beanDefinition) {
        if (beanDefinition.getScope() == Scope.SINGLETON)
            return createSingletonBean(beanDefinition);
        else
            return createPrototypeBean(beanDefinition);
    }

    /**
     * Crée ou récupère une instance singleton de manière thread-safe.
     *
     * @param beanDefinition définition du singleton
     * @param <T> type de retour attendu
     * @return instance singleton
     */
    @SuppressWarnings("unchecked")
    <T> T createSingletonBean(BeanDefinition<?> beanDefinition) {

        Object singleton = getBeanDefinitionInSingletonCache(beanDefinition);

        if (singleton != null)
            return (T) singleton;

        Object lock = SINGLETON_LOCKS.computeIfAbsent(beanDefinition, ignored -> new Object());

        synchronized (lock) {
            singleton = getBeanDefinitionInSingletonCache(beanDefinition);

            if (singleton != null)
                return (T) singleton;

            Object bean = createBean(beanDefinition);

            addBeanDefinitionInSingletonCache(beanDefinition, bean);

            return (T) bean;
        }
    }

    /**
     * Crée une nouvelle instance prototype.
     *
     * @param beanDefinition définition prototype
     * @param <T> type de retour attendu
     * @return nouvelle instance
     */
    @SuppressWarnings("unchecked")
    <T> T createPrototypeBean(BeanDefinition<?> beanDefinition) {
        return (T) createBean(beanDefinition);
    }

    /**
     * Délègue la création d'instance à la définition de bean.
     *
     * @param beanDefinition définition à créer
     * @return instance créée
     */
    Object createBean(BeanDefinition<?> beanDefinition) {
        return beanDefinition.createBean();
    }

    /**
     * Résout la définition à utiliser parmi les candidats.
     * <p>
     * La sélection applique successivement : filtre OS, candidat unique,
     * puis bean primaire.
     * </p>
     *
     * @param beanDefinitions candidats potentiels
     * @param forType type demandé (pour les messages d'erreur)
     * @return définition retenue
     */
    BeanDefinition<?> resolve(List<BeanDefinition<?>> beanDefinitions, Class<?> forType) {

        List<BeanDefinition<?>> candidates = getBeanDefinitionsCompatibleWithCurrentOS(beanDefinitions);

        if (candidates.isEmpty())
            throw noBeanMatchingCurrentOS();

        if (candidates.size() == 1)
            return getFirstBeanDefinitionInList(candidates);

        List<BeanDefinition<?>> primaries = getPrimaryBeanDefinitionList(candidates);

        if (primaries.size() == 1)
            return getFirstBeanDefinitionInList(primaries);

        throw multipleBeansFoundForType(forType, candidates);
    }

    /**
     * Indique si la définition possède un nom non vide.
     *
     * @param beanDefinition définition à vérifier
     * @return {@code true} si le nom est renseigné
     */
    boolean isNotEmptyBeanDefinitionName(BeanDefinition<?> beanDefinition) {
        return !beanDefinition.getName().isEmpty();
    }

    /**
     * Retourne les interfaces implémentées par un type.
     *
     * @param type type cible
     * @return interfaces implémentées
     */
    Class<?>[] getInterfacesForType(Class<?> type) {
        return type.getInterfaces();
    }

    /**
     * Retourne la superclasse directe d'un type.
     *
     * @param type type cible
     * @return superclasse, ou {@code null}
     */
    Class<?> getSuperclassForType(Class<?> type) {
        return type.getSuperclass();
    }

    /**
     * Retourne les interfaces du type principal d'une définition.
     *
     * @param beanDefinition définition cible
     * @return interfaces implémentées
     */
    Class<?>[] getInterfacesForBeanDefinition(BeanDefinition<?> beanDefinition) {
        return getInterfacesForType(beanDefinition.getType());
    }

    /**
     * Retourne la superclasse du type principal d'une définition.
     *
     * @param beanDefinition définition cible
     * @return superclasse, ou {@code null}
     */
    Class<?> getSuperclassForBeanDefinition(BeanDefinition<?> beanDefinition) {
        return getSuperclassForType(beanDefinition.getType());
    }

    /**
     * Enregistre une définition dans l'index par type.
     *
     * @param type clé de type
     * @param beanDefinition définition à ajouter
     */
    void registerBeanDefinitionForType(Class<?> type, BeanDefinition<?> beanDefinition) {
        BEANS_BY_TYPE.computeIfAbsent(type, this::createBeanList)
                .add(beanDefinition);
    }

    /**
     * Enregistre une définition dans l'index par nom.
     *
     * @param beanDefinition définition nommée
     */
    void registerBeanDefinitionForName(BeanDefinition<?> beanDefinition) {
        BEANS_BY_NAME.computeIfAbsent(beanDefinition.getName(), k -> createBeanList(ArrayList.class))
                .add(beanDefinition);
    }

    /**
     * Lit une entrée du cache singleton.
     *
     * @param def définition cible
     * @return instance en cache, ou {@code null}
     */
    Object getBeanDefinitionInSingletonCache(BeanDefinition<?> def) {
        return SINGLETON_CACHE.get(def);
    }

    /**
     * Ajoute une instance dans le cache singleton.
     *
     * @param beanDefinition définition associée
     * @param instance instance à stocker
     */
    void addBeanDefinitionInSingletonCache(BeanDefinition<?> beanDefinition, Object instance) {
        SINGLETON_CACHE.put(beanDefinition, instance);
    }

    /**
     * Crée une nouvelle liste de définitions.
     *
     * @param type type demandé (non utilisé, compatible avec {@code computeIfAbsent})
     * @return nouvelle liste vide
     */
    List<BeanDefinition<?>> createBeanList(Class<?> type) {
        return new ArrayList<>();
    }

    /**
     * Filtre les définitions compatibles avec l'OS courant.
     *
     * @param beanDefinitions définitions candidates
     * @return définitions compatibles
     */
    List<BeanDefinition<?>> getBeanDefinitionsCompatibleWithCurrentOS(List<BeanDefinition<?>> beanDefinitions) {
        return beanDefinitions.stream()
                .filter(OperatingSystemHelper::isCompatibleWithCurrentOS)
                .toList();
    }

    /**
     * Extrait les définitions marquées primaires.
     *
     * @param beanDefinitions définitions à filtrer
     * @return définitions primaires
     */
    static List<BeanDefinition<?>> getPrimaryBeanDefinitionList(List<BeanDefinition<?>> beanDefinitions) {
        return beanDefinitions.stream()
                .filter(BeanDefinition::isPrimary)
                .toList();
    }

    /**
     * Extrait les définitions assignables au type attendu.
     *
     * @param beanDefinitions définitions candidates
     * @param expectedType type attendu
     * @return définitions compatibles avec le type attendu
     */
    static List<BeanDefinition<?>> getBeanDefinitionsAssignableToType(List<BeanDefinition<?>> beanDefinitions, Class<?> expectedType) {
        return beanDefinitions.stream()
                .filter(beanDefinition -> expectedType.isAssignableFrom(beanDefinition.getType()))
                .toList();
    }

    /**
     * Retourne le premier élément d'une liste de définitions.
     *
     * @param beanDefinitions liste non vide
     * @return première définition
     */
    static BeanDefinition<?> getFirstBeanDefinitionInList(List<BeanDefinition<?>> beanDefinitions) {
        return beanDefinitions.get(0);
    }

    /**
     * Comparator pour trier les instances par nom de classe.
     *
     * @return Comparator pour trier les instances par nom de classe.
     */
    static Comparator<Object> getClassNameComparator() {
        return (object1, object2) -> {
            String className1 = object1.getClass().getCanonicalName();
            String className2 = object2.getClass().getCanonicalName();
            return className1.compareTo(className2);
        };
    }
}
