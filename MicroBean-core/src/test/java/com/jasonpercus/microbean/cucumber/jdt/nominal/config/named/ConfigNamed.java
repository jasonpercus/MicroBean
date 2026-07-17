package com.jasonpercus.microbean.cucumber.jdt.nominal.config.named;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.Primary;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object14;

@Configuration
public class ConfigNamed {

    @Bean
    public Object14 createObject14_1() {
        return new Object14("14.1");
    }

    @Bean(name = "14.2")
    public Object14 createObject14_2() {
        return new Object14("14.2");
    }

    @Bean(name = "14.3")
    @Primary
    public Object14 createObject14_3() {
        return new Object14("14.3");
    }

    @Bean(name = "14.3")
    public Object14 createObject14_4() {
        return new Object14("14.4");
    }
}
