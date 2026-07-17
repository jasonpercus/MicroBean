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
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object3;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object4;

@Configuration
@Profile({"debug", "test"})
public class ConfigProfiledKept {

    @Bean
    public Object3 createObject3() {
        return new Object3();
    }

    @Bean
    public Object4 createObject4() {
        return new Object4();
    }
}
