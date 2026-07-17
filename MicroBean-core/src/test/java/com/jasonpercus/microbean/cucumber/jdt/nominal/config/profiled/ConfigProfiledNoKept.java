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
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object5;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object6;

@Configuration
@Profile("prod")
public class ConfigProfiledNoKept {

    @Bean
    public Object5 createObject5() {
        return new Object5();
    }

    @Bean
    public Object6 createObject6() {
        return new Object6();
    }
}
