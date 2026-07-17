package com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.named;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.Named;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object1;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object14;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object3;

@Configuration
public class AdapterNamedConfig {

    @Bean(name = "AdapterNamed2")
    public AdapterNamedByClassSPI createAdapterNamedByClassAPI(Object1 object1,
                                                               Object3 object3,
                                                               @Named("14.2") Object14 object14_2,
                                                               @Named("14.3") Object14 object14_3) {
        return new AdapterNamedByClass(object1, object3, object14_2, object14_3);
    }

    @Bean(name = "AdapterNamed1")
    public AdapterNamedByBeanSPI createAdapterNamedByBeanAPI(Object1 object1,
                                                             Object3 object3,
                                                             @Named("14.2") Object14 object14_2,
                                                             @Named("14.3") Object14 object14_3) {
        return new AdapterNamedByBean(object1, object3, object14_2, object14_3);
    }
}
