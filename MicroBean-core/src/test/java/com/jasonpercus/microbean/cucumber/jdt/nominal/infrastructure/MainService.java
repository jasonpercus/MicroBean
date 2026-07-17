package com.jasonpercus.microbean.cucumber.jdt.nominal.infrastructure;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.api.LifecycleEntryPoint.ONE_SHOT;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.Named;
import com.jasonpercus.microbean.api.PostConstruct;
import com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.conditioned.AdapterConditionedKeptSPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.named.AdapterNamedByBeanSPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.named.AdapterNamedByClassSPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.os.AdapterOSSPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.primary.AdapterPrimaryByBeanSPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.primary.AdapterPrimaryByClassSPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.profiled.AdapterProfiledKeptSPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.scoped.AdapterScopedPrototypeSPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.adapter.scoped.AdapterScopedSingletonSPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object1;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object10;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object13;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object14;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object15;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object16;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object3;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object4;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object7;
import com.jasonpercus.microbean.cucumber.jdt.nominal.domain.model.Object9;
import com.jasonpercus.microbean.cucumber.jdt.nominal.service.conditioned.ServiceConditionedKeptAPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.service.named.ServiceNamedByBeanAPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.service.named.ServiceNamedByClassAPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.service.primary.ServicePrimaryByBeanAPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.service.primary.ServicePrimaryByClassAPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.service.profiled.ServiceProfiledKeptAPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.service.scoped.ServiceScopedPrototypeAPI;
import com.jasonpercus.microbean.cucumber.jdt.nominal.service.scoped.ServiceScopedSingletonAPI;

@EntryPointService(lifecycle = ONE_SHOT)
public class MainService implements ApplicationEntryPoint {

    private final Object1 object1;
    private final Object3 object3;
    private final Object4 object4;
    private final Object7 object7;
    private final Object9 object9;
    private final Object10 object10;
    private final Object13 object13;
    private final Object14 object14_2;
    private final Object14 object14_3;
    private final Object15 object15_1;
    private final Object15 object15_2;
    private final Object16 object16_1;
    private final Object16 object16_2;
    private final ServiceConditionedKeptAPI serviceConditionedKeptAPI;
    private final ServiceNamedByClassAPI serviceNamedByClassAPI;
    private final ServiceNamedByBeanAPI serviceNamedByBeanAPI;
    private final ServicePrimaryByClassAPI servicePrimaryByClassAPI;
    private final ServicePrimaryByBeanAPI servicePrimaryByBeanAPI;
    private final ServiceProfiledKeptAPI serviceProfiledKeptAPI;
    private final ServiceScopedSingletonAPI serviceScopedSingletonAPI_1;
    private final ServiceScopedSingletonAPI serviceScopedSingletonAPI_2;
    private final ServiceScopedPrototypeAPI serviceScopedPrototypeAPI_1;
    private final ServiceScopedPrototypeAPI serviceScopedPrototypeAPI_2;
    private final AdapterConditionedKeptSPI adapterConditionedKeptSPI;
    private final AdapterNamedByClassSPI adapterNamedByClassSPI;
    private final AdapterNamedByBeanSPI adapterNamedByBeanSPI;
    private final AdapterPrimaryByClassSPI adapterPrimaryByClassSPI;
    private final AdapterPrimaryByBeanSPI adapterPrimaryByBeanSPI;
    private final AdapterProfiledKeptSPI adapterProfiledKeptSPI;
    private final AdapterScopedSingletonSPI adapterScopedSingletonSPI_1;
    private final AdapterScopedSingletonSPI adapterScopedSingletonSPI_2;
    private final AdapterScopedPrototypeSPI adapterScopedPrototypeSPI_1;
    private final AdapterScopedPrototypeSPI adapterScopedPrototypeSPI_2;
    private final AdapterOSSPI adapterOSSPI_specific;
    private final AdapterOSSPI adapterOSSPI_all;

    public MainService(Object1 object1,
                       Object3 object3,
                       Object4 object4,
                       Object7 object7,
                       Object9 object9,
                       Object10 object10,
                       Object13 object13,
                       @Named("14.2") Object14 object14_2,
                       @Named("14.3") Object14 object14_3,
                       Object15 object15_1,
                       Object15 object15_2,
                       Object16 object16_1,
                       Object16 object16_2,
                       ServiceConditionedKeptAPI serviceConditionedKeptAPI,
                       @Named("ServiceNamed1") ServiceNamedByClassAPI serviceNamedByClassAPI,
                       @Named("ServiceNamed1") ServiceNamedByBeanAPI serviceNamedByBeanAPI,
                       ServicePrimaryByClassAPI servicePrimaryByClassAPI,
                       ServicePrimaryByBeanAPI servicePrimaryByBeanAPI,
                       ServiceProfiledKeptAPI serviceProfiledKeptAPI,
                       ServiceScopedSingletonAPI serviceScopedSingletonAPI_1,
                       ServiceScopedSingletonAPI serviceScopedSingletonAPI_2,
                       ServiceScopedPrototypeAPI serviceScopedPrototypeAPI_1,
                       ServiceScopedPrototypeAPI serviceScopedPrototypeAPI_2,
                       AdapterConditionedKeptSPI adapterConditionedKeptSPI,
                       @Named("AdapterNamed1") AdapterNamedByClassSPI adapterNamedByClassSPI,
                       @Named("AdapterNamed1") AdapterNamedByBeanSPI adapterNamedByBeanSPI,
                       AdapterPrimaryByClassSPI adapterPrimaryByClassSPI,
                       AdapterPrimaryByBeanSPI adapterPrimaryByBeanSPI,
                       AdapterProfiledKeptSPI adapterProfiledKeptSPI,
                       AdapterScopedSingletonSPI adapterScopedSingletonSPI_1,
                       AdapterScopedSingletonSPI adapterScopedSingletonSPI_2,
                       AdapterScopedPrototypeSPI adapterScopedPrototypeSPI_1,
                       AdapterScopedPrototypeSPI adapterScopedPrototypeSPI_2,
                       @Named("specific") AdapterOSSPI adapterOSSPI_specific,
                       @Named("all") AdapterOSSPI adapterOSSPI_all
    ) {
        this.object1 = object1;
        this.object3 = object3;
        this.object4 = object4;
        this.object7 = object7;
        this.object9 = object9;
        this.object10 = object10;
        this.object13 = object13;
        this.object14_2 = object14_2;
        this.object14_3 = object14_3;
        this.object15_1 = object15_1;
        this.object15_2 = object15_2;
        this.object16_1 = object16_1;
        this.object16_2 = object16_2;
        this.serviceConditionedKeptAPI = serviceConditionedKeptAPI;
        this.serviceNamedByClassAPI = serviceNamedByClassAPI;
        this.serviceNamedByBeanAPI = serviceNamedByBeanAPI;
        this.servicePrimaryByClassAPI = servicePrimaryByClassAPI;
        this.servicePrimaryByBeanAPI = servicePrimaryByBeanAPI;
        this.serviceProfiledKeptAPI = serviceProfiledKeptAPI;
        this.serviceScopedSingletonAPI_1 = serviceScopedSingletonAPI_1;
        this.serviceScopedSingletonAPI_2 = serviceScopedSingletonAPI_2;
        this.serviceScopedPrototypeAPI_1 = serviceScopedPrototypeAPI_1;
        this.serviceScopedPrototypeAPI_2 = serviceScopedPrototypeAPI_2;
        this.adapterConditionedKeptSPI = adapterConditionedKeptSPI;
        this.adapterNamedByClassSPI = adapterNamedByClassSPI;
        this.adapterNamedByBeanSPI = adapterNamedByBeanSPI;
        this.adapterPrimaryByClassSPI = adapterPrimaryByClassSPI;
        this.adapterPrimaryByBeanSPI = adapterPrimaryByBeanSPI;
        this.adapterProfiledKeptSPI = adapterProfiledKeptSPI;
        this.adapterScopedSingletonSPI_1 = adapterScopedSingletonSPI_1;
        this.adapterScopedSingletonSPI_2 = adapterScopedSingletonSPI_2;
        this.adapterScopedPrototypeSPI_1 = adapterScopedPrototypeSPI_1;
        this.adapterScopedPrototypeSPI_2 = adapterScopedPrototypeSPI_2;
        this.adapterOSSPI_specific = adapterOSSPI_specific;
        this.adapterOSSPI_all = adapterOSSPI_all;
    }

    @Override
    public void main(String[] args) {
        System.out.println(getClass().getSimpleName() + " is running on thread [%s]".formatted(Thread.currentThread().getName()));
        System.out.println(this.object13);
        System.out.println(this.object14_2);
        System.out.println(this.object14_3);
        System.out.println(this.object15_1.equals(this.object15_2)
                ? "Object15 is a singleton"
                : "Object15 is a prototype");
        System.out.println(this.object16_1.equals(this.object16_2)
                ? "Object16 is a singleton"
                : "Object16 is a prototype");
        this.serviceConditionedKeptAPI.execute();
        this.serviceNamedByClassAPI.execute();
        this.serviceNamedByBeanAPI.execute();
        this.servicePrimaryByClassAPI.execute();
        this.servicePrimaryByBeanAPI.execute();
        this.serviceProfiledKeptAPI.execute();
        this.serviceScopedSingletonAPI_1.execute();
        this.serviceScopedSingletonAPI_2.execute();
        this.serviceScopedPrototypeAPI_1.execute();
        this.serviceScopedPrototypeAPI_2.execute();
        System.out.println(this.serviceScopedSingletonAPI_1.equals(this.serviceScopedSingletonAPI_2)
                ? "ServiceScopedSingleton is a singleton"
                : "ServiceScopedSingleton is a prototype");
        System.out.println(this.serviceScopedPrototypeAPI_1.equals(this.serviceScopedPrototypeAPI_2)
                ? "ServiceScopedPrototype is a singleton"
                : "ServiceScopedPrototype is a prototype");
        this.adapterConditionedKeptSPI.execute();
        this.adapterNamedByClassSPI.execute();
        this.adapterNamedByBeanSPI.execute();
        this.adapterPrimaryByClassSPI.execute();
        this.adapterPrimaryByBeanSPI.execute();
        this.adapterProfiledKeptSPI.execute();
        this.adapterScopedSingletonSPI_1.execute();
        this.adapterScopedSingletonSPI_2.execute();
        this.adapterScopedPrototypeSPI_1.execute();
        this.adapterScopedPrototypeSPI_2.execute();
        System.out.println(this.adapterScopedSingletonSPI_1.equals(this.adapterScopedSingletonSPI_2)
                ? "AdapterScopedSingleton is a singleton"
                : "AdapterScopedSingleton is a prototype");
        System.out.println(this.adapterScopedPrototypeSPI_1.equals(this.adapterScopedPrototypeSPI_2)
                ? "AdapterScopedPrototype is a singleton"
                : "AdapterScopedPrototype is a prototype");
        this.adapterOSSPI_specific.execute();
        this.adapterOSSPI_all.execute();
    }

    @PostConstruct
    public void afterInit1() {
        System.out.println(getClass().getSimpleName() + " post init n°1");
    }

    @PostConstruct
    public void afterInit2() {
        System.out.println(getClass().getSimpleName() + " post init n°2");
    }
}
