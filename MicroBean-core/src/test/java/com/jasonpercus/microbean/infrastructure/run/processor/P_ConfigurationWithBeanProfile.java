package com.jasonpercus.microbean.infrastructure.run.processor;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.Profile;

@Configuration
public class P_ConfigurationWithBeanProfile {

    @Bean
    @Profile("dev")
    @SuppressWarnings("unused")
    public P_BeanProfile beanProfil() {
        return new P_BeanProfile();
    }
}
