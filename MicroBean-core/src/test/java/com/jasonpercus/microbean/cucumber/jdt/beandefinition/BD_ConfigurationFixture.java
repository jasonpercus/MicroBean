package com.jasonpercus.microbean.cucumber.jdt.beandefinition;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Primary;
import com.jasonpercus.microbean.api.Scope;

public class BD_ConfigurationFixture {

    @Bean(name = "bean-cucumber", scope = Scope.PROTOTYPE)
    @Primary
    public BD_BeanMethodeFixture creer_bean_depuis_methode() {
        return new BD_BeanMethodeFixture();
    }

    public BD_BeanMethodeFixture methode_non_bean() {
        return new BD_BeanMethodeFixture();
    }
}
