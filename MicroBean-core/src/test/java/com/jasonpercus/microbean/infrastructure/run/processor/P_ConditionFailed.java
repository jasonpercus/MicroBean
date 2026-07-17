package com.jasonpercus.microbean.infrastructure.run.processor;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.ConditionEvaluator;

public class P_ConditionFailed implements ConditionEvaluator {

    @Override
    public boolean validate(String[] args) {
        throw new IllegalStateException("Erreur conditionnelle volontaire");
    }
}
