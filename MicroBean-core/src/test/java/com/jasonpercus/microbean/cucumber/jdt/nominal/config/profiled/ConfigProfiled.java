package com.jasonpercus.microbean.cucumber.jdt.nominal.config.profiled;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object1;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object2;

@Configuration
public class ConfigProfiled {

    @Bean
    public Object1 createObject1() {
        return new Object1();
    }

    @Bean
    @Profile("debug")
    public Object2 createObject2() {
        return new Object2();
    }
}
