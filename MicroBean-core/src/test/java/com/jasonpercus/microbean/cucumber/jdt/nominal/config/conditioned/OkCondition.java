package com.jasonpercus.microbean.cucumber.jdt.nominal.config.conditioned;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.ConditionEvaluator;

public class OkCondition implements ConditionEvaluator {

    @Override
    public boolean validate(String[] args) {
        return true;
    }
}
