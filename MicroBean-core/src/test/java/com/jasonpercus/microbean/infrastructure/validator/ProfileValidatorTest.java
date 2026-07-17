package com.jasonpercus.microbean.infrastructure.validator;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.jasonpercus.microbean.api.Profile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires de ProfileValidator")
class ProfileValidatorTest {

    private final String originalProfile = System.getProperty("app.profile");

    @AfterEach
    void doit_restaurer_le_profil_systeme() {
        restorePropertyAppProfile(originalProfile);
    }

    @Test
    @DisplayName("Doit valider quand aucun profil actif n'est défini")
    void doit_valider_quand_aucun_profil_actif_n_est_defini() {

        // Given
        System.clearProperty("app.profile");
        Profile profile = WithDebugAndReleaseProfiles.class.getAnnotation(Profile.class);
        ProfileValidator validator = new ProfileValidator(profile);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit valider quand le profil actif est vide")
    void doit_valider_quand_le_profil_actif_est_vide() {

        // Given
        System.setProperty("app.profile", "");
        Profile profile = WithDebugAndReleaseProfiles.class.getAnnotation(Profile.class);
        ProfileValidator validator = new ProfileValidator(profile);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit valider quand aucun profil autorisé n'est défini")
    void doit_valider_quand_aucun_profil_autorise_n_est_defini() {

        // Given
        System.setProperty("app.profile", "debug");
        Profile profile = WithEmptyProfiles.class.getAnnotation(Profile.class);
        ProfileValidator validator = new ProfileValidator(profile);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit valider quand le profil actif est autorisé")
    void doit_valider_quand_le_profil_actif_est_autorise() {

        // Given
        System.setProperty("app.profile", "release");
        Profile profile = WithDebugAndReleaseProfiles.class.getAnnotation(Profile.class);
        ProfileValidator validator = new ProfileValidator(profile);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit invalider quand le profil actif n'est pas autorisé")
    void doit_invalider_quand_le_profil_actif_n_est_pas_autorise() {

        // Given
        System.setProperty("app.profile", "prod");
        Profile profile = WithDebugAndReleaseProfiles.class.getAnnotation(Profile.class);
        ProfileValidator validator = new ProfileValidator(profile);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Doit valider quand la liste des profils autorisés est null")
    void doit_valider_quand_la_liste_des_profils_autorises_est_null() {

        // Given
        System.setProperty("app.profile", "debug");
        Profile profile = mock(Profile.class);
        when(profile.value()).thenReturn(null);
        ProfileValidator validator = new ProfileValidator(profile);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    private static void restorePropertyAppProfile(String value) {
        if (value == null)
            System.clearProperty("app.profile");
        else
            System.setProperty("app.profile", value);
    }

    @Profile({"debug", "release"})
    private static class WithDebugAndReleaseProfiles {

    }

    @Profile({})
    private static class WithEmptyProfiles {

    }
}
