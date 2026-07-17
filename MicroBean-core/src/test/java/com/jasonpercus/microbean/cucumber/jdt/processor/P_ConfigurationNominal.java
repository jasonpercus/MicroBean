package com.jasonpercus.microbean.cucumber.jdt.processor;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Configuration;

@Configuration
public class P_ConfigurationNominal {

    @Bean
    @SuppressWarnings("unused")
    public P_BeanNominal beanNominal() {
        return new P_BeanNominal();
    }
}
