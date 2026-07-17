package com.jasonpercus.microbean.infrastructure.run.initializer;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;

@EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
public class ValidEntryPointInitializer implements ApplicationEntryPoint {

    @Override
    public void main(String[] args) {

    }
}
