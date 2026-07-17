package com.jasonpercus.microbean.infrastructure.factory;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;
import com.jasonpercus.microbean.api.OS;
import com.jasonpercus.microbean.api.Primary;
import com.jasonpercus.microbean.api.Scope;
import com.jasonpercus.microbean.api.Service;

@DisplayName("Tests unitaires de la classe BeanDefinition")
class BeanDefinitionTest {

    @Test
    @DisplayName("Doit construire une définition depuis une méthode @Bean avec les métadonnées attendues")
    void doit_construire_une_definition_depuis_une_methode_bean_avec_les_metadonnees_attendues() throws Exception {

        // Given
        Context context = new Context();
        ConfigurationFixture configuration = new ConfigurationFixture();
        Method method = ConfigurationFixture.class.getDeclaredMethod("creer_bean_depuis_methode");

        // When
        BeanDefinition<?> definition = new BeanDefinition<>(configuration, method, context);

        // Then
        assertThat(definition.getType()).isEqualTo(BeanMethodeFixture.class);
        assertThat(definition.getName()).isEqualTo("bean-methode");
        assertThat(definition.isPrimary()).isTrue();
        assertThat(definition.getScope()).isEqualTo(Scope.PROTOTYPE);
        assertThat(definition.getOs()).containsExactly(OS.ALL);
    }

    @Test
    @DisplayName("Doit échouer si la méthode du constructeur n'est pas annotée @Bean")
    void doit_echouer_si_la_methode_du_constructeur_n_est_pas_annotee_bean() throws Exception {

        // Given
        Context context = new Context();
        ConfigurationFixture configuration = new ConfigurationFixture();
        Method method = ConfigurationFixture.class.getDeclaredMethod("methode_non_bean");

        // When & Then
        assertThatThrownBy(() -> new BeanDefinition<>(configuration, method, context))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not annotated")
                .hasMessageContaining("Bean");
    }

    @Test
    @DisplayName("Doit construire une définition de service avec scope, nom et primary")
    void doit_construire_une_definition_de_service_avec_scope_nom_et_primary() {

        // Given
        Context context = new Context();

        // When
        BeanDefinition<ServiceFixture> definition = new BeanDefinition<>(ServiceFixture.class, context);

        // Then
        assertThat(definition.getType()).isEqualTo(ServiceFixture.class);
        assertThat(definition.getName()).isEqualTo("service-fixture");
        assertThat(definition.isPrimary()).isTrue();
        assertThat(definition.getScope()).isEqualTo(Scope.PROTOTYPE);
        assertThat(definition.getOs()).containsExactly(OS.ALL);
    }

    @Test
    @DisplayName("Doit construire une définition d'adapter avec OS dédié")
    void doit_construire_une_definition_d_adapter_avec_os_dedie() {

        // Given
        Context context = new Context();

        // When
        BeanDefinition<AdapterFixture> definition = new BeanDefinition<>(AdapterFixture.class, context);

        // Then
        assertThat(definition.getType()).isEqualTo(AdapterFixture.class);
        assertThat(definition.getName()).isEqualTo("adapter-fixture");
        assertThat(definition.isPrimary()).isFalse();
        assertThat(definition.getScope()).isEqualTo(Scope.PROTOTYPE);
        assertThat(definition.getOs()).containsExactly(OS.MAC);
    }

    @Test
    @DisplayName("Doit construire une définition d'entry point avec valeurs par défaut")
    void doit_construire_une_definition_d_entrypoint_avec_valeurs_par_defaut() {

        // Given
        Context context = new Context();

        // When
        BeanDefinition<EntryPointFixture> definition = new BeanDefinition<>(EntryPointFixture.class, context);

        // Then
        assertThat(definition.getType()).isEqualTo(EntryPointFixture.class);
        assertThat(definition.getName()).isEmpty();
        assertThat(definition.isPrimary()).isFalse();
        assertThat(definition.getScope()).isEqualTo(Scope.SINGLETON);
        assertThat(definition.getOs()).containsExactly(OS.ALL);
    }

    @Test
    @DisplayName("Doit échouer si la classe du constructeur n'est pas un composant supporté")
    void doit_echouer_si_la_classe_du_constructeur_n_est_pas_un_composant_supporte() {

        // Given
        Context context = new Context();

        // When & Then
        assertThatThrownBy(() -> new BeanDefinition<>(ClasseSansAnnotation.class, context))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not annotated")
                .hasMessageContaining("component");
    }

    @Test
    @DisplayName("Doit créer un bean via la factory de méthode")
    void doit_creer_un_bean_via_la_factory_de_methode() throws Exception {

        // Given
        Context context = new Context();
        ConfigurationFixture configuration = new ConfigurationFixture();
        Method method = ConfigurationFixture.class.getDeclaredMethod("creer_bean_depuis_methode");
        BeanDefinition<?> definition = new BeanDefinition<>(configuration, method, context);

        // When
        Object bean1 = definition.createBean();
        Object bean2 = definition.createBean();

        // Then
        assertThat(bean1).isInstanceOf(BeanMethodeFixture.class);
        assertThat(bean2).isInstanceOf(BeanMethodeFixture.class);
        assertThat(ConfigurationFixture.COUNT_CREATE_BEAN_METHOD.get()).isEqualTo(2);
    }

    @Test
    @DisplayName("Doit créer un bean via la factory de classe")
    void doit_creer_un_bean_via_la_factory_de_classe() {

        // Given
        Context context = new Context();
        BeanDefinition<ServiceFixture> definition = new BeanDefinition<>(ServiceFixture.class, context);

        // When
        Object bean = definition.createBean();

        // Then
        assertThat(bean).isInstanceOf(ServiceFixture.class);
    }

    @Test
    @DisplayName("Doit construire une définition de service depuis une méta-annotation possédant ses propres attributs (getValue — méthode trouvée)")
    void doit_construire_une_definition_de_service_depuis_une_meta_annotation_avec_attributs_surcharges() {

        // Given
        Context context = new Context();

        // When
        BeanDefinition<MetaServiceAvecAttributsFixture> definition =
                new BeanDefinition<>(MetaServiceAvecAttributsFixture.class, context);

        // Then — les valeurs lues sont celles de l'instance de l'annotation, PAS les défauts de @Service
        assertThat(definition.getName()).isEqualTo("service-explicite");
        assertThat(definition.getScope()).isEqualTo(Scope.SINGLETON);
    }

    @Test
    @DisplayName("Doit retourner les valeurs par défaut de la méta-annotation @Service quand elle ne possède pas les attributs (getValue — NoSuchMethodException)")
    void doit_retourner_les_valeurs_par_defaut_de_la_meta_annotation_service_quand_elle_ne_possede_pas_les_attributs() {

        // Given
        Context context = new Context();

        // When
        BeanDefinition<MetaServiceSansAttributsFixture> definition =
                new BeanDefinition<>(MetaServiceSansAttributsFixture.class, context);

        // Then
        assertThat(definition.getName()).isEqualTo("valeur-par-defaut-service");
        assertThat(definition.getScope()).isEqualTo(Scope.PROTOTYPE);
    }

    @Test
    @DisplayName("Doit construire une définition d'adapter depuis une méta-annotation possédant ses propres attributs (getValue — méthode trouvée)")
    void doit_construire_une_definition_d_adapter_depuis_une_meta_annotation_avec_attributs_surcharges() {

        // Given
        Context context = new Context();

        // When
        BeanDefinition<MetaAdapterAvecAttributsFixture> definition =
                new BeanDefinition<>(MetaAdapterAvecAttributsFixture.class, context);

        // Then — les valeurs lues sont celles de l'instance de l'annotation, PAS les défauts de @Adapter
        assertThat(definition.getName()).isEqualTo("adapter-explicite");
        assertThat(definition.getScope()).isEqualTo(Scope.SINGLETON);
        assertThat(definition.getOs()).containsExactly(OS.WINDOWS);
    }

    @Test
    @DisplayName("Doit retourner les valeurs par défaut de la méta-annotation @Adapter quand elle ne possède pas les attributs (getValue — NoSuchMethodException)")
    void doit_retourner_les_valeurs_par_defaut_de_la_meta_annotation_adapter_quand_elle_ne_possede_pas_les_attributs() {

        // Given
        Context context = new Context();

        // When
        BeanDefinition<MetaAdapterSansAttributsFixture> definition =
                new BeanDefinition<>(MetaAdapterSansAttributsFixture.class, context);

        // Then
        assertThat(definition.getName()).isEqualTo("valeur-par-defaut-adapter");
        assertThat(definition.getScope()).isEqualTo(Scope.PROTOTYPE);
        assertThat(definition.getOs()).containsExactly(OS.LINUX);
    }

    @Test
    @DisplayName("Doit lever une RuntimeException quand l'invocation réflexive d'une méthode d'annotation échoue (getValue — InvocationTargetException)")
    void doit_lever_runtime_exception_quand_l_invocation_de_la_methode_d_annotation_echoue() throws Exception {

        // Given
        Service mockAnnotation = mock(Service.class);
        org.mockito.Mockito.doReturn(Service.class).when(mockAnnotation).annotationType();
        when(mockAnnotation.name()).thenThrow(new RuntimeException("invocation échouée"));

        Method getValueMethod = BeanDefinition.class.getDeclaredMethod(
                "getValue", Annotation.class, String.class, Class.class, Object.class);
        getValueMethod.setAccessible(true);

        // When & Then
        assertThatThrownBy(() -> {
            try {
                getValueMethod.invoke(null, mockAnnotation, "name", String.class, "fallback");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Doit lever IllegalArgumentException quand l'annotation passée à extractService n'est pas méta-annotée @Service (defaults == null)")
    void doit_lever_illegal_argument_exception_quand_annotation_passee_a_extract_service_n_est_pas_meta_annotee_service() throws Exception {

        // Given — @Deprecated n'est pas méta-annotée avec @Service, donc defaults sera null
        Annotation annotation = AnnotationNonServiceFixture.class.getAnnotation(Deprecated.class);

        Method extractService = BeanDefinition.class.getDeclaredMethod("extractService", Annotation.class);
        extractService.setAccessible(true);

        // When & Then
        assertThatThrownBy(() -> {
            try {
                extractService.invoke(null, annotation);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("@Service");
    }

    @Test
    @DisplayName("Doit retourner Service.class depuis annotationType() de l'objet Service construit par extractService")
    void doit_retourner_service_class_depuis_annotation_type_du_service_extrait() throws Exception {

        // Given — récupère l'instance de la méta-annotation @CustomServiceSansAttributs posée sur la fixture
        Annotation annotation = MetaServiceSansAttributsFixture.class.getDeclaredAnnotations()[0];

        Method extractService = BeanDefinition.class.getDeclaredMethod("extractService", Annotation.class);
        extractService.setAccessible(true);

        // When
        Service result = (Service) extractService.invoke(null, annotation);

        // Then
        assertThat(result.annotationType()).isEqualTo(Service.class);
    }

    @Test
    @DisplayName("Doit lever IllegalArgumentException quand l'annotation passée à extractAdapter n'est pas méta-annotée @Adapter (defaults == null)")
    void doit_lever_illegal_argument_exception_quand_annotation_passee_a_extract_adapter_n_est_pas_meta_annotee_adapter() throws Exception {

        // Given — @Deprecated n'est pas méta-annotée avec @Adapter, donc defaults sera null
        Annotation annotation = AnnotationNonServiceFixture.class.getAnnotation(Deprecated.class);

        Method extractAdapter = BeanDefinition.class.getDeclaredMethod("extractAdapter", Annotation.class);
        extractAdapter.setAccessible(true);

        // When & Then
        assertThatThrownBy(() -> {
            try {
                extractAdapter.invoke(null, annotation);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("@Adapter");
    }

    @Test
    @DisplayName("Doit retourner Adapter.class depuis annotationType() de l'objet Adapter construit par extractAdapter")
    void doit_retourner_adapter_class_depuis_annotation_type_de_l_adapter_extrait() throws Exception {

        // Given — récupère l'instance de la méta-annotation @CustomAdapterSansAttributs posée sur la fixture
        Annotation annotation = MetaAdapterSansAttributsFixture.class.getDeclaredAnnotations()[0];

        Method extractAdapter = BeanDefinition.class.getDeclaredMethod("extractAdapter", Annotation.class);
        extractAdapter.setAccessible(true);

        // When
        Adapter result = (Adapter) extractAdapter.invoke(null, annotation);

        // Then
        assertThat(result.annotationType()).isEqualTo(Adapter.class);
    }

    @Test
    @DisplayName("Doit ignorer les annotations non méta-@Service et trouver la bonne méta-annotation dans la boucle (isAnnotationPresent @Service == false)")
    void doit_ignorer_annotation_neutre_et_trouver_la_meta_annotation_service_dans_la_boucle() {

        // Given — @MarqueurNeutre est posé AVANT @CustomServiceSansAttributs :
        // le premier tour de boucle retourne false, le second retourne true → extractService est appelé
        Context context = new Context();

        // When
        BeanDefinition<MetaServiceAvecMarqueurNeutreFixture> definition =
                new BeanDefinition<>(MetaServiceAvecMarqueurNeutreFixture.class, context);

        // Then — les valeurs viennent bien de la méta-annotation @Service trouvée en second
        assertThat(definition.getName()).isEqualTo("valeur-par-defaut-service");
        assertThat(definition.getScope()).isEqualTo(Scope.PROTOTYPE);
    }

    @Test
    @DisplayName("Doit ignorer les annotations non méta-@Adapter et trouver la bonne méta-annotation dans la boucle (isAnnotationPresent @Adapter == false)")
    void doit_ignorer_annotation_neutre_et_trouver_la_meta_annotation_adapter_dans_la_boucle() {

        // Given — @MarqueurNeutre est posé AVANT @CustomAdapterSansAttributs :
        // le premier tour de boucle retourne false, le second retourne true → extractAdapter est appelé
        Context context = new Context();

        // When
        BeanDefinition<MetaAdapterAvecMarqueurNeutreFixture> definition =
                new BeanDefinition<>(MetaAdapterAvecMarqueurNeutreFixture.class, context);

        // Then — les valeurs viennent bien de la méta-annotation @Adapter trouvée en second
        assertThat(definition.getName()).isEqualTo("valeur-par-defaut-adapter");
        assertThat(definition.getScope()).isEqualTo(Scope.PROTOTYPE);
        assertThat(definition.getOs()).containsExactly(OS.LINUX);
    }

    // -------------------------------------------------------------------------
    // Fixtures méta-annotations
    // -------------------------------------------------------------------------

    /* Méta-annotation @Service avec ses propres attributs name() et scope(). */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Service
    @interface CustomServiceAvecAttributs {
        String name() default "service-surcharge";
        Scope scope() default Scope.PROTOTYPE;
    }

    /* Méta-annotation @Service SANS attributs propres (déclenche NoSuchMethodException dans getValue). */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Service(name = "valeur-par-defaut-service", scope = Scope.PROTOTYPE)
    @interface CustomServiceSansAttributs {}

    /* Méta-annotation @Adapter avec ses propres attributs name(), scope() et os(). */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Adapter
    @interface CustomAdapterAvecAttributs {
        String name() default "adapter-surcharge";
        Scope scope() default Scope.PROTOTYPE;
        OS[] os() default OS.MAC;
    }

    /** Méta-annotation @Adapter SANS attributs propres (déclenche NoSuchMethodException dans getValue). */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Adapter(name = "valeur-par-defaut-adapter", scope = Scope.PROTOTYPE, os = OS.LINUX)
    @interface CustomAdapterSansAttributs {}

    /**
     * Annotation neutre : ni méta-@Service, ni méta-@Adapter.
     * Placée en tête sur une classe pour forcer le {@code false} du {@code if}
     * dans la boucle {@code extractOtherValues} avant de trouver la bonne méta-annotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface MarqueurNeutre {}

    @CustomServiceAvecAttributs(name = "service-explicite", scope = Scope.SINGLETON)
    static class MetaServiceAvecAttributsFixture {}

    @CustomServiceSansAttributs
    static class MetaServiceSansAttributsFixture {}

    /* @MarqueurNeutre en premier → isAnnotationPresent(@Service) == false → continue ; @CustomServiceSansAttributs ensuite → true → extractService. */
    @MarqueurNeutre
    @CustomServiceSansAttributs
    static class MetaServiceAvecMarqueurNeutreFixture {}

    @CustomAdapterAvecAttributs(name = "adapter-explicite", scope = Scope.SINGLETON, os = OS.WINDOWS)
    static class MetaAdapterAvecAttributsFixture {}

    @CustomAdapterSansAttributs
    static class MetaAdapterSansAttributsFixture {}

    /* @MarqueurNeutre en premier → isAnnotationPresent(@Adapter) == false → continue ; @CustomAdapterSansAttributs ensuite → true → extractAdapter. */
    @MarqueurNeutre
    @CustomAdapterSansAttributs
    static class MetaAdapterAvecMarqueurNeutreFixture {}

    // -------------------------------------------------------------------------
    // Fixtures existantes
    // -------------------------------------------------------------------------

    /* Classe annotée avec @Deprecated uniquement — jamais méta-annotée @Service ni @Adapter. */
    @Deprecated
    @SuppressWarnings("all")
    static class AnnotationNonServiceFixture {}

    static class BeanMethodeFixture {
    }

    static class ClasseSansAnnotation {
    }

    static class ConfigurationFixture {

        static final AtomicInteger COUNT_CREATE_BEAN_METHOD = new AtomicInteger();

        @Bean(name = "bean-methode", scope = Scope.PROTOTYPE)
        @Primary
        public BeanMethodeFixture creer_bean_depuis_methode() {
            COUNT_CREATE_BEAN_METHOD.incrementAndGet();
            return new BeanMethodeFixture();
        }

        public BeanMethodeFixture methode_non_bean() {
            return new BeanMethodeFixture();
        }
    }

    @Primary
    @Service(name = "service-fixture", scope = Scope.PROTOTYPE)
    static class ServiceFixture {
    }

    @Adapter(name = "adapter-fixture", scope = Scope.PROTOTYPE, os = OS.MAC)
    static class AdapterFixture {
    }

    @EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
    static class EntryPointFixture {
    }
}
