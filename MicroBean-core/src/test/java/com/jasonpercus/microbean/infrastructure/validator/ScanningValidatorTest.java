package com.jasonpercus.microbean.infrastructure.validator;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.ConditionEvaluator;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;
import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.api.Service;
import com.jasonpercus.microbean.infrastructure.exception.MicroBeanException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires de ScanningValidator")
class ScanningValidatorTest {

    private final String originalProfile = System.getProperty("app.profile");

    @AfterEach
    void doit_restaurer_le_profil_systeme() {
        restorePropertyAppProfile(originalProfile);
    }

    // -------------------------------------------------------------------------
    // Cas : aucune annotation de composant
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Doit retourner false quand la classe n'a aucune annotation de composant")
    void doit_retourner_false_quand_aucune_annotation_de_composant() {

        // Given
        ScanningValidator validator = new ScanningValidator(NoAnnotationClass.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    // -------------------------------------------------------------------------
    // Cas : annotations multiples
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Doit lever une exception quand plusieurs annotations de composant sont présentes")
    void doit_lever_une_exception_quand_plusieurs_annotations_de_composant() {

        // Given
        ScanningValidator validator = new ScanningValidator(ServiceAndAdapterClass.class, new String[0]);

        // When & Then
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(MicroBeanException.class)
                .hasMessageContaining("multiple");
    }

    // -------------------------------------------------------------------------
    // Cas : @EntryPointService
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Doit valider une classe annotée @EntryPointService sans condition")
    void doit_valider_une_classe_entry_point_service() {

        // Given
        ScanningValidator validator = new ScanningValidator(SimpleEntryPointClass.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    // -------------------------------------------------------------------------
    // Cas : @Service
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Doit valider un @Service sans profil ni condition")
    void doit_valider_un_service_sans_profil_ni_condition() {

        // Given
        ScanningValidator validator = new ScanningValidator(SimpleServiceClass.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit invalider un @Service quand le profil actif ne correspond pas")
    void doit_invalider_un_service_quand_le_profil_ne_correspond_pas() {

        // Given
        System.setProperty("app.profile", "prod");
        ScanningValidator validator = new ScanningValidator(ServiceWithDebugProfile.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Doit valider un @Service quand le profil actif correspond")
    void doit_valider_un_service_quand_le_profil_actif_correspond() {

        // Given
        System.setProperty("app.profile", "debug");
        ScanningValidator validator = new ScanningValidator(ServiceWithDebugProfile.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit invalider un @Service quand la condition n'est pas satisfaite")
    void doit_invalider_un_service_quand_la_condition_n_est_pas_satisfaite() {

        // Given
        ScanningValidator validator = new ScanningValidator(ServiceWithFalseCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Doit valider un @Service quand la condition est satisfaite")
    void doit_valider_un_service_quand_la_condition_est_satisfaite() {

        // Given
        ScanningValidator validator = new ScanningValidator(ServiceWithTrueCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit invalider un @Service quand negate=true et l'évaluateur retourne true")
    void doit_invalider_un_service_quand_negate_true_et_evaluateur_retourne_true() {

        // Given
        ScanningValidator validator = new ScanningValidator(ServiceWithNegateTrueCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Doit valider un @Service quand negate=true et l'évaluateur retourne false")
    void doit_valider_un_service_quand_negate_true_et_evaluateur_retourne_false() {

        // Given
        ScanningValidator validator = new ScanningValidator(ServiceWithNegateFalseCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    // -------------------------------------------------------------------------
    // Cas : @Adapter
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Doit valider un @Adapter sans profil ni condition")
    void doit_valider_un_adapter_sans_profil_ni_condition() {

        // Given
        ScanningValidator validator = new ScanningValidator(SimpleAdapterClass.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit invalider un @Adapter quand le profil actif ne correspond pas")
    void doit_invalider_un_adapter_quand_le_profil_ne_correspond_pas() {

        // Given
        System.setProperty("app.profile", "prod");
        ScanningValidator validator = new ScanningValidator(AdapterWithDebugProfile.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Doit valider un @Adapter quand le profil actif correspond")
    void doit_valider_un_adapter_quand_le_profil_actif_correspond() {

        // Given
        System.setProperty("app.profile", "debug");
        ScanningValidator validator = new ScanningValidator(AdapterWithDebugProfile.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit invalider un @Adapter quand la condition n'est pas satisfaite")
    void doit_invalider_un_adapter_quand_la_condition_n_est_pas_satisfaite() {

        // Given
        ScanningValidator validator = new ScanningValidator(AdapterWithFalseCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Doit valider un @Adapter quand la condition est satisfaite")
    void doit_valider_un_adapter_quand_la_condition_est_satisfaite() {

        // Given
        ScanningValidator validator = new ScanningValidator(AdapterWithTrueCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit invalider un @Adapter quand negate=true et l'évaluateur retourne true")
    void doit_invalider_un_adapter_quand_negate_true_et_evaluateur_retourne_true() {

        // Given
        ScanningValidator validator = new ScanningValidator(AdapterWithNegateTrueCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Doit valider un @Adapter quand negate=true et l'évaluateur retourne false")
    void doit_valider_un_adapter_quand_negate_true_et_evaluateur_retourne_false() {

        // Given
        ScanningValidator validator = new ScanningValidator(AdapterWithNegateFalseCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    // -------------------------------------------------------------------------
    // Cas : @Configuration
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Doit valider une @Configuration sans profil ni condition")
    void doit_valider_une_configuration_sans_profil_ni_condition() {

        // Given
        ScanningValidator validator = new ScanningValidator(SimpleConfigClass.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit invalider une @Configuration quand le profil actif ne correspond pas")
    void doit_invalider_une_configuration_quand_le_profil_ne_correspond_pas() {

        // Given
        System.setProperty("app.profile", "prod");
        ScanningValidator validator = new ScanningValidator(ConfigWithDebugProfile.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Doit valider une @Configuration quand le profil actif correspond")
    void doit_valider_une_configuration_quand_le_profil_actif_correspond() {

        // Given
        System.setProperty("app.profile", "debug");
        ScanningValidator validator = new ScanningValidator(ConfigWithDebugProfile.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit invalider une @Configuration quand la condition n'est pas satisfaite")
    void doit_invalider_une_configuration_quand_la_condition_n_est_pas_satisfaite() {

        // Given
        ScanningValidator validator = new ScanningValidator(ConfigWithFalseCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Doit valider une @Configuration quand la condition est satisfaite")
    void doit_valider_une_configuration_quand_la_condition_est_satisfaite() {

        // Given
        ScanningValidator validator = new ScanningValidator(ConfigWithTrueCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit invalider une @Configuration quand negate=true et l'évaluateur retourne true")
    void doit_invalider_une_configuration_quand_negate_true_et_evaluateur_retourne_true() {

        // Given
        ScanningValidator validator = new ScanningValidator(ConfigWithNegateTrueCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Doit valider une @Configuration quand negate=true et l'évaluateur retourne false")
    void doit_valider_une_configuration_quand_negate_true_et_evaluateur_retourne_false() {

        // Given
        ScanningValidator validator = new ScanningValidator(ConfigWithNegateFalseCondition.class, new String[0]);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    // -------------------------------------------------------------------------
    // Classes utilitaires de test
    // -------------------------------------------------------------------------

    private static void restorePropertyAppProfile(String value) {
        if (value == null) System.clearProperty("app.profile");
        else System.setProperty("app.profile", value);
    }

    static class NoAnnotationClass {}

    @Service
    @Adapter
    static class ServiceAndAdapterClass {}

    @EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
    static class SimpleEntryPointClass {}

    @Service
    static class SimpleServiceClass {}

    @Service
    @Profile({"debug"})
    static class ServiceWithDebugProfile {}

    @Service
    @Condition(AlwaysFalseEvaluator.class)
    static class ServiceWithFalseCondition {}

    @Service
    @Condition(AlwaysTrueEvaluator.class)
    static class ServiceWithTrueCondition {}

    @Service
    @Condition(value = AlwaysTrueEvaluator.class, negate = true)
    static class ServiceWithNegateTrueCondition {}

    @Service
    @Condition(value = AlwaysFalseEvaluator.class, negate = true)
    static class ServiceWithNegateFalseCondition {}

    @Adapter
    static class SimpleAdapterClass {}

    @Adapter
    @Profile({"debug"})
    static class AdapterWithDebugProfile {}

    @Adapter
    @Condition(AlwaysFalseEvaluator.class)
    static class AdapterWithFalseCondition {}

    @Adapter
    @Condition(AlwaysTrueEvaluator.class)
    static class AdapterWithTrueCondition {}

    @Adapter
    @Condition(value = AlwaysTrueEvaluator.class, negate = true)
    static class AdapterWithNegateTrueCondition {}

    @Adapter
    @Condition(value = AlwaysFalseEvaluator.class, negate = true)
    static class AdapterWithNegateFalseCondition {}

    @Configuration
    static class SimpleConfigClass {}

    @Configuration
    @Profile({"debug"})
    static class ConfigWithDebugProfile {}

    @Configuration
    @Condition(AlwaysFalseEvaluator.class)
    static class ConfigWithFalseCondition {}

    @Configuration
    @Condition(AlwaysTrueEvaluator.class)
    static class ConfigWithTrueCondition {}

    @Configuration
    @Condition(value = AlwaysTrueEvaluator.class, negate = true)
    static class ConfigWithNegateTrueCondition {}

    @Configuration
    @Condition(value = AlwaysFalseEvaluator.class, negate = true)
    static class ConfigWithNegateFalseCondition {}

    public static class AlwaysTrueEvaluator implements ConditionEvaluator {

        @Override
        public boolean validate(String[] args) { return true; }
    }

    public static class AlwaysFalseEvaluator implements ConditionEvaluator {

        @Override
        public boolean validate(String[] args) { return false; }
    }
}
