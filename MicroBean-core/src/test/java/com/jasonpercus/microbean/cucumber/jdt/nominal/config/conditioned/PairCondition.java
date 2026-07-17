package com.jasonpercus.microbean.cucumber.jdt.nominal.config.conditioned;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.ConditionEvaluator;

public class PairCondition implements ConditionEvaluator {

    @Override
    public boolean validate(String[] args) {
        int a = Integer.parseInt(args[1]);
        return a % 2 == 0;
    }
}
