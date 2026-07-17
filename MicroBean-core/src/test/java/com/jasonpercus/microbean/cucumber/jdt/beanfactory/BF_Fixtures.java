package com.jasonpercus.microbean.cucumber.jdt.beanfactory;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Named;
import com.jasonpercus.microbean.api.PostConstruct;
import com.jasonpercus.microbean.api.Service;

@SuppressWarnings("all")
public final class BF_Fixtures {

    public static boolean parentCalled;
    public static boolean childCalled;
    public static boolean interfaceCalled;

    private BF_Fixtures() {
    }

    public static void reset() {
        parentCalled = false;
        childCalled = false;
        interfaceCalled = false;
    }

    public static class Dependency {
    }

    public static class NamedDependency {
    }

    public static class BeanFromMethod {

        public final Dependency dependency;

        public BeanFromMethod(Dependency dependency) {
            this.dependency = dependency;
        }
    }

    public static class ServiceWithNamed {

        public final NamedDependency dependency;

        public ServiceWithNamed(@Named("special-dependency") NamedDependency dependency) {
            this.dependency = dependency;
        }
    }

    public static class ConfigurationFactory {

        @Bean
        public BeanFromMethod createFromMethod(Dependency dependency) {
            return new BeanFromMethod(dependency);
        }
    }

    @Service
    public static class DependencyService extends Dependency {
    }

    @Service(name = "special-dependency")
    public static class NamedDependencyService extends NamedDependency {
    }

    @Service
    public static class ConstructorMaxParamService {

        public final String constructorUsed;
        public final Dependency dependency;

        public ConstructorMaxParamService() {
            this.constructorUsed = "MIN";
            this.dependency = null;
        }

        public ConstructorMaxParamService(Dependency dependency) {
            this.constructorUsed = "MAX";
            this.dependency = dependency;
        }
    }

    public interface PostConstructInterface {

        @PostConstruct
        default void initInterface() {
            BF_Fixtures.interfaceCalled = true;
        }
    }

    public static class ParentPostConstructService {

        @PostConstruct
        void initParent() {
            BF_Fixtures.parentCalled = true;
        }
    }

    @Service
    public static class MultiPostConstructService extends ParentPostConstructService implements PostConstructInterface {

        @PostConstruct
        void initChild() {
            BF_Fixtures.childCalled = true;
        }
    }

    @Service
    public static class FailingPostConstructService {

        @PostConstruct
        void initFail() {
            throw new IllegalStateException("Erreur post construct volontaire");
        }
    }

    @Service
    public static class NamedConsumerService extends ServiceWithNamed {

        public NamedConsumerService(@Named("special-dependency") NamedDependency dependency) {
            super(dependency);
        }
    }
}
