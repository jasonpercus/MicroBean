package com.jasonpercus.microbean.infrastructure.factory;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.api.OS.ALL;
import static com.jasonpercus.microbean.api.Scope.SINGLETON;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.classIsNotAnnotatedWithComponentAnnotation;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.methodIsNotAnnotated;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isAnnotatedBean;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isAnnotatedEntryPointService;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isAnnotatedPrimary;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isAnnotatedService;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isNotVirtuallyAnnotatedBean;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.OS;
import com.jasonpercus.microbean.api.Scope;
import com.jasonpercus.microbean.api.Service;

/**
 * Représente la définition d'un bean géré par le conteneur MicroBean.
 * <p>
 * Une instance de cette classe encapsule les métadonnées nécessaires à la
 * création et à la résolution d'un bean : type exposé, stratégie de création,
 * nom éventuel, caractère prioritaire, portée et contrainte de système
 * d'exploitation.
 * </p>
 *
 * @param <T> type concret du bean manipulé par la factory interne
 */
public class BeanDefinition<T> {

    /**
     * Type principal du bean enregistré dans le contexte.
     */
    private final Class<?> type;

    /**
     * Factory utilisée pour instancier le bean à la demande.
     */
    private final BeanFactory<T> factory;

    /**
     * Nom explicite du bean (chaîne vide si non renseigné).
     */
    private final String name;

    /**
     * Indique si ce bean est marqué comme prioritaire pour la résolution.
     */
    private final boolean primary;

    /**
     * Portée (cycle de vie) du bean.
     */
    private final Scope scope;

    /**
     * Systèmes d'exploitation ciblés par le bean (utile pour les @Adapter).
     */
    private final OS[] os;

    /**
     * Construit une définition de bean à partir d'une méthode de configuration
     * annotée {@link Bean}.
     *
     * @param configurationInstance instance de la classe de configuration
     * @param method méthode factory annotée {@link Bean}
     * @param context contexte d'injection utilisé par la factory
     * @throws RuntimeException si la méthode n'est pas annotée {@link Bean}
     */
    public BeanDefinition(Object configurationInstance, Method method, Context context) {

        if (!isAnnotatedBean(method)) {
            throw methodIsNotAnnotated(method);
        }

        Bean bean = method.getAnnotation(Bean.class);

        this.type = method.getReturnType();
        this.factory = new BeanFactory<>(configurationInstance, method, context);
        this.name = bean.name();
        this.primary = isAnnotatedPrimary(method);
        this.scope = bean.scope();
        this.os = new OS[]{ALL};
    }

    /**
     * Construit une définition de bean à partir d'une classe composante
     * (@Service, @Adapter ou @EntryPointService).
     *
     * @param type type de la classe composante
     * @param context contexte d'injection utilisé par la factory
     * @throws RuntimeException si la classe n'est pas un composant supporté
     */
    public BeanDefinition(Class<T> type, Context context) {

        if (isNotVirtuallyAnnotatedBean(type)) {
            throw classIsNotAnnotatedWithComponentAnnotation(type);
        }

        OtherValues otherValues = extractOtherValues(type);

        this.type = type;
        this.factory = new BeanFactory<>(type, context);
        this.name = otherValues.name;
        this.primary = isAnnotatedPrimary(type);
        this.scope = otherValues.scope;
        this.os = otherValues.os;
    }

    /**
     * Construit une définition de singleton pré-instancié.
     *
     * @param type type exposé dans le contexte
     * @param instance instance à réutiliser pour toutes les résolutions
     */
    public BeanDefinition(Class<T> type, T instance) {
        this.type = type;
        this.factory = new BeanFactory<>(instance);
        this.name = "";
        this.primary = false;
        this.scope = SINGLETON;
        this.os = new OS[]{ALL};
    }

    /**
     * Construit une définition de singleton pré-instancié.
     *
     * @param type type exposé dans le contexte
     * @param instance instance à réutiliser pour toutes les résolutions
     * @param name nom explicite du bean
     */
    public BeanDefinition(Class<T> type, T instance, String name) {
        this.type = type;
        this.factory = new BeanFactory<>(instance);
        this.name = Optional.ofNullable(name).map(String::trim).orElse("");
        this.primary = false;
        this.scope = SINGLETON;
        this.os = new OS[]{ALL};
    }

    /**
     * Crée une instance du bean via la factory associée.
     *
     * @return instance du bean
     */
    public Object createBean() {
        return factory.create();
    }

    /**
     * Retourne le type principal du bean.
     *
     * @return type du bean
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Retourne le nom explicite du bean.
     *
     * @return nom du bean, ou chaîne vide si non défini
     */
    public String getName() {
        return name;
    }

    /**
     * Indique si ce bean est prioritaire.
     *
     * @return {@code true} si le bean est annoté {@code @Primary}, sinon {@code false}
     */
    public boolean isPrimary() {
        return primary;
    }

    /**
     * Retourne la portée du bean.
     *
     * @return portée configurée
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * Retourne les systèmes d'exploitation associés au bean.
     *
     * @return OS ciblés, ou {@link OS#ALL} si non restreint
     */
    public OS[] getOs() {
        return os;
    }

    /**
     * Extrait les valeurs métier complémentaires selon l'annotation portée par
     * la classe composante.
     *
     * @param clazz classe composante annotée
     * @return valeurs de portée, nom et OS à appliquer à la définition
     */
    private OtherValues extractOtherValues(AnnotatedElement clazz) {

        if (isAnnotatedEntryPointService(clazz)) {

            return new OtherValues(SINGLETON, "", new OS[]{ALL});
        } else if (isAnnotatedService(clazz)) {

            Service serviceAnnotation = null;
            if (clazz.isAnnotationPresent(Service.class)) {
                serviceAnnotation = clazz.getAnnotation(Service.class);
            } else {

                for (Annotation annotation : clazz.getDeclaredAnnotations()) {
                    if (annotation.annotationType().isAnnotationPresent(Service.class)) {
                        serviceAnnotation = extractService(annotation);
                        break;
                    }
                }
            }

            assert serviceAnnotation != null : "Service annotation should not be null";
            return new OtherValues(serviceAnnotation.scope(), serviceAnnotation.name(), new OS[]{ALL});
        } else {

            Adapter adapterAnnotation = null;
            if (clazz.isAnnotationPresent(Adapter.class)) {
                adapterAnnotation = clazz.getAnnotation(Adapter.class);
            } else {

                for (Annotation annotation : clazz.getDeclaredAnnotations()) {
                    if (annotation.annotationType().isAnnotationPresent(Adapter.class)) {
                        adapterAnnotation = extractAdapter(annotation);
                        break;
                    }
                }
            }

            assert adapterAnnotation != null : "Adapter annotation should not be null";
            return new OtherValues(adapterAnnotation.scope(), adapterAnnotation.name(), adapterAnnotation.os());
        }
    }

    private static Service extractService(Annotation annotation) {
        Service defaults = annotation.annotationType().getAnnotation(Service.class);

        if (defaults == null) {
            throw new IllegalArgumentException(
                    annotation.annotationType().getName() + " n'est pas annotée avec @Service");
        }

        return new Service() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Service.class;
            }

            @Override
            public String name() {
                return getValue(annotation, "name", String.class, defaults.name());
            }

            @Override
            public Scope scope() {
                return getValue(annotation, "scope", Scope.class, defaults.scope());
            }
        };
    }

    private static Adapter extractAdapter(Annotation annotation) {
        Adapter defaults = annotation.annotationType().getAnnotation(Adapter.class);

        if (defaults == null) {
            throw new IllegalArgumentException(
                    annotation.annotationType().getName() + " n'est pas annotée avec @Adapter");
        }

        return new Adapter() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Adapter.class;
            }

            @Override
            public String name() {
                return getValue(annotation, "name", String.class, defaults.name());
            }

            @Override
            public Scope scope() {
                return getValue(annotation, "scope", Scope.class, defaults.scope());
            }

            @Override
            public OS[] os() {
                return getValue(annotation, "os", OS[].class, defaults.os());
            }
        };
    }

    private static <T> T getValue(
            Annotation annotation,
            String methodName,
            Class<T> type,
            T defaultValue) {

        try {
            Method method = annotation.annotationType().getMethod(methodName);
            Object value = method.invoke(annotation);
            return type.cast(value);
        } catch (NoSuchMethodException e) {
            return defaultValue;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Porteur interne des valeurs dérivées des annotations composantes.
     *
     * @param scope portée à appliquer
     * @param name nom à appliquer
     * @param os OS à appliquer
     */
    private record OtherValues(Scope scope, String name, OS[] os) {}
}
