package com.jasonpercus.microbean.infrastructure.validator;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires de Validator")
class ValidatorTest {

    @Test
    @DisplayName("Doit appliquer la logique de validate(T)")
    void doit_appliquer_la_logique_de_validate_avec_parametre() {

        // Given
        SpyValidator validator = new SpyValidator("enabled"::equals);

        // When
        boolean enabledResult = validator.validate("enabled");
        boolean disabledResult = validator.validate("disabled");

        // Then
        assertThat(enabledResult).isTrue();
        assertThat(disabledResult).isFalse();
    }

    @Test
    @DisplayName("Doit deleguer validate() vers validate(null)")
    void doit_deleguer_validate_sans_parametre_vers_validate_null() {

        // Given
        SpyValidator validator = new SpyValidator(Objects::isNull);

        // When
        boolean result = validator.validate();

        // Then
        assertThat(result).isTrue();
        assertThat(validator.getLastParameter()).isNull();
    }

    @Test
    @DisplayName("Doit inverser le resultat avec invalidate(T)")
    void doit_inverser_le_resultat_avec_invalidate_avec_parametre() {

        // Given
        SpyValidator validator = new SpyValidator("enabled"::equals);

        // When
        boolean enabledResult = validator.invalidate("enabled");
        boolean disabledResult = validator.invalidate("disabled");

        // Then
        assertThat(enabledResult).isFalse();
        assertThat(disabledResult).isTrue();
    }

    @Test
    @DisplayName("Doit deleguer invalidate() vers invalidate(null)")
    void doit_deleguer_invalidate_sans_parametre_vers_invalidate_null() {

        // Given
        SpyValidator validator = new SpyValidator(parameter -> false);

        // When
        boolean result = validator.invalidate();

        // Then
        assertThat(result).isTrue();
        assertThat(validator.getLastParameter()).isNull();
    }

    private static final class SpyValidator implements Validator<String> {

        private final Predicate<String> predicate;
        private final AtomicReference<String> lastParameter = new AtomicReference<>();

        private SpyValidator(Predicate<String> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean validate(String parameter) {

            lastParameter.set(parameter);
            return predicate.test(parameter);
        }

        private String getLastParameter() {

            return lastParameter.get();
        }
    }
}
