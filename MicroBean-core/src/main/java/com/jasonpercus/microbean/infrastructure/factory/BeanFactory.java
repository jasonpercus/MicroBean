package com.jasonpercus.microbean.infrastructure.factory;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.MicroBean.isEnabledDebugMicroBean;
import static com.jasonpercus.microbean.infrastructure.Constants.DEBUG_MESSAGE_CREATED_BEAN;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.cyclicDependencyDetected;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.failedToCallPostConstructMethod;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.failedToCreateBean;
import static com.jasonpercus.microbean.infrastructure.exception.ExceptionManager.invocationMethodFailed;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.getNamedValue;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isAnnotatedNamed;
import static com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper.isNotAnnotatedProfile;
import static com.jasonpercus.microbean.infrastructure.helpers.LogHelper.debug;
import static com.jasonpercus.microbean.infrastructure.helpers.StringHelper.abbreviateClassName;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.infrastructure.helpers.AnnotationHelper;
import com.jasonpercus.microbean.infrastructure.helpers.StringHelper;
import com.jasonpercus.microbean.infrastructure.validator.ProfileValidator;

/**
 * Fabrique de création de beans pour le conteneur MicroBean.
 * <p>
 * Cette classe prend en charge :
 * </p>
 * <ul>
 *   <li>la création via une méthode de configuration {@code @Bean},</li>
 *   <li>la création via un constructeur de classe composante,</li>
 *   <li>la résolution des paramètres injectés (par type ou via {@code @Named}),</li>
 *   <li>l'exécution des méthodes {@code @PostConstruct} compatibles avec le profil actif,</li>
 *   <li>la détection des dépendances cycliques au sein d'un thread.</li>
 * </ul>
 *
 * @param <T> type principal de bean produit par la fabrique
 */
public class BeanFactory<T> {

    /**
     * Pile de types en cours de construction, isolée par thread.
     * <p>
     * Utilisée pour détecter les cycles de dépendances pendant la création.
     * </p>
     */
    private static final ThreadLocal<Set<Class<?>>> CONSTRUCTING = ThreadLocal.withInitial(HashSet::new);

    /**
     * Stratégie de création concrète du bean, injectée via le constructeur.
     */
    private final Supplier<Object> factory;

    /**
     * Construit une fabrique basée sur une méthode de configuration.
     *
     * @param instance instance de classe de configuration porteuse de la méthode
     * @param method méthode factory à invoquer
     * @param context contexte d'injection utilisé pour résoudre les paramètres
     */
    public BeanFactory(Object instance, Method method, Context context) {
        this.factory = () -> {
            try {
                Object[] parameters = createParameters(method, context);
                Object bean =  method.invoke(instance, parameters);

                showCreatedBeanDebugMessage(bean, parameters);

                return bean;
            } catch (Exception e) {
                throw invocationMethodFailed(method, e);
            }
        };
    }

    /**
     * Construit une fabrique basée sur une classe à instancier.
     *
     * @param type type de classe à créer
     * @param context contexte d'injection utilisé pour résoudre les paramètres
     */
    public BeanFactory(Class<T> type, Context context) {
        this.factory = () -> create(type, context);
    }

    /**
     * Construit une fabrique à partir d'une instance pré-existante (singleton
     * pré-instancié).
     * <p>
     * Chaque appel à {@link #create()} retournera toujours la même instance.
     * </p>
     *
     * @param instance instance à encapsuler ; ne doit pas être {@code null}
     * @throws NullPointerException si {@code instance} est {@code null}
     */
    public BeanFactory(T instance) {

        Objects.requireNonNull(instance, "instance must not be null");

        this.factory = () -> instance;
    }

    /**
     * Crée une instance via la stratégie de création configurée.
     *
     * @return instance créée
     */
    @SuppressWarnings("unchecked")
    public T create() {
        return (T) factory.get();
    }

    /**
     * Crée un bean depuis sa classe en résolvant le constructeur avec le plus
     * grand nombre de paramètres.
     *
     * @param type type de bean à créer
     * @param context contexte d'injection pour résoudre les paramètres
     * @param <T> type de retour attendu
     * @return instance créée
     * @throws RuntimeException en cas de dépendance cyclique ou d'échec de création
     */
    public static <T> T create(Class<T> type, Context context) {
        Set<Class<?>> constructing = CONSTRUCTING.get();

        if (constructing.contains(type))
            throw cyclicDependencyDetected(type);

        constructing.add(type);

        Constructor<T> constructor = getBeanConstructorWithMaxParameters(type);

        Object[] parameters = createParameters(constructor, context);

        try {
            return create(constructor, parameters);
        } catch (Exception e) {
            throw failedToCreateBean(type, e);
        } finally {
            constructing.remove(type);

            if (constructing.isEmpty())
                CONSTRUCTING.remove();
        }
    }

    /**
     * Crée le bean via un constructeur déjà sélectionné.
     *
     * @param constructor constructeur à invoquer
     * @param parameters paramètres injectés à transmettre
     * @param <T> type du bean
     * @return instance créée
     * @throws InstantiationException si la classe ne peut pas être instanciée
     * @throws IllegalAccessException si le constructeur n'est pas accessible
     * @throws InvocationTargetException si le constructeur lève une exception
     */
    static <T> T create(Constructor<T> constructor, Object[] parameters) throws InstantiationException, IllegalAccessException, InvocationTargetException {

        T bean = constructor.newInstance(parameters);

        showCreatedBeanDebugMessage(bean, parameters);

        invokePostConstruct(bean);
        return bean;
    }

    /**
     * Recherche et exécute les méthodes {@code @PostConstruct} d'un bean.
     * <p>
     * La recherche couvre la classe, sa hiérarchie, puis ses interfaces.
     * Une signature de méthode n'est exécutée qu'une fois.
     * </p>
     *
     * @param bean instance cible
     */
    static void invokePostConstruct(Object bean) {

        Class<?> beanClass = bean.getClass();

        Map<MethodSignature, Method> methodsToCall = new LinkedHashMap<>();

        while (beanClass != Object.class) {
            Method[] methodsInBean = beanClass.getDeclaredMethods();
            filterMethodsIfIsAnnotatedPostConstruct(methodsInBean, methodsToCall);
            beanClass = beanClass.getSuperclass();
        }

        for (Class<?> iface : bean.getClass().getInterfaces()) {
            Method[] methodsInInterface = iface.getMethods();
            filterMethodsIfIsAnnotatedPostConstruct(methodsInInterface, methodsToCall);
        }

        // Invocation
        methodsToCall.values().forEach(method -> invokePostConstruct(bean, method));
    }

    /**
     * Exécute une méthode {@code @PostConstruct} sur un bean donné.
     *
     * @param bean instance cible
     * @param method méthode à invoquer
     * @throws RuntimeException si l'invocation échoue
     */
    static void invokePostConstruct(Object bean, Method method) {
        try {
            method.setAccessible(true);
            method.invoke(bean);
        } catch (Exception e) {
            throw failedToCallPostConstructMethod(method, e);
        }
    }

    /**
     * Résout un paramètre d'injection.
     *
     * @param parameter paramètre à résoudre
     * @param context contexte d'injection
     * @return instance injectée correspondant au paramètre
     */
    static Object resolveParameter(Parameter parameter, Context context) {
        if (isAnnotatedNamed(parameter)) {
            String name = getNamedValue(parameter);
            return context.getBean(parameter.getType(), name);
        } else {
            return context.getBean(parameter.getType());
        }
    }

    /**
     * Affiche un message de debug de création de bean lorsque le mode debug
     * MicroBean est actif.
     *
     * @param beanCreated instance créée
     * @param parametersInjected paramètres effectivement injectés
     * @param <T> type de l'instance créée
     */
    static <T> void showCreatedBeanDebugMessage(T beanCreated, Object[] parametersInjected) {
        if (isEnabledDebugMicroBean()) {

            String className = abbreviateClassName(beanCreated.getClass());
            String parametersNames = listObjectNames(parametersInjected);

            debug(DEBUG_MESSAGE_CREATED_BEAN, className, parametersNames);
        }
    }

    /**
     * Construit le tableau des paramètres injectés d'une méthode/constructeur.
     *
     * @param function exécutable cible
     * @param context contexte d'injection
     * @return tableau des instances résolues
     */
    static Object[] createParameters(Executable function, Context context) {
        return Arrays.stream(function.getParameters())
                .map(parameter -> resolveParameter(parameter, context))
                .toArray();
    }

    /**
     * Transforme les objets injectés en représentation abrégée lisible.
     *
     * @param parametersInjected objets injectés
     * @return chaîne formatée du type {@code [a.b.Type1, a.b.Type2]}
     */
    static String listObjectNames(Object[] parametersInjected) {
        return Arrays.stream(parametersInjected)
                .map(Object::getClass)
                .map(StringHelper::abbreviateClassName)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    /**
     * Filtre et enregistre les méthodes annotées {@code @PostConstruct}
     * compatibles avec le profil actif.
     *
     * @param methodsInClassOrInterface méthodes candidates
     * @param methodsToCall dictionnaire de méthodes à appeler, dédupliqué par signature
     */
    static void filterMethodsIfIsAnnotatedPostConstruct(Method[] methodsInClassOrInterface, Map<MethodSignature, Method> methodsToCall) {
        Arrays.stream(methodsInClassOrInterface)
                .filter(AnnotationHelper::isAnnotatedPostConstruct)
                .filter(BeanFactory::matchesActiveProfile)
                .forEach(method -> {
                    MethodSignature methodSignature = new MethodSignature(method);
                    methodsToCall.putIfAbsent(methodSignature, method);
                });
    }

    /**
     * Détermine si une méthode est exécutable selon le profil actif.
     *
     * @param method méthode candidate
     * @return {@code true} si la méthode est autorisée pour le profil courant
     */
    static boolean matchesActiveProfile(Method method) {

        if (isNotAnnotatedProfile(method))
            return true;

        ProfileValidator profileValidator = new ProfileValidator(method.getAnnotation(Profile.class));

        return profileValidator.validate();
    }

    /**
     * Retourne le constructeur comportant le plus de paramètres.
     *
     * @param clazz classe cible
     * @param <T> type de classe
     * @return constructeur sélectionné
     */
    @SuppressWarnings("unchecked")
    static <T> Constructor<T> getBeanConstructorWithMaxParameters(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .map(constructor -> (Constructor<T>) constructor)
                .orElseThrow();
    }

    /**
     * Signature technique utilisée pour dédupliquer les méthodes
     * {@code @PostConstruct} détectées.
     */
    private static class MethodSignature {

        /**
         * Nom de la méthode.
         */
        private final String name;

        /**
         * Types des paramètres de la méthode.
         */
        private final Class<?>[] paramTypes;

        /**
        * Construit une signature à partir d'une méthode.
        *
        * @param method méthode source
        */
        public MethodSignature(Method method) {
            Objects.requireNonNull(method, "method must not be null");
            this.name = method.getName();
            this.paramTypes = method.getParameterTypes();
        }

        /**
         * Compare deux signatures de méthode.
         *
         * @param o objet à comparer
         * @return {@code true} si nom et types de paramètres sont identiques
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MethodSignature other)) return false;
            return name.equals(other.name) && Arrays.equals(paramTypes, other.paramTypes);
        }

        /**
         * Calcule le hash code cohérent avec {@link #equals(Object)}.
         *
         * @return hash code de la signature
         */
        @Override
        public int hashCode() {
            return Objects.hash(name, Arrays.hashCode(paramTypes));
        }
    }
}
