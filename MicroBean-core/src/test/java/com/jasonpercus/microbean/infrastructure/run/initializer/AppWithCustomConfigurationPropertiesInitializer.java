package com.jasonpercus.microbean.infrastructure.run.initializer;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.MicroBeanApplication;

@MicroBeanApplication(configurationProperties = {
        "initializer/custom/microbean.json",
        "initializer/custom/feature.yaml",
        "initializer/custom/flags.yml"
})
public class AppWithCustomConfigurationPropertiesInitializer {

}
