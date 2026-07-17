package com.jasonpercus.microbean.infrastructure.run;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.infrastructure.Constants.PACKAGE_ENTRYPOINTS;
import static com.jasonpercus.microbean.infrastructure.Constants.PACKAGE_SERVICES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.infrastructure.run.initializer.AppWithExplicitScanPackagesInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.InvalidAppAnnotatedAsEntryPointInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.NotAnnotatedAppInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.NotAnnotatedEntryPointInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.ValidAppInitializer;
import com.jasonpercus.microbean.infrastructure.run.initializer.ValidEntryPointInitializer;
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
        assertThat(initializer.getPackagesPathsToScan()).containsExactly("test.pkg.one", "test.pkg.two", PACKAGE_ENTRYPOINTS, PACKAGE_SERVICES);
    }

    @Test
    @DisplayName("Doit utiliser le package de l'application si aucun scan package n'est défini")
    void doit_utiliser_le_package_de_l_application_si_aucun_scanpackage_n_est_defini() {
        
        // Given
        Class<? extends ApplicationEntryPoint>[] entryPoints = singleEntryPoint(ValidEntryPointInitializer.class);
        String[] expectedScanPackages = new String[] {
                "com.jasonpercus.microbean.infrastructure.run.initializer",
                PACKAGE_ENTRYPOINTS,
                PACKAGE_SERVICES
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
}
