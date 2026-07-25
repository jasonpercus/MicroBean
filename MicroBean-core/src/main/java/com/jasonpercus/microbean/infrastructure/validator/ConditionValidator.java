package com.jasonpercus.microbean.infrastructure.validator;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.ConditionEvaluator;
import com.jasonpercus.microbean.infrastructure.exception.ExceptionManager;

/**
 * Valide une {@link Condition} en instanciant dynamiquement son {@link ConditionEvaluator}.
 * <p>
 * Cette implémentation délègue la décision à l'évaluateur déclaré par
 * {@link Condition#value()}. Si {@link Condition#negate()} vaut {@code true},
 * le résultat est inversé via {@link ConditionEvaluator#invalidate(Object)}.
 * </p>
 */
public class ConditionValidator implements Validator<String[]> {

    /** L'annotation de condition à évaluer. */
    private final Condition condition;

    /**
     * Crée un validateur pour l'annotation {@link Condition} fournie.
     *
     * @param condition annotation de condition à évaluer
     */
    public ConditionValidator(Condition condition) {
        this.condition = condition;
    }

    /**
     * Évalue la condition à partir des arguments applicatifs.
     *
     * @param args arguments de l'application transmis à l'évaluateur
     * @return {@code true} si la condition est satisfaite (ou inversée selon negate), sinon {@code false}
     * @throws RuntimeException si l'instanciation ou l'évaluation de la condition échoue
     */
    @Override
    public boolean validate(String[] args) {
        Class<? extends ConditionEvaluator> conditionClass = condition.value();
        try {
            ConditionEvaluator evaluator = conditionClass.getDeclaredConstructor().newInstance();

            if (condition.negate())
                return evaluator.invalidate(args);
            else
                return evaluator.validate(args);

        } catch (Exception e) {
            throw ExceptionManager.failedToEvaluateCondition(condition, e);
        }
    }
}
