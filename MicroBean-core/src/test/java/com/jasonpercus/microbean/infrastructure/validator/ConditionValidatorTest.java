package com.jasonpercus.microbean.infrastructure.validator;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.ConditionEvaluator;
import com.jasonpercus.microbean.infrastructure.exception.MicroBeanException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires de ConditionValidator")
public class ConditionValidatorTest {

    @Test
    @DisplayName("Doit lever une exception à la validation d'une condition qui échoue")
    void doit_lever_une_exception_a_la_validation() {

        // Given
        Condition condition = TestFailed.class.getAnnotation(Condition.class);
        ConditionValidator validator = new ConditionValidator(condition);

        // When & Then
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(MicroBeanException.class)
                .hasMessageContaining("Failed to evaluate condition");
    }

    @Test
    @DisplayName("Doit valider une condition qui réussit")
    void doit_valider_une_condition() {

        // Given
        Condition condition = TestSuccess.class.getAnnotation(Condition.class);
        ConditionValidator validator = new ConditionValidator(condition);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Doit invalider une condition avec negate=true")
    void doit_invalider_une_condition() {

        // Given
        Condition condition = TestNegateSuccess.class.getAnnotation(Condition.class);
        ConditionValidator validator = new ConditionValidator(condition);

        // When
        boolean result = validator.validate(null);

        // Then
        assertThat(result).isFalse();
    }

    @Condition(EvaluatorFailed.class)
    private static class TestFailed {
    }

    private static class EvaluatorFailed implements ConditionEvaluator {

        @Override
        public boolean validate(String[] args) {
            return true;
        }
    }

    @Condition(EvaluatorSuccess.class)
    public static class TestSuccess {
    }

    @Condition(value = EvaluatorSuccess.class, negate = true)
    public static class TestNegateSuccess {
    }

    public static class EvaluatorSuccess implements ConditionEvaluator {

        @Override
        public boolean validate(String[] args) {
            return true;
        }
    }
}
