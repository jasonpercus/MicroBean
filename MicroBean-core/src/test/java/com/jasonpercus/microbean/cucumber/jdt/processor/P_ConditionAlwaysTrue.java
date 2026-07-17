package com.jasonpercus.microbean.cucumber.jdt.processor;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.ConditionEvaluator;

public class P_ConditionAlwaysTrue implements ConditionEvaluator {

    @Override
    public boolean validate(String[] args) {
        return true;
    }
}
