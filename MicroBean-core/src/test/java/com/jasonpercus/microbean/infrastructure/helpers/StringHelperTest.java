package com.jasonpercus.microbean.infrastructure.helpers;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires de StringHelper")
class StringHelperTest {

    @Test
    @DisplayName("Doit abréger le nom complet d'une classe du projet")
    void doit_abreger_le_nom_complet_d_une_classe_du_projet() {

        // Given
        Class<?> clazz = StringHelper.class;

        // When
        String result = StringHelper.abbreviateClassName(clazz);

        // Then
        assertThat(result).isEqualTo("c.j.m.i.h.StringHelper");
    }

    @Test
    @DisplayName("Doit abréger le nom complet d'une classe du JDK")
    void doit_abreger_le_nom_complet_d_une_classe_du_jdk() {

        // Given
        Class<?> clazz = String.class;

        // When
        String result = StringHelper.abbreviateClassName(clazz);

        // Then
        assertThat(result).isEqualTo("j.l.String");
    }

    @Test
    @DisplayName("Doit abréger un nom de méthode avec le format classe#methode")
    void doit_abreger_un_nom_de_methode_avec_le_format_classe_methode() throws Exception {

        // Given
        Method method = Fixture.class.getDeclaredMethod("traiter", String.class);

        // When
        String result = StringHelper.abbreviateMethodName(Fixture.class, method);

        // Then
        assertThat(result).isEqualTo("c.j.m.i.h.StringHelperTest$Fixture#traiter");
    }

    @Test
    @DisplayName("Doit lever une exception si la classe est null lors de l'abréviation")
    @SuppressWarnings("ConstantConditions")
    void doit_lever_une_exception_si_la_classe_est_null_lors_de_l_abreviation() {

        // Given
        Class<?> clazz = null;

        // When & Then
        assertThatThrownBy(() -> StringHelper.abbreviateClassName(clazz))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Doit lever une exception si la méthode est null lors de l'abréviation")
    @SuppressWarnings("ConstantConditions")
    void doit_lever_une_exception_si_la_methode_est_null_lors_de_l_abreviation() {

        // Given
        Method method = null;

        // When & Then
        assertThatThrownBy(() -> StringHelper.abbreviateMethodName(Fixture.class, method))
                .isInstanceOf(NullPointerException.class);
    }

    private static class Fixture {

        @SuppressWarnings("unused")
        private void traiter(String value) {

        }
    }
}
