package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
}
