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
        assertThat(properties.getServerPort()).isEqualTo(8080);
        assertThat(properties.getServiceName()).isEqualTo("microbean");
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
        assertThat(properties.getDatabase()).isNotNull();
        assertThat(properties.getDatabase().getHostName()).isEqualTo("localhost");
        assertThat(properties.getDatabase().getMaxPoolSize()).isEqualTo(16);
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
        assertThat(properties.getServerPort()).isEqualTo(0);
        assertThat(properties.getServiceName()).isNull();
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
    @DisplayName("Doit échouer si une clé inconnue est présente")
    void doit_echouer_si_une_cle_inconnue_est_presente() {

        // Given
        Environment env = new Environment(new String[0]);
        env.putProperties(Map.of("unknown-key", "value"));

        // When & Then
        assertThatThrownBy(() -> env.getProperties(AppProperties.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static class AppProperties {

        private int serverPort;
        private String serviceName;

        public int getServerPort() {
            return serverPort;
        }

        public void setServerPort(int serverPort) {
            this.serverPort = serverPort;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }
    }

    private static class NestedProperties {

        private DatabaseProperties database;

        public DatabaseProperties getDatabase() {
            return database;
        }

        public void setDatabase(DatabaseProperties database) {
            this.database = database;
        }
    }

    private static class DatabaseProperties {

        private String hostName;
        private int maxPoolSize;

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }
    }
}
