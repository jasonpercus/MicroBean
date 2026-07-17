package com.jasonpercus.microbean.cucumber.jdt.nominal.config.conditioned;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object11;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object12;

@Configuration
@Profile("test")
@Condition(value = OkCondition.class, negate = true)
public class ConfigConditionedNoKept {

    @Bean
    public Object11 createObject11() {
        return new Object11();
    }

    @Bean
    public Object12 createObject12() {
        return new Object12();
    }
}
