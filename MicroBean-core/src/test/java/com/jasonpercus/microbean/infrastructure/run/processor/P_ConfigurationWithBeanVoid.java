package com.jasonpercus.microbean.infrastructure.run.processor;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Configuration;

@Configuration
public class P_ConfigurationWithBeanVoid {

    @Bean
    @SuppressWarnings("unused")
    public Void beanInvalide() {
        return null;
    }
}
