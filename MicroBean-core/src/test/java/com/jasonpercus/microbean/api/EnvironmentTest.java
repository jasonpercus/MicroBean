package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires pour la classe Environment")
class EnvironmentTest {

    @Test
    @DisplayName("Doit encapsuler les arguments correctement")
    void doit_encapsuler_les_arguments_correctement() {

        // Given
        String[] input = {"a", "b", "c"};
        Environment env = new Environment(input);

        // When
        Arguments args = env.getArguments();

        // Then
        assertNotNull(args);
        assertThat(args.size()).isEqualTo(3);
        assertThat(args.getArgs()).containsExactly(input);
    }

    @Test
    @DisplayName("Doit traiter les arguments null comme une liste vide")
    void doit_traiter_null_arguments() {

        // Given
        Environment env = new Environment(null);

        // When
        Arguments args = env.getArguments();

        // Then
        assertNotNull(args);
        assertThat(args.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Doit retourner la même instance d'Arguments à chaque appel")
    void doit_retourner_la_meme_instance_d_arguments() {

        // Given
        String[] input = {"x", "y"};
        Environment env = new Environment(input);

        // When
        Arguments a1 = env.getArguments();
        Arguments a2 = env.getArguments();

        // Then
        assertSame(a1, a2);
    }

    @Test
    @DisplayName("Doit retourner le profil actif de MicroBean")
    void doit_retourner_le_profil_de_MicroBean() {

        // Given (simule un profil actif via une propriété système)
        System.setProperty("app.profile", "dev");

        // When
        Environment env = new Environment(new String[]{"--test"});

        // Then
        assertThat(env.getProfile()).isEqualTo("dev");
    }

    @Test
    @DisplayName("Doit mapper les propriétés kebab-case vers un POJO")
    void doit_mapper_les_proprietes_kebab_case_vers_un_pojo() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperties(Map.of(
                "server-port", 8080,
                "service-name", "microbean"
        ));

        // When
        AppProperties properties = env.getProperties(AppProperties.class);

        // Then
        assertThat(properties).isNotNull();
        assertThat(properties.serverPort).isEqualTo(8080);
        assertThat(properties.serviceName).isEqualTo("microbean");
    }

    @Test
    @DisplayName("Doit mapper les propriétés imbriquées vers un POJO")
    void doit_mapper_les_proprietes_imbriquees_vers_un_pojo() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperties(Map.of(
                "database", Map.of(
                        "host-name", "localhost",
                        "max-pool-size", 16
                )
        ));

        // When
        NestedProperties properties = env.getProperties(NestedProperties.class);

        // Then
        assertThat(properties).isNotNull();
        assertThat(properties.database).isNotNull();
        assertThat(properties.database.hostName).isEqualTo("localhost");
        assertThat(properties.database.maxPoolSize).isEqualTo(16);
    }

    @Test
    @DisplayName("Doit retourner un objet avec valeurs par défaut si aucune propriété n'est définie")
    void doit_retourner_un_objet_avec_valeurs_par_defaut_si_aucune_propriete_n_est_definie() {

        // Given
        Environment env = new Environment(new String[0]);

        // When
        AppProperties properties = env.getProperties(AppProperties.class);

        // Then
        assertThat(properties).isNotNull();
        assertThat(properties.serverPort).isEqualTo(0);
        assertThat(properties.serviceName).isNull();
    }

    @Test
    @DisplayName("Doit échouer si le type cible est null")
    void doit_echouer_si_le_type_cible_est_null() {

        // Given
        Environment env = new Environment(new String[0]);

        // When & Then
        assertThatThrownBy(() -> env.getProperties(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Doit ignorer les clés inconnues lors du mapping")
    void doit_ignorer_les_cles_inconnues_lors_du_mapping() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperties(Map.of("unknown-key", "value"));

        // When
        AppProperties properties = env.getProperties(AppProperties.class);

        // Then
        assertThat(properties).isNotNull();
        assertThat(properties.serverPort).isEqualTo(0);
        assertThat(properties.serviceName).isNull();
    }

    @Test
    @DisplayName("Doit fusionner récursivement les propriétés imbriquées")
    void doit_fusionner_recursivement_les_proprietes_imbriquees() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperties(Map.of(
                "application", Map.of(
                        "name", "microbean",
                        "port", 8080
                )
        ));

        // When
        env.putProperties(Map.of(
                "application", Map.of(
                        "port", 9090,
                        "debug", true
                )
        ));

        // Then
        NestedApplicationProperties properties = env.getProperties(NestedApplicationProperties.class);
        assertThat(properties).isNotNull();
        assertThat(properties.application).isNotNull();
        assertThat(properties.application.name).isEqualTo("microbean");
        assertThat(properties.application.port).isEqualTo(9090);
        assertThat(properties.application.debug).isTrue();
    }

    // -------------------------------------------------------------------------
    // Tests : getProperties() — map brute
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Doit retourner une map vide pour getProperties() quand aucune propriété n'est chargée")
    void doit_retourner_une_map_vide_pour_getproperties_quand_aucune_propriete_n_est_chargee() {

        // Given
        Environment env = new Environment(new String[0]);

        // When
        Map<String, Object> props = env.getProperties();

        // Then
        assertThat(props).isNotNull();
        assertThat(props).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner les propriétés brutes après putProperties")
    void doit_retourner_les_proprietes_brutes_apres_putproperties() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperties(Map.of("server", Map.of("port", 8080)));

        // When
        Map<String, Object> props = env.getProperties();

        // Then
        assertThat(props).containsKey("server");
        @SuppressWarnings("unchecked")
        Map<String, Object> server = (Map<String, Object>) props.get("server");
        assertThat(server).containsEntry("port", 8080);
    }

    @Test
    @DisplayName("Doit retourner la même référence de map à chaque appel de getProperties()")
    void doit_retourner_la_meme_reference_de_map_a_chaque_appel_de_getproperties() {

        // Given
        Environment env = new Environment(new String[0]);

        // When
        Map<String, Object> props1 = env.getProperties();
        Map<String, Object> props2 = env.getProperties();

        // Then
        assertThat(props1).isSameAs(props2);
    }

    @Test
    @DisplayName("Doit ignorer putProperties null sans modifier les propriétés existantes")
    void doit_ignorer_putproperties_null_sans_modifier_les_proprietes_existantes() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperties(Map.of("key", "value"));

        // When
        env.putProperties(null);

        // Then
        assertThat(env.getProperties()).containsEntry("key", "value");
    }

    @Test
    @DisplayName("Doit ignorer putProperties vide sans modifier les propriétés existantes")
    void doit_ignorer_putproperties_vide_sans_modifier_les_proprietes_existantes() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperties(Map.of("key", "value"));

        // When
        env.putProperties(Map.of());

        // Then
        assertThat(env.getProperties()).containsEntry("key", "value");
    }

    // -------------------------------------------------------------------------
    // Tests : getFlatProperties()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Doit retourner une map vide pour getFlatProperties() quand aucune propriété n'est définie")
    void doit_retourner_une_map_vide_pour_getflatproperties_quand_aucune_propriete_n_est_definie() {

        // Given
        Environment env = new Environment(new String[0]);

        // When
        Map<String, Object> flat = env.getFlatProperties();

        // Then
        assertThat(flat).isNotNull();
        assertThat(flat).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner les propriétés plates après putProperty")
    void doit_retourner_les_proprietes_plates_apres_putproperty() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperty("server.port", 9090);
        env.putProperty("app.name", "microbean");

        // When
        Map<String, Object> flat = env.getFlatProperties();

        // Then
        assertThat(flat).containsEntry("server.port", 9090);
        assertThat(flat).containsEntry("app.name", "microbean");
    }

    @Test
    @DisplayName("Doit retourner la même référence de map à chaque appel de getFlatProperties()")
    void doit_retourner_la_meme_reference_de_map_a_chaque_appel_de_getflatproperties() {

        // Given
        Environment env = new Environment(new String[0]);

        // When
        Map<String, Object> flat1 = env.getFlatProperties();
        Map<String, Object> flat2 = env.getFlatProperties();

        // Then
        assertThat(flat1).isSameAs(flat2);
    }

    @Test
    @DisplayName("Doit accumuler les propriétés plates lors de plusieurs appels putProperty")
    void doit_accumuler_les_proprietes_plates_lors_de_plusieurs_appels_putproperty() {

        // Given
        Environment env = new Environment(new String[0]);

        // When
        env.putProperty("key1", "v1");
        env.putProperty("key2", 42);
        env.putProperty("key3", true);

        // Then
        Map<String, Object> flat = env.getFlatProperties();
        assertThat(flat).hasSize(3);
        assertThat(flat).containsEntry("key1", "v1");
        assertThat(flat).containsEntry("key2", 42);
        assertThat(flat).containsEntry("key3", true);
    }

    @Test
    @DisplayName("Doit écraser la valeur existante lors de putProperty sur une clé déjà présente")
    void doit_ecraser_la_valeur_existante_lors_de_putproperty_sur_une_cle_deja_presente() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperty("port", 8080);

        // When
        env.putProperty("port", 9090);

        // Then
        assertThat(env.getFlatProperties()).containsEntry("port", 9090);
    }

    // -------------------------------------------------------------------------
    // Tests : getProperty(String key)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Doit retourner null pour getProperty quand la clé est inconnue")
    void doit_retourner_null_pour_getproperty_quand_la_cle_est_inconnue() {

        // Given
        Environment env = new Environment(new String[0]);

        // When
        Object result = env.getProperty("inexistant");

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Doit retourner la valeur correcte pour getProperty après putProperty")
    void doit_retourner_la_valeur_correcte_pour_getproperty_apres_putproperty() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperty("app.debug", true);

        // When
        Object result = env.getProperty("app.debug");

        // Then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("Doit retourner null pour getProperty avec une clé null")
    void doit_retourner_null_pour_getproperty_avec_une_cle_null() {

        // Given
        Environment env = new Environment(new String[0]);

        // When
        Object result = env.getProperty(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Doit retourner la dernière valeur après plusieurs putProperty sur la même clé")
    void doit_retourner_la_derniere_valeur_apres_plusieurs_putproperty_sur_la_meme_cle() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperty("server.host", "localhost");
        env.putProperty("server.host", "production.example.com");

        // When
        Object result = env.getProperty("server.host");

        // Then
        assertThat(result).isEqualTo("production.example.com");
    }

    @Test
    @DisplayName("Doit accéder aux clés plates injectées manuellement via putProperty depuis la map getFlatProperties")
    void doit_acceder_aux_cles_plates_injectees_manuellement_via_getflatproperties() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperty("feature.flag", "enabled");

        // When
        Object viaGetProperty = env.getProperty("feature.flag");
        Object viaFlatMap = env.getFlatProperties().get("feature.flag");

        // Then — les deux accès doivent retourner la même valeur
        assertThat(viaGetProperty).isEqualTo("enabled");
        assertThat(viaFlatMap).isEqualTo("enabled");
        assertThat(viaGetProperty).isEqualTo(viaFlatMap);
    }

    private static class AppProperties {

        public int serverPort;
        public String serviceName;
    }

    private static class NestedProperties {

        public DatabaseProperties database;
    }

    private static class DatabaseProperties {

        public String hostName;
        public int maxPoolSize;
    }

    private static class NestedApplicationProperties {

        public ApplicationProperties application;
    }

    private static class ApplicationProperties {

        public String name;
        public int port;
        public boolean debug;
    }
}
