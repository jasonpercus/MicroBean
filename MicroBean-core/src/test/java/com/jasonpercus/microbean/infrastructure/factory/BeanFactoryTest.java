package com.jasonpercus.microbean.infrastructure.factory;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Environment;
import com.jasonpercus.microbean.api.Named;
import com.jasonpercus.microbean.api.PostConstruct;
import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.api.Service;
import com.jasonpercus.microbean.infrastructure.helpers.StringHelper;

@DisplayName("Tests unitaires de la classe BeanFactory")
class BeanFactoryTest {

    private final String originalProfile = System.getProperty("app.profile");
    private final String originalDebug = System.getProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG);
    private final PrintStream originalOut = System.out;

    @AfterEach
    @SuppressWarnings("all")
    void doit_restaurer_les_proprietes_et_l_etat_statique_apres_chaque_test() {

        // Given
        String profileAttendu = originalProfile;
        String debugAttendu = originalDebug;

        // When
        restoreProperty("app.profile", originalProfile);
        restoreProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG, originalDebug);
        System.setOut(originalOut);
        ParentPostConstructService.parentCalled = false;
        MultiPostConstructService.childCalled = false;
        PostConstructFlags.interfaceCalled = false;
        ProfiledPostConstructService.profileCalled = false;

        // Then
        assertThat(System.getProperty("app.profile")).isEqualTo(profileAttendu);
        assertThat(System.getProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG)).isEqualTo(debugAttendu);
        assertThat(System.out).isSameAs(originalOut);
        assertThat(ParentPostConstructService.parentCalled).isFalse();
        assertThat(MultiPostConstructService.childCalled).isFalse();
        assertThat(PostConstructFlags.interfaceCalled).isFalse();
        assertThat(ProfiledPostConstructService.profileCalled).isFalse();
    }

    @Test
    @DisplayName("Doit créer un bean depuis une méthode @Bean et injecter ses dépendances")
    void doit_creer_un_bean_depuis_une_methode_bean_et_injecter_ses_dependances() throws Exception {

        // Given
        Context context = new Context();
        context.register(new BeanDefinition<>(DependanceService.class, context));

        ConfigurationFactoryFixture fixture = new ConfigurationFactoryFixture();
        Method method = ConfigurationFactoryFixture.class.getDeclaredMethod("creer_depuis_methode", DependanceService.class);
        BeanFactory<BeanDepuisMethode> beanFactory = new BeanFactory<>(fixture, method, context);

        // When
        BeanDepuisMethode bean = beanFactory.create();

        // Then
        assertThat(bean).isNotNull();
        assertThat(bean.dependance).isNotNull();
        assertThat(bean.dependance).isInstanceOf(DependanceService.class);
    }

    @Test
    @DisplayName("Doit lever une exception si l'invocation de la méthode @Bean échoue")
    void doit_lever_une_exception_si_l_invocation_de_la_methode_bean_echoue() throws Exception {

        // Given
        Context context = new Context();
        ConfigurationFactoryFixture fixture = new ConfigurationFactoryFixture();
        Method method = ConfigurationFactoryFixture.class.getDeclaredMethod("creer_en_echec");
        BeanFactory<Object> beanFactory = new BeanFactory<>(fixture, method, context);

        // When & Then
        assertThatThrownBy(beanFactory::create)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to invoke")
                .hasMessageContaining("creer_en_echec");
    }

    @Test
    @DisplayName("Doit créer un bean via le constructeur ayant le plus de paramètres")
    void doit_creer_un_bean_via_le_constructeur_ayant_le_plus_de_parametres() {

        // Given
        Context context = new Context();
        context.register(new BeanDefinition<>(DependanceService.class, context));

        // When
        ConstructeurMaxParamService bean = BeanFactory.create(ConstructeurMaxParamService.class, context);

        // Then
        assertThat(bean.constructeurUtilise).isEqualTo("MAX");
        assertThat(bean.dependance).isNotNull();
    }

    @Test
    @DisplayName("Doit résoudre une dépendance nommée avec @Named")
    void doit_resoudre_une_dependance_nommee_avec_named() {

        // Given
        Context context = new Context();
        context.register(new BeanDefinition<>(DependanceNommeeService.class, context));

        // When
        ServiceAvecNamed bean = BeanFactory.create(ServiceAvecNamed.class, context);

        // Then
        assertThat(bean).isNotNull();
        assertThat(bean.dependanceNommee).isNotNull();
        assertThat(bean.dependanceNommee).isInstanceOf(DependanceNommeeService.class);
    }

    @Test
    @DisplayName("Doit injecter Environment dans un bean quand il est enregistré dans le context")
    void doit_injecter_environment_dans_un_bean_quand_il_est_enregistre_dans_le_context() {

        // Given
        Context context = new Context();
        Environment environment = new Environment(new String[]{"--profile=dev"});
        context.registerSingleton(Environment.class, environment);

        // When
        ServiceAvecEnvironment bean = BeanFactory.create(ServiceAvecEnvironment.class, context);

        // Then
        assertThat(bean).isNotNull();
        assertThat(bean.environment).isSameAs(environment);
        assertThat(bean.environment.getArguments().getArgs()).containsExactly("--profile=dev");
    }

    @Test
    @DisplayName("Doit exécuter les méthodes @PostConstruct de la classe, superclasse et interface")
    void doit_executer_les_methodes_postconstruct_de_la_classe_superclasse_et_interface() {

        // Given
        Context context = new Context();

        // When
        MultiPostConstructService bean = BeanFactory.create(MultiPostConstructService.class, context);

        // Then
        assertThat(bean).isNotNull();
        assertThat(ParentPostConstructService.parentCalled).isTrue();
        assertThat(MultiPostConstructService.childCalled).isTrue();
        assertThat(PostConstructFlags.interfaceCalled).isTrue();
    }

    @Test
    @DisplayName("Doit ignorer une méthode @PostConstruct quand son profil n'est pas actif")
    void doit_ignorer_une_methode_postconstruct_quand_son_profil_n_est_pas_actif() {

        // Given
        System.setProperty("app.profile", "prod");
        Context context = new Context();

        // When
        ProfiledPostConstructService bean = BeanFactory.create(ProfiledPostConstructService.class, context);

        // Then
        assertThat(bean).isNotNull();
        assertThat(ProfiledPostConstructService.profileCalled).isFalse();
    }

    @Test
    @DisplayName("Doit lever une exception quand une méthode @PostConstruct échoue")
    void doit_lever_une_exception_quand_une_methode_postconstruct_echoue() {

        // Given
        Context context = new Context();

        // When
        Throwable throwable = org.assertj.core.api.ThrowableAssert.catchThrowable(
                () -> BeanFactory.create(FailingPostConstructService.class, context)
        );

        // Then
        assertThat(throwable).isInstanceOf(RuntimeException.class);
        assertThat(containsInChain(throwable, "Failed to call")).isTrue();
        assertThat(containsInChain(throwable, "PostConstruct")).isTrue();
    }

    @Test
    @DisplayName("Doit lever une exception quand le constructeur du bean échoue")
    void doit_lever_une_exception_quand_le_constructeur_du_bean_echoue() {

        // Given
        Context context = new Context();

        // When & Then
        assertThatThrownBy(() -> BeanFactory.create(ConstructorEnErreurService.class, context))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to create bean")
                .hasMessageContaining("ConstructorEnErreurService");
    }

    @Test
    @DisplayName("Doit détecter une dépendance cyclique")
    void doit_detecter_une_dependance_cyclique() {

        // Given
        Context context = new Context();
        context.register(new BeanDefinition<>(CyclicService.class, context));

        // When & Then
        assertThatThrownBy(() -> context.getBean(CyclicService.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cyclic dependency detected")
                .hasMessageContaining("CyclicService");
    }

    @Test
    @DisplayName("Doit ignorer l'invocation des PostConstruct quand le bean est un Object simple")
    void doit_ignorer_l_invocation_des_postconstruct_quand_le_bean_est_un_object_simple() throws Exception {

        // Given
        Method method = BeanFactory.class.getDeclaredMethod("invokePostConstruct", Object.class);
        method.setAccessible(true);
        Object bean = new Object();

        // When
        Object result = method.invoke(null, bean);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Doit afficher un message de debug quand la création de bean est tracée")
    void doit_afficher_un_message_de_debug_quand_la_creation_de_bean_est_tracee() throws Exception {

        // Given
        MicroBean.setEnabledDebugMicroBean(true);
        BeanDepuisMethode bean = new BeanDepuisMethode(new DependanceService());
        Object[] parameters = new Object[]{bean.dependance};
        Method method = BeanFactory.class.getDeclaredMethod("showCreatedBeanDebugMessage", Object.class, Object[].class);
        method.setAccessible(true);

        // When
        String output = captureOutput(() -> invokeReflective(method, null, bean, parameters));

        // Then
        String expectedClassName = StringHelper.abbreviateClassName(BeanDepuisMethode.class);
        String expectedParameters = "[" + StringHelper.abbreviateClassName(DependanceService.class) + "]";
        assertThat(output).contains("Created bean: " + expectedClassName + " <= " + expectedParameters);
    }

    @Test
    @DisplayName("Ne doit ne rien afficher comme message de debug quand la création de bean n'est pas tracée")
    void doit_rien_afficher_comme_debug_quand_la_creation_de_bean_n_est_pas_tracee() throws Exception {

        // Given
        MicroBean.setEnabledDebugMicroBean(false);
        BeanDepuisMethode bean = new BeanDepuisMethode(new DependanceService());
        Object[] parameters = new Object[]{bean.dependance};
        Method method = BeanFactory.class.getDeclaredMethod("showCreatedBeanDebugMessage", Object.class, Object[].class);
        method.setAccessible(true);

        // When
        String output = captureOutput(() -> invokeReflective(method, null, bean, parameters));

        // Then
        assertThat(output).isEqualTo("");
    }

    @Test
    @DisplayName("Doit lister les noms abrégés des objets injectés")
    void doit_lister_les_noms_abreges_des_objets_injectes() throws Exception {

        // Given
        Method method = BeanFactory.class.getDeclaredMethod("listObjectNames", Object[].class);
        method.setAccessible(true);
        Object[] parameters = new Object[]{new DependanceService(), new DependanceNommeeService()};

        // When
        String result = (String) method.invoke(null, (Object) parameters);

        // Then
        String expected = "[" + StringHelper.abbreviateClassName(DependanceService.class)
                + ", " + StringHelper.abbreviateClassName(DependanceNommeeService.class) + "]";
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Doit comparer correctement deux signatures de méthode")
    void doit_comparer_correctement_deux_signatures_de_methode() throws Exception {

        // Given
        Class<?> methodSignatureClass = findMethodSignatureClass();
        Constructor<?> constructor = methodSignatureClass.getDeclaredConstructor(Method.class);
        constructor.setAccessible(true);

        Method method1 = ConfigurationFactoryFixture.class.getDeclaredMethod("creer_depuis_methode", DependanceService.class);
        Method method2 = ConfigurationFactoryFixture.class.getDeclaredMethod("creer_depuis_methode", DependanceService.class);
        Method method3 = ConfigurationFactoryFixture.class.getDeclaredMethod("creer_en_echec");

        Object signature1 = constructor.newInstance(method1);
        Object signature2 = constructor.newInstance(method2);
        Object signature3 = constructor.newInstance(method3);

        // When
        boolean equalSameSignature = signature1.equals(signature2);
        boolean equalDifferentSignature = signature1.equals(signature3);
        boolean equalDifferentType = signature1.equals("autre-type");

        // Then
        assertThat(equalSameSignature).isTrue();
        assertThat(signature1.hashCode()).isEqualTo(signature2.hashCode());
        assertThat(equalDifferentSignature).isFalse();
        assertThat(equalDifferentType).isFalse();
    }

    private static void restoreProperty(String propertyName, String value) {
        if (value == null)
            System.clearProperty(propertyName);
        else
            System.setProperty(propertyName, value);
    }

    private static boolean containsInChain(Throwable throwable, String expectedText) {
        Throwable current = throwable;

        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains(expectedText))
                return true;
            current = current.getCause();
        }

        return false;
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        System.setOut(printStream);
        try {
            action.run();
        } finally {
            printStream.flush();
            System.setOut(originalOut);
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    @SuppressWarnings("all")
    private static void invokeReflective(Method method, Object target, Object... args) {
        try {
            method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> findMethodSignatureClass() {
        for (Class<?> innerClass : BeanFactory.class.getDeclaredClasses()) {
            if ("MethodSignature".equals(innerClass.getSimpleName()))
                return innerClass;
        }

        throw new IllegalStateException("Classe interne MethodSignature introuvable");
    }

    @SuppressWarnings("all")
    static class BeanDepuisMethode {
        final DependanceService dependance;

        BeanDepuisMethode(DependanceService dependance) {
            this.dependance = dependance;
        }
    }

    static class ConfigurationFactoryFixture {

        @Bean
        public BeanDepuisMethode creer_depuis_methode(DependanceService dependance) {
            return new BeanDepuisMethode(dependance);
        }

        @Bean
        public Object creer_en_echec() {
            throw new IllegalStateException("Echec volontaire de méthode @Bean");
        }
    }

    @Service
    static class DependanceService {
    }

    @Service(name = "special-dependency")
    static class DependanceNommeeService {
    }

    @Service
    @SuppressWarnings("all")
    static class ServiceAvecNamed {

        final DependanceNommeeService dependanceNommee;

        ServiceAvecNamed(@Named("special-dependency") DependanceNommeeService dependanceNommee) {
            this.dependanceNommee = dependanceNommee;
        }
    }

    @Service
    static class ConstructeurMaxParamService {

        final String constructeurUtilise;
        final DependanceService dependance;

        @SuppressWarnings("all")
        ConstructeurMaxParamService() {
            this.constructeurUtilise = "MIN";
            this.dependance = null;
        }

        @SuppressWarnings("all")
        ConstructeurMaxParamService(DependanceService dependance) {
            this.constructeurUtilise = "MAX";
            this.dependance = dependance;
        }
    }

    @Service
    @SuppressWarnings("all")
    static class ServiceAvecEnvironment {

        final Environment environment;

        ServiceAvecEnvironment(Environment environment) {
            this.environment = environment;
        }
    }

    interface PostConstructInterface {

        @PostConstruct
        @SuppressWarnings("all")
        default void init_interface() {
            PostConstructFlags.interfaceCalled = true;
        }
    }

    static class PostConstructFlags {
        static boolean interfaceCalled;
    }

    static class ParentPostConstructService {

        static boolean parentCalled;

        @PostConstruct
        @SuppressWarnings("all")
        void init_parent() {
            parentCalled = true;
        }
    }

    @Service
    static class MultiPostConstructService extends ParentPostConstructService implements PostConstructInterface {

        static boolean childCalled;

        @PostConstruct
        @SuppressWarnings("all")
        void init_child() {
            childCalled = true;
        }
    }

    @Service
    static class ProfiledPostConstructService {

        static boolean profileCalled;

        @PostConstruct
        @Profile("dev")
        @SuppressWarnings("all")
        void init_profile() {
            profileCalled = true;
        }
    }

    @Service
    static class FailingPostConstructService {

        @PostConstruct
        @SuppressWarnings("all")
        void init_fail() {
            throw new IllegalStateException("Erreur post construct volontaire");
        }
    }

    @Service
    static class ConstructorEnErreurService {

        ConstructorEnErreurService() {
            throw new IllegalStateException("Erreur constructeur volontaire");
        }
    }

    @Service
    static class CyclicService {

        @SuppressWarnings("all")
        CyclicService(CyclicService self) {
        }
    }
}
