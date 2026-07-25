package com.jasonpercus.microbean.infrastructure.run;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.PACKAGE_ADAPTERS;
import static com.jasonpercus.microbean.infrastructure.Constants.PACKAGE_COMPONENTS;
import static com.jasonpercus.microbean.infrastructure.Constants.PACKAGE_ENTRYPOINTS;
import static com.jasonpercus.microbean.infrastructure.Constants.PACKAGE_SERVICES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jasonpercus.microbean.api.Environment;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.infrastructure.run.initializer.AppWithConfigurationPropertiesInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.AppWithCustomConfigurationPropertiesInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.AppWithExplicitScanPackagesInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.AppWithInvalidConfigurationExtensionInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.AppWithInvalidJsonConfigurationInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.AppWithMissingConfigurationInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.InvalidAppAnnotatedAsEntryPointInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.NotAnnotatedAppInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.NotAnnotatedEntryPointInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.ValidAppInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.ValidEntryPointInitializer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires de la classe Initializer")
class InitializerTest {

    @Test
    @DisplayName("Doit initialiser le context et les classes scannées quand les paramètres sont valides")
    void doit_initialiser_le_context_et_les_classes_scanees_quand_les_parametres_sont_valides() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = singleEntryPoint(ValidEntryPointInitializer.class);

        // When
        Initializer initializer = Initializer.init(ValidAppInitializer.class, new String[0], entryPoints);

        // Then
        assertThat(initializer.getContext()).isNotNull();
        assertThat(initializer.getClasses()).isNotNull();
        assertThat(initializer.getClasses()).doesNotContainNull();
    }

    @Test
    @DisplayName("Doit échouer si la classe application n'est pas annotée avec @MicroBeanApplication")
    void doit_echouer_si_la_classe_application_n_est_pas_annotee_microbeanapplication() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = singleEntryPoint(ValidEntryPointInitializer.class);

        // When & Then
        assertThatThrownBy(() -> Initializer.init(NotAnnotatedAppInitializer.class, new String[0], entryPoints))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Missing @MicroBeanApplication")
                .hasMessageContaining("NotAnnotatedApp");
    }

    @Test
    @DisplayName("Doit échouer si le tableau des entry points est null")
    @SuppressWarnings("all")
    void doit_echouer_si_le_tableau_des_entrypoints_est_null() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = null;

        // When & Then
        assertThatThrownBy(() -> Initializer.init(ValidAppInitializer.class, new String[0], entryPoints))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("At least one ApplicationEntryPoint class must be provided");
    }

    @Test
    @DisplayName("Doit échouer si le tableau des entry points est vide")
    void doit_echouer_si_le_tableau_des_entrypoints_est_vide() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = emptyEntryPoints();

        // When & Then
        assertThatThrownBy(() -> Initializer.init(ValidAppInitializer.class, new String[0], entryPoints))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("At least one ApplicationEntryPoint class must be provided");
    }

    @Test
    @DisplayName("Doit échouer si la classe application est annotée avec @EntryPointService")
    void doit_echouer_si_la_classe_application_est_annotee_entrypointservice() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = singleEntryPoint(ValidEntryPointInitializer.class);

        // When & Then
        assertThatThrownBy(() -> Initializer.init(InvalidAppAnnotatedAsEntryPointInitializer.class, new String[0], entryPoints))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("class should not be annotated with @EntryPointService")
                .hasMessageContaining("InvalidAppAnnotatedAsEntryPoint");
    }

    @Test
    @DisplayName("Doit échouer si un entry point n'est pas annote avec @EntryPointService")
    void doit_echouer_si_un_entrypoint_n_est_pas_annote_entrypointservice() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = singleEntryPoint(NotAnnotatedEntryPointInitializer.class);
        
        // When & Then
        assertThatThrownBy(() -> Initializer.init(ValidAppInitializer.class, new String[0], entryPoints))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Missing @EntryPointService")
                .hasMessageContaining("NotAnnotatedEntryPoint");
    }

    @Test
    @DisplayName("Doit retourner les scan packages declares quand ils sont définis")
    void doit_retourner_les_scanpackages_declares_quand_ils_sont_definis() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = singleEntryPoint(ValidEntryPointInitializer.class);

        // When
        Initializer initializer = Initializer.init(AppWithExplicitScanPackagesInitializer.class, new String[0], entryPoints);

        // Then
        assertThat(initializer.getPackagesPathsToScan()).containsExactly("test.pkg.one", "test.pkg.two", PACKAGE_ENTRYPOINTS, PACKAGE_SERVICES, PACKAGE_ADAPTERS, PACKAGE_COMPONENTS);
    }

    @Test
    @DisplayName("Doit utiliser le package de l'application si aucun scan package n'est défini")
    void doit_utiliser_le_package_de_l_application_si_aucun_scanpackage_n_est_defini() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = singleEntryPoint(ValidEntryPointInitializer.class);
        String[] expectedScanPackages = new String[] {
                "com.jasonpercus.microbean.infrastructure.run.initializer",
                PACKAGE_ENTRYPOINTS,
                PACKAGE_SERVICES,
                PACKAGE_ADAPTERS,
                PACKAGE_COMPONENTS
        };

        // When
        Initializer initializer = Initializer.init(ValidAppInitializer.class, new String[0], entryPoints);

        // Then
        assertThat(initializer.getPackagesPathsToScan()).containsExactly(expectedScanPackages);
    }

    @Test
    @DisplayName("Doit retourner true si isNotEmptyEntryPoints reçoit null")
    void doit_retourner_true_si_isnotemptyentrypoints_recoit_null() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = nullEntryPoints();

        // When
        boolean result = Initializer.isNotEmptyEntryPoints(entryPoints);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit retourner true si isNotEmptyEntryPoints reçoit un tableau vide")
    void doit_retourner_true_si_isnotemptyentrypoints_recoit_un_tableau_vide() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = emptyEntryPoints();

        // When
        boolean result = Initializer.isNotEmptyEntryPoints(entryPoints);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit retourner false si isNotEmptyEntryPoints reçoit au moins un element")
    void doit_retourner_false_si_isnotemptyentrypoints_recoit_au_moins_un_element() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = singleEntryPoint(ValidEntryPointInitializer.class);

        // When
        boolean result = Initializer.isNotEmptyEntryPoints(entryPoints);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Doit charger et aplatir les propriétés YAML et JSON")
    void doit_charger_et_aplatir_les_proprietes_yaml_et_json() {

        // Given
        Initializer initializer = newInitializer(AppWithConfigurationPropertiesInitializer.class);
        Environment environment = new Environment(new String[0]);

        // When
        initializer.manageConfigurationProperties(environment);

        // Then
        assertThat(environment.getFlatProperties()).containsKeys(
                "server.host-name",
                "server.port",
                "feature.metrics-enabled",
                "database.max-pool-size",
                "database.enabled"
        );
        assertThat(environment.getProperty("server.host-name")).isEqualTo("localhost");
        assertThat(environment.getProperty("server.port")).isEqualTo(8080);
        assertThat(environment.getProperty("database.max-pool-size")).isEqualTo(10);
        assertThat(environment.getProperty("database.enabled")).isEqualTo(true);
    }

    @Test
    @DisplayName("Doit charger les propriétés implicites application.* quand l'annotation n'en déclare aucune")
    void doit_charger_les_proprietes_implicites_quand_l_annotation_n_en_declare_aucune() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);
        Environment environment = new Environment(new String[0]);

        // When
        initializer.manageConfigurationProperties(environment);

        // Then
        assertThat(environment.getFlatProperties()).containsKeys(
                "application.name",
                "application.port",
                "application.description",
                "shared.value"
        );
        assertThat(environment.getProperty("application.name")).isEqualTo("microbean");
        assertThat(environment.getProperty("application.port")).isEqualTo(9090);
        assertThat(environment.getProperty("shared.value")).isEqualTo("json-value");
    }

    @Test
    @DisplayName("Doit charger automatiquement application.yaml et le profil actif")
    void doit_charger_automatiquement_application_yaml_et_le_profil_actif() {

        // Given
        String originalProfile = System.getProperty("app.profile");
        System.setProperty("app.profile", "local");
        Initializer initializer = newInitializer(ValidAppInitializer.class);
        Environment environment = new Environment(new String[0]);

        try {
            // When
            initializer.manageConfigurationProperties(environment);

            // Then
            assertThat(environment.getProperty("application.name")).isEqualTo("microbean");
            assertThat(environment.getProperty("application.port")).isEqualTo(9090);
            assertThat(environment.getProperty("application.debug")).isEqualTo(true);
            assertThat(environment.getProperty("application.description")).isEqualTo("local override");
        } finally {
            restoreProfile(originalProfile);
        }
    }

    @Test
    @DisplayName("Doit laisser la dernière valeur l'emporter quand plusieurs fichiers déclarent la même clé")
    void doit_laisser_la_derniere_valeur_l_emporter_quand_plusieurs_fichiers_declarent_la_meme_cle() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);
        Environment environment = new Environment(new String[0]);

        // When
        initializer.manageConfigurationProperties(environment);

        // Then
        assertThat(environment.getProperty("shared.value")).isEqualTo("json-value");
    }

    @Test
    @DisplayName("Doit charger un overlay de profil même si le profil est écrit en majuscules")
    void doit_charger_un_overlay_de_profil_meme_si_le_profil_est_ecrit_en_majuscules() {

        // Given
        String originalProfile = System.getProperty("app.profile");
        System.setProperty("app.profile", "LOCAL");
        Initializer initializer = newInitializer(ValidAppInitializer.class);
        Environment environment = new Environment(new String[0]);

        try {
            // When
            initializer.manageConfigurationProperties(environment);

            // Then
            assertThat(environment.getProperty("application.description")).isEqualTo("local override");
        } finally {
            restoreProfile(originalProfile);
        }
    }

    @Test
    @DisplayName("Doit surcharger les fichiers explicites non application.* avec le profil actif")
    void doit_surcharger_les_fichiers_explicites_non_application_avec_le_profil_actif() {

        // Given
        String originalProfile = System.getProperty("app.profile");
        System.setProperty("app.profile", "local");
        Initializer initializer = newInitializer(AppWithCustomConfigurationPropertiesInitializer.class);
        Environment environment = new Environment(new String[0]);

        try {
            // When
            initializer.manageConfigurationProperties(environment);

            // Then
            assertThat(environment.getProperty("custom-json.origin")).isEqualTo("base");
            assertThat(environment.getProperty("custom-json.value")).isEqualTo("json-local");
            assertThat(environment.getProperty("custom-json.enabled")).isEqualTo(true);

            assertThat(environment.getProperty("custom-yaml.origin")).isEqualTo("base");
            assertThat(environment.getProperty("custom-yaml.value")).isEqualTo("yaml-local");
            assertThat(environment.getProperty("custom-yaml.enabled")).isEqualTo(true);

            assertThat(environment.getProperty("custom-yml.origin")).isEqualTo("base");
            assertThat(environment.getProperty("custom-yml.value")).isEqualTo("yml-local");
            assertThat(environment.getProperty("custom-yml.enabled")).isEqualTo(true);
        } finally {
            restoreProfile(originalProfile);
        }
    }

    @Test
    @DisplayName("Doit conserver les valeurs de base des fichiers explicites quand aucun profil n'est actif")
    void doit_conserver_les_valeurs_de_base_des_fichiers_explicites_quand_aucun_profil_n_est_actif() {

        // Given
        String originalProfile = System.getProperty("app.profile");
        System.clearProperty("app.profile");
        Initializer initializer = newInitializer(AppWithCustomConfigurationPropertiesInitializer.class);
        Environment environment = new Environment(new String[0]);

        try {
            // When
            initializer.manageConfigurationProperties(environment);

            // Then
            assertThat(environment.getProperty("custom-json.value")).isEqualTo("json-base");
            assertThat(environment.getProperty("custom-json.enabled")).isNull();
            assertThat(environment.getProperty("custom-yaml.value")).isEqualTo("yaml-base");
            assertThat(environment.getProperty("custom-yml.value")).isEqualTo("yml-base");
        } finally {
            restoreProfile(originalProfile);
        }
    }

    @Test
    @DisplayName("Doit résoudre les surcharges explicites quand le profil est en majuscules")
    void doit_resoudre_les_surcharges_explicites_quand_le_profil_est_en_majuscules() {

        // Given
        String originalProfile = System.getProperty("app.profile");
        System.setProperty("app.profile", "LOCAL");
        Initializer initializer = newInitializer(AppWithCustomConfigurationPropertiesInitializer.class);
        Environment environment = new Environment(new String[0]);

        try {
            // When
            initializer.manageConfigurationProperties(environment);

            // Then
            assertThat(environment.getProperty("custom-json.value")).isEqualTo("json-local");
            assertThat(environment.getProperty("custom-yaml.value")).isEqualTo("yaml-local");
            assertThat(environment.getProperty("custom-yml.value")).isEqualTo("yml-local");
        } finally {
            restoreProfile(originalProfile);
        }
    }

    @Test
    @DisplayName("Doit echouer au chargement des propriétés quand l'extension du fichier est invalide")
    void doit_echouer_au_chargement_des_proprietes_quand_l_extension_du_fichier_est_invalide() {

        // Given
        Initializer initializer = newInitializer(AppWithInvalidConfigurationExtensionInitializer.class);

        // When & Then
        assertThatThrownBy(() -> initializer.manageConfigurationProperties(new Environment(new String[0])))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid configuration properties file")
                .hasMessageContaining("initializer/application-config.txt");
    }

    @Test
    @DisplayName("Doit echouer au chargement des propriétés quand le fichier est absent")
    void doit_echouer_au_chargement_des_proprietes_quand_le_fichier_est_absent() {

        // Given
        Initializer initializer = newInitializer(AppWithMissingConfigurationInitializer.class);

        // When & Then
        assertThatThrownBy(() -> initializer.manageConfigurationProperties(new Environment(new String[0])))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Configuration properties file not found")
                .hasMessageContaining("initializer/missing-config.yaml");
    }

    @Test
    @DisplayName("Doit echouer au chargement des propriétés quand le JSON est invalide")
    void doit_echouer_au_chargement_des_proprietes_quand_le_json_est_invalide() {

        // Given
        Initializer initializer = newInitializer(AppWithInvalidJsonConfigurationInitializer.class);

        // When & Then
        assertThatThrownBy(() -> initializer.manageConfigurationProperties(new Environment(new String[0])))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to load configuration properties")
                .hasMessageContaining("initializer/application-invalid.json")
                .hasCauseInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Doit créer un ObjectMapper JSON avec stratégie KEBAB_CASE")
    void doit_creer_un_objectmapper_json_avec_strategie_kebab_case() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);

        // When
        ObjectMapper objectMapper = initializer.createObjectMapper("initializer/application-config.json");

        // Then
        assertThat(objectMapper.getFactory()).isNotInstanceOf(YAMLFactory.class);
        assertThat(objectMapper.getPropertyNamingStrategy()).isEqualTo(PropertyNamingStrategies.KEBAB_CASE);
    }

    @Test
    @DisplayName("Doit créer un ObjectMapper YAML avec stratégie KEBAB_CASE")
    void doit_creer_un_objectmapper_yaml_avec_strategie_kebab_case() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);

        // When
        ObjectMapper objectMapper = initializer.createObjectMapper("initializer/application-config.yaml");

        // Then
        assertThat(objectMapper.getFactory()).isInstanceOf(YAMLFactory.class);
        assertThat(objectMapper.getPropertyNamingStrategy()).isEqualTo(PropertyNamingStrategies.KEBAB_CASE);
    }

    @Test
    @DisplayName("Doit valider et retourner l'URL d'un fichier de configuration existant")
    void doit_valider_et_retourner_l_url_d_un_fichier_de_configuration_existant() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);

        // When
        URL url = initializer.checkConfigurationProperties("initializer/application-config.yaml");

        // Then
        assertThat(url).isNotNull();
    }

    @Test
    @DisplayName("Doit echouer si le chemin de configuration est null ou vide")
    void doit_echouer_si_le_chemin_de_configuration_est_null_ou_vide() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);

        // When & Then
        assertThatThrownBy(() -> initializer.checkConfigurationProperties(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid path for properties file");

        assertThatThrownBy(() -> initializer.checkConfigurationProperties(""))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid path for properties file");
    }

    @Test
    @DisplayName("Doit echouer si le fichier de configuration est introuvable")
    void doit_echouer_si_le_fichier_de_configuration_est_introuvable() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);

        // When & Then
        assertThatThrownBy(() -> initializer.checkConfigurationProperties("initializer/missing-config.yaml"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Configuration properties file not found")
                .hasMessageContaining("initializer/missing-config.yaml");
    }

    @Test
    @DisplayName("Doit echouer si le fichier existe mais n'est ni YAML ni JSON")
    void doit_echouer_si_le_fichier_existe_mais_n_est_ni_yaml_ni_json() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);

        // When & Then
        assertThatThrownBy(() -> initializer.checkConfigurationProperties("initializer/application-config.txt"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid configuration properties file")
                .hasMessageContaining("initializer/application-config.txt");
    }

    @Test
    @DisplayName("Doit aplatir récursivement les propriétés imbriquées")
    void doit_aplatir_recursivement_les_proprietes_imbriquees() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);
        Environment environment = new Environment(new String[0]);
        Map<String, Object> nested = Map.of(
                "host", "localhost",
                "pool", Map.of("size", 32)
        );

        // When
        initializer.setupConfigurationProperties(environment, "database", nested);

        // Then
        assertThat(environment.getProperty("database.host")).isEqualTo("localhost");
        assertThat(environment.getProperty("database.pool.size")).isEqualTo(32);
    }

    @Test
    @DisplayName("Doit placer la valeur directement quand setupConfigurationProperties reçoit une valeur simple")
    void doit_placer_la_valeur_directement_quand_setupconfigurationproperties_recoit_une_valeur_simple() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);
        Environment environment = new Environment(new String[0]);

        // When
        initializer.setupConfigurationProperties(environment, "database.enabled", true);

        // Then
        assertThat(environment.getProperty("database.enabled")).isEqualTo(true);
    }

    @Test
    @DisplayName("Doit désérialiser un YAML en map")
    void doit_deserialiser_un_yaml_en_map() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);
        URL url = initializer.checkConfigurationProperties("initializer/application-config.yaml");
        ObjectMapper objectMapper = initializer.createObjectMapper("initializer/application-config.yaml");

        // When
        Map<String, Object> map = Initializer.deserializeToMap(url, objectMapper, "initializer/application-config.yaml");

        // Then
        assertThat(map).containsKeys("server", "feature");
    }

    @Test
    @DisplayName("Doit echouer lors de la désérialisation d'un JSON invalide")
    void doit_echouer_lors_de_la_deserialisation_d_un_json_invalide() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);
        URL url = initializer.checkConfigurationProperties("initializer/application-invalid.json");
        ObjectMapper objectMapper = initializer.createObjectMapper("initializer/application-invalid.json");

        // When & Then
        assertThatThrownBy(() -> Initializer.deserializeToMap(url, objectMapper, "initializer/application-invalid.json"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to load configuration properties")
                .hasMessageContaining("initializer/application-invalid.json")
                .hasCauseInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Doit reconnaître correctement les extensions YAML et JSON")
    void doit_reconnaitre_correctement_les_extensions_yaml_et_json() {

        // Given
        Initializer initializer = newInitializer(ValidAppInitializer.class);

        // When & Then
        assertThat(invokePrivateExtensionCheck(initializer, "isYaml", "config.yaml")).isTrue();
        assertThat(invokePrivateExtensionCheck(initializer, "isYaml", "config.YML")).isTrue();
        assertThat(invokePrivateExtensionCheck(initializer, "isYaml", "config.json")).isFalse();

        assertThat(invokePrivateExtensionCheck(initializer, "isJson", "config.json")).isTrue();
        assertThat(invokePrivateExtensionCheck(initializer, "isJson", "config.JSON")).isTrue();
        assertThat(invokePrivateExtensionCheck(initializer, "isJson", "config.yaml")).isFalse();
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends ApplicationEntryPoint>[] singleEntryPoint(Class<? extends ApplicationEntryPoint> type) {
        return (Class<? extends ApplicationEntryPoint>[]) new Class<?>[]{type};
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends ApplicationEntryPoint>[] emptyEntryPoints() {
        return (Class<? extends ApplicationEntryPoint>[]) new Class<?>[0];
    }

    private static Class<? extends ApplicationEntryPoint>[] nullEntryPoints() {
        return null;
    }

    private static Initializer newInitializer(Class<?> appClass) {
        try {
            Constructor<Initializer> constructor = Initializer.class.getDeclaredConstructor(Class.class, String[].class, Class[].class);
            constructor.setAccessible(true);
            return constructor.newInstance(appClass, new String[0], singleEntryPoint(ValidEntryPointInitializer.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void restoreProfile(String value) {
        if (value == null)
            System.clearProperty("app.profile");
        else
            System.setProperty("app.profile", value);
    }

    private static boolean invokePrivateExtensionCheck(Initializer initializer, String methodName, String path) {
        try {
            Method method = Initializer.class.getDeclaredMethod(methodName, String.class);
            method.setAccessible(true);
            return (boolean) method.invoke(initializer, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
