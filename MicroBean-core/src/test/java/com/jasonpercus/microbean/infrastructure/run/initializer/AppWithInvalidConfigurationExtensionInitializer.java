package com.jasonpercus.microbean.infrastructure.run.initializer;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.MicroBeanApplication;

@MicroBeanApplication(configurationProperties = "initializer/application-config.txt")
public class AppWithInvalidConfigurationExtensionInitializer {

}

