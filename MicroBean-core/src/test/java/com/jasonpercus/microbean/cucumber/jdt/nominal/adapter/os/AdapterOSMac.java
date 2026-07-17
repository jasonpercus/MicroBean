package com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.os;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.Named;
import com.jasonpercus.microbean.api.OS;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object1;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object14;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object3;

@Adapter(name = "specific", os = OS.MAC)
public class AdapterOSMac implements AdapterOSSPI {

    private final Object1 object1;
    private final Object3 object3;
    private final Object14 object14_2;
    private final Object14 object14_3;

    public AdapterOSMac(Object1 object1,
                              Object3 object3,
                              @Named("14.2") Object14 object14_2,
                              @Named("14.3") Object14 object14_3) {
        this.object1 = object1;
        this.object3 = object3;
        this.object14_2 = object14_2;
        this.object14_3 = object14_3;
    }

    @Override
    public void execute() {
        boolean injected = this.object1 != null
                && this.object3 != null
                && this.object14_2 != null
                && this.object14_3 != null;

        System.out.println(this.getClass().getSimpleName() + " is injected: " + injected);
    }
}
