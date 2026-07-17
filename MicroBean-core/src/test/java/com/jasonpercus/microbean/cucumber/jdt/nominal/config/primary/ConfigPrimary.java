package com.jasonpercus.microbean.cucumber.jdt.nominal.config.primary;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.Primary;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object13;

@Configuration
public class ConfigPrimary {

    @Bean
    public Object13 createObject13_1() {
        return new Object13("NOK 1");
    }

    @Bean
    @Primary
    public Object13 createObject13_2() {
        return new Object13("OK");
    }

    @Bean
    public Object13 createObject13_3() {
        return new Object13("NOK 2");
    }
}
