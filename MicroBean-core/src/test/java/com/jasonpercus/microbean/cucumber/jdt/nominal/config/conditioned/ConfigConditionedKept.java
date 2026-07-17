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
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object10;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object9;

@Configuration
@Profile("test")
@Condition(OkCondition.class)
public class ConfigConditionedKept {

    @Bean
    public Object9 createObject9() {
        return new Object9();
    }

    @Bean
    public Object10 createObject10() {
        return new Object10();
    }
}
