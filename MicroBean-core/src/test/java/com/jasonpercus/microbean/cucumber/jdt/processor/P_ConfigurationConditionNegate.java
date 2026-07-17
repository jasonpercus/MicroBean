package com.jasonpercus.microbean.cucumber.jdt.processor;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.Configuration;

@Configuration
public class P_ConfigurationConditionNegate {

    @Bean
    @Condition(value = P_ConditionAlwaysTrue.class, negate = true)
    @SuppressWarnings("unused")
    public P_BeanConditionNegate beanConditionNegatee() {
        return new P_BeanConditionNegate();
    }
}
