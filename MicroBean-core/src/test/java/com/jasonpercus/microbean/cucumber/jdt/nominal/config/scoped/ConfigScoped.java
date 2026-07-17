package com.jasonpercus.microbean.cucumber.jdt.nominal.config.scoped;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.Scope;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object15;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object16;

@Configuration
public class ConfigScoped {

    @Bean(scope = Scope.SINGLETON)
    public Object15 createObject15() {
        return new Object15();
    }

    @Bean(scope = Scope.PROTOTYPE)
    public Object16 createObject16() {
        return new Object16();
    }
}
