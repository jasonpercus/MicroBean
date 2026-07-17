package com.jasonpercus.microbean.cucumber.jdt.appexecutor;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.util.Arrays;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;

@EntryPointService(lifecycle = LifecycleEntryPoint.LONG_RUNNING)
public class AE_LongRunningService implements ApplicationEntryPoint {

    @Override
    public void main(String[] args) {
        System.out.println(getClass().getSimpleName() + " is running on thread [" + Thread.currentThread().getName() + "]");
        System.out.println(getClass().getSimpleName() + " args=" + Arrays.toString(args));
    }
}
