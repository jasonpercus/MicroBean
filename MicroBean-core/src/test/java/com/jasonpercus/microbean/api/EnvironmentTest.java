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
