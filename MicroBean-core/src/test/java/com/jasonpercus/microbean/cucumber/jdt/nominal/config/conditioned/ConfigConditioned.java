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
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object7;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object8;

@Configuration
public class ConfigConditioned {

    @Bean
    @Profile("test")
    @Condition(PairCondition.class)
    public Object7 createObject7() {
        return new Object7();
    }

    @Bean
    @Profile("test")
    @Condition(value = PairCondition.class, negate = true)
    public Object8 createObject8() {
        return new Object8();
    }
}
