package com.jasonpercus.microbean.infrastructure.helpers;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.api.Named;
import com.jasonpercus.microbean.api.PostConstruct;
import com.jasonpercus.microbean.api.Primary;
import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.api.Service;

/**
 * Classe utilitaire pour les annotations utilisées dans le framework.
 */
public class AnnotationHelper {

    /**
     * Vérifie si l'élément annoté est marqué comme primaire.
     *
     * @param element l'élément annoté à vérifier
     * @return true si l'élément est annoté avec @Primary, false sinon
     */
    public static boolean isAnnotatedPrimary(AnnotatedElement element) {
        return element.isAnnotationPresent(Primary.class);
    }

    /**
     * Vérifie si l'élément annoté est marqué comme un bean.
     *
     * @param element l'élément annoté à vérifier
     * @return true si l'élément est annoté avec @Bean, false sinon
     */
    public static boolean isAnnotatedBean(AnnotatedElement element) {
        return element.isAnnotationPresent(Bean.class);
    }

    /**
     * Vérifie si l'élément annoté est marqué comme un adapter.
     *
     * @param element l'élément annoté à vérifier
     * @return true si l'élément est annoté avec @Adapter, false sinon
     */
    public static boolean isAnnotatedAdapter(AnnotatedElement element) {

        boolean hasAdapterAnnotation = element.isAnnotationPresent(Adapter.class);

        if (!hasAdapterAnnotation) {
            Annotation[] annotations = element.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().isAnnotationPresent(Adapter.class)) {
                    hasAdapterAnnotation = true;
                    break;
                }
            }
        }

        return hasAdapterAnnotation;
    }

    /**
     * Vérifie si l'élément annoté est marqué comme un service.
     *
     * @param element l'élément annoté à vérifier
     * @return true si l'élément est annoté avec @Service, false sinon
     */
    public static boolean isAnnotatedService(AnnotatedElement element) {

        boolean hasServiceAnnotation = element.isAnnotationPresent(Service.class);

        if (!hasServiceAnnotation) {
            Annotation[] annotations = element.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().isAnnotationPresent(Service.class)) {
                    hasServiceAnnotation = true;
                    break;
                }
            }
        }

        return hasServiceAnnotation;
    }

    /**
     * Vérifie si l'élément annoté est marqué comme un service d'entrée.
     *
     * @param element l'élément annoté à vérifier
     * @return true si l'élément est annoté avec @EntryPointService, false sinon
     */
    public static boolean isAnnotatedEntryPointService(AnnotatedElement element) {
        return element.isAnnotationPresent(EntryPointService.class);
    }

    /**
     * Vérifie si l'élément annoté est marqué comme une configuration.
     *
     * @param element l'élément annoté à vérifier
     * @return true si l'élément est annoté avec @Configuration, false sinon
     */
    public static boolean isAnnotatedConfiguration(AnnotatedElement element) {
        return element.isAnnotationPresent(Configuration.class);
    }

    /**
     * Vérifie si le paramètre est annoté avec @Named.
     *
     * @param param le paramètre à vérifier
     * @return true si le paramètre est annoté avec @Named, false sinon
     */
    public static boolean isAnnotatedNamed(Parameter param) {
        return param.isAnnotationPresent(Named.class);
    }

    /**
     * Vérifie si la méthode est annotée avec @PostConstruct.
     *
     * @param method la méthode à vérifier
     * @return true si la méthode est annotée avec @PostConstruct, false sinon
     */
    public static boolean isAnnotatedPostConstruct(Method method) {
        return method.isAnnotationPresent(PostConstruct.class);
    }

    /**
     * Vérifie si l'élément annoté est marqué avec @Profile.
     *
     * @param element l'élément annoté à vérifier
     * @return true si l'élément est annoté avec @Profile, false sinon
     */
    public static boolean isAnnotatedProfile(AnnotatedElement element) {
        return element.isAnnotationPresent(Profile.class);
    }

    /**
     * Vérifie si l'élément annoté est marqué avec @Condition.
     *
     * @param element l'élément annoté à vérifier
     * @return true si l'élément est annoté avec @Condition, false sinon
     */
    public static boolean isAnnotatedCondition(AnnotatedElement element) {
        return element.isAnnotationPresent(Condition.class);
    }

    /**
     * Vérifie si l'élément annoté n'est pas marqué avec @Profile.
     *
     * @param element l'élément annoté à vérifier
     * @return true si l'élément n'est pas annoté avec @Profile, false sinon
     */
    public static boolean isNotAnnotatedProfile(AnnotatedElement element) {
        return !isAnnotatedProfile(element);
    }

    /**
     * Vérifie si l'élément annoté n'est pas marqué avec @Condition.
     *
     * @param element l'élément annoté à vérifier
     * @return true si l'élément n'est pas annoté avec @Condition, false sinon
     */
    public static boolean isNotAnnotatedCondition(AnnotatedElement element) {
        return !isAnnotatedCondition(element);
    }

    /**
     * Vérifie si la classe n'est pas annotée avec @Service, @Adapter ou @EntryPointService.
     *
     * @param clazz la classe à vérifier
     * @return true si la classe n'est pas annotée avec ces annotations, false sinon
     */
    public static boolean isNotVirtuallyAnnotatedBean(Class<?> clazz) {
        return isNotAnnotatedService(clazz) && isNotAnnotatedAdapter(clazz) && isNotAnnotatedEntryPointService(clazz);
    }

    /**
     * Vérifie si la méthode n'est pas annotée avec @Bean.
     *
     * @param method la méthode à vérifier
     * @return true si la méthode n'est pas annotée avec @Bean, false sinon
     */
    public static boolean isNotBeanMethod(Method method) {
        return !method.isAnnotationPresent(Bean.class);
    }

    /**
     * Vérifie si la méthode n'est pas annotée avec @PostConstruct.
     *
     * @param method la méthode à vérifier
     * @return true si la méthode n'est pas annotée avec @PostConstruct, false sinon
     */
    public static boolean isNotPublicMethod(Method method) {
        return !Modifier.isPublic(method.getModifiers());
    }

    /**
     * Vérifie si la classe n'est pas annotée avec @Service, @Adapter ou @EntryPointService.
     *
     * @param clazz la classe à vérifier
     * @return true si la classe n'est pas annotée avec ces annotations, false sinon
     */
    public static boolean isNotComponentClass(Class<?> clazz) {
        return isNotAnnotatedService(clazz) && isNotAnnotatedEntryPointService(clazz) && isNotAnnotatedAdapter(clazz);
    }

    /**
     * Vérifie si la classe n'est pas annotée avec @Service.
     *
     * @param clazz la classe à vérifier
     * @return true si la classe n'est pas annotée avec @Service, false sinon
     */
    public static boolean isNotAnnotatedService(Class<?> clazz) {
        return !isAnnotatedService(clazz);
    }

    /**
     * Vérifie si la classe n'est pas annotée avec @EntryPointService.
     *
     * @param clazz la classe à vérifier
     * @return true si la classe n'est pas annotée avec @EntryPointService, false sinon
     */
    public static boolean isNotAnnotatedEntryPointService(Class<?> clazz) {
        return !isAnnotatedEntryPointService(clazz);
    }

    /**
     * Vérifie si la classe n'est pas annotée avec @Adapter.
     *
     * @param clazz la classe à vérifier
     * @return true si la classe n'est pas annotée avec @Adapter, false sinon
     */
    public static boolean isNotAnnotatedAdapter(Class<?> clazz) {
        return !isAnnotatedAdapter(clazz);
    }

    /**
     * Vérifie si la classe n'est pas annotée avec @EntryPointService.
     *
     * @param entryPoint la classe d'entrée à vérifier
     * @return true si la classe n'est pas annotée avec @EntryPointService, false sinon
     */
    public static boolean isNotAnnotatedWithEntryPointService(Class<? extends ApplicationEntryPoint> entryPoint) {
        return !entryPoint.isAnnotationPresent(EntryPointService.class);
    }

    /**
     * Vérifie si la classe n'est pas annotée avec @MicroBeanApplication.
     *
     * @param clazz la classe à vérifier
     * @return true si la classe n'est pas annotée avec @MicroBeanApplication, false sinon
     */
    public static boolean isNotAnnotatedWithMicroBeanApplication(Class<?> clazz) {
        return !clazz.isAnnotationPresent(MicroBeanApplication.class);
    }

    /**
     * Récupère la valeur de l'annotation @Named d'un paramètre.
     *
     * @param param le paramètre annoté avec @Named
     * @return la valeur de l'annotation @Named
     */
    public static String getNamedValue(Parameter param) {
        return param.getAnnotation(Named.class).value();
    }
}
