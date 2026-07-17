package com.jasonpercus.microbean.infrastructure.helpers;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static com.jasonpercus.microbean.api.LifecycleEntryPoint.ONE_SHOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.ConditionEvaluator;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.MicroBeanApplication;
import com.jasonpercus.microbean.api.Named;
import com.jasonpercus.microbean.api.PostConstruct;
import com.jasonpercus.microbean.api.Primary;
import com.jasonpercus.microbean.api.Profile;
import com.jasonpercus.microbean.api.Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires de AnnotationHelper")
class AnnotationHelperTest {

    @Test
    @DisplayName("Doit détecter les annotations de composant et de bean")
    void doit_detecter_les_annotations_de_composant_et_de_bean() throws Exception {

        // Given
        Method beanMethod = methodOf(MethodFixtures.class, "beanMethod");

        // When
        boolean isPrimary = AnnotationHelper.isAnnotatedPrimary(PrimaryFixture.class);
        boolean isBean = AnnotationHelper.isAnnotatedBean(beanMethod);
        boolean isAdapter = AnnotationHelper.isAnnotatedAdapter(AdapterFixture.class);
        boolean isService = AnnotationHelper.isAnnotatedService(ServiceFixture.class);
        boolean isEntryPoint = AnnotationHelper.isAnnotatedEntryPointService(EntryPointFixture.class);

        boolean isPrimaryOnPlain = AnnotationHelper.isAnnotatedPrimary(PlainFixture.class);
        boolean isBeanOnNotBean = AnnotationHelper.isAnnotatedBean(methodOf(MethodFixtures.class, "notBeanMethod"));
        boolean isAdapterOnPlain = AnnotationHelper.isAnnotatedAdapter(PlainFixture.class);
        boolean isServiceOnPlain = AnnotationHelper.isAnnotatedService(PlainFixture.class);
        boolean isEntryPointOnPlain = AnnotationHelper.isAnnotatedEntryPointService(PlainEntryPointFixture.class);

        // Then
        assertThat(isPrimary).isTrue();
        assertThat(isBean).isTrue();
        assertThat(isAdapter).isTrue();
        assertThat(isService).isTrue();
        assertThat(isEntryPoint).isTrue();

        assertThat(isPrimaryOnPlain).isFalse();
        assertThat(isBeanOnNotBean).isFalse();
        assertThat(isAdapterOnPlain).isFalse();
        assertThat(isServiceOnPlain).isFalse();
        assertThat(isEntryPointOnPlain).isFalse();
    }

    @Test
    @DisplayName("Doit détecter @Named et @PostConstruct")
    void doit_detecter_named_et_postconstruct() throws Exception {

        // Given
        Method namedMethod = methodOf(MethodFixtures.class, "namedConsumer", String.class, String.class);
        Parameter namedParameter = namedMethod.getParameters()[0];
        Parameter plainParameter = namedMethod.getParameters()[1];

        // When
        boolean isNamed = AnnotationHelper.isAnnotatedNamed(namedParameter);
        boolean isNamedOnPlain = AnnotationHelper.isAnnotatedNamed(plainParameter);
        String namedValue = AnnotationHelper.getNamedValue(namedParameter);
        boolean isPostConstruct = AnnotationHelper.isAnnotatedPostConstruct(methodOf(MethodFixtures.class, "postConstructMethod"));
        boolean isPostConstructOnPublic = AnnotationHelper.isAnnotatedPostConstruct(methodOf(MethodFixtures.class, "publicMethod"));

        // Then
        assertThat(isNamed).isTrue();
        assertThat(isNamedOnPlain).isFalse();
        assertThat(namedValue).isEqualTo("alpha");
        assertThatThrownBy(() -> AnnotationHelper.getNamedValue(plainParameter))
                .isInstanceOf(NullPointerException.class);
        assertThat(isPostConstruct).isTrue();
        assertThat(isPostConstructOnPublic).isFalse();
    }

    @Test
    @DisplayName("Doit détecter @Profile, @Condition et leurs négations")
    void doit_detecter_profile_condition_et_leurs_negations() {

        // When
        boolean isProfile = AnnotationHelper.isAnnotatedProfile(ProfiledFixture.class);
        boolean isCondition = AnnotationHelper.isAnnotatedCondition(ConditionedFixture.class);

        boolean isNotProfileOnPlain = AnnotationHelper.isNotAnnotatedProfile(PlainFixture.class);
        boolean isNotConditionOnPlain = AnnotationHelper.isNotAnnotatedCondition(PlainFixture.class);

        boolean isNotProfileOnProfiled = AnnotationHelper.isNotAnnotatedProfile(ProfiledFixture.class);
        boolean isNotConditionOnConditioned = AnnotationHelper.isNotAnnotatedCondition(ConditionedFixture.class);

        // Then
        assertThat(isProfile).isTrue();
        assertThat(isCondition).isTrue();
        assertThat(isNotProfileOnPlain).isTrue();
        assertThat(isNotConditionOnPlain).isTrue();
        assertThat(isNotProfileOnProfiled).isFalse();
        assertThat(isNotConditionOnConditioned).isFalse();
    }

    @Test
    @DisplayName("Doit détecter les méthodes bean et la visibilité")
    void doit_detecter_les_methodes_bean_et_la_visibilite() throws Exception {

        // Given
        Method beanMethod = methodOf(MethodFixtures.class, "beanMethod");
        Method notBeanMethod = methodOf(MethodFixtures.class, "notBeanMethod");
        Method publicMethod = methodOf(MethodFixtures.class, "publicMethod");
        Method privateMethod = methodOf(MethodFixtures.class, "privateMethod");

        // When
        boolean isNotBeanOnBeanMethod = AnnotationHelper.isNotBeanMethod(beanMethod);
        boolean isNotBeanOnNotBeanMethod = AnnotationHelper.isNotBeanMethod(notBeanMethod);

        boolean isNotPublicOnPublicMethod = AnnotationHelper.isNotPublicMethod(publicMethod);
        boolean isNotPublicOnPrivateMethod = AnnotationHelper.isNotPublicMethod(privateMethod);

        // Then
        assertThat(isNotBeanOnBeanMethod).isFalse();
        assertThat(isNotBeanOnNotBeanMethod).isTrue();
        assertThat(isNotPublicOnPublicMethod).isFalse();
        assertThat(isNotPublicOnPrivateMethod).isTrue();
    }

    @Test
    @DisplayName("Doit détecter les classes virtuellement bean et composant")
    void doit_detecter_les_classes_virtuellement_bean_et_composant() {

        // When
        boolean isNotVirtualOnPlain = AnnotationHelper.isNotVirtuallyAnnotatedBean(PlainFixture.class);
        boolean isNotVirtualOnService = AnnotationHelper.isNotVirtuallyAnnotatedBean(ServiceFixture.class);
        boolean isNotVirtualOnAdapter = AnnotationHelper.isNotVirtuallyAnnotatedBean(AdapterFixture.class);
        boolean isNotVirtualOnEntryPoint = AnnotationHelper.isNotVirtuallyAnnotatedBean(EntryPointFixture.class);

        boolean isNotComponentOnPlain = AnnotationHelper.isNotComponentClass(PlainFixture.class);
        boolean isNotComponentOnService = AnnotationHelper.isNotComponentClass(ServiceFixture.class);
        boolean isNotComponentOnAdapter = AnnotationHelper.isNotComponentClass(AdapterFixture.class);
        boolean isNotComponentOnEntryPoint = AnnotationHelper.isNotComponentClass(EntryPointFixture.class);

        // Then
        assertThat(isNotVirtualOnPlain).isTrue();
        assertThat(isNotVirtualOnService).isFalse();
        assertThat(isNotVirtualOnAdapter).isFalse();
        assertThat(isNotVirtualOnEntryPoint).isFalse();

        assertThat(isNotComponentOnPlain).isTrue();
        assertThat(isNotComponentOnService).isFalse();
        assertThat(isNotComponentOnAdapter).isFalse();
        assertThat(isNotComponentOnEntryPoint).isFalse();
    }

    @Test
    @DisplayName("Doit détecter les négations des annotations de composant et d'application")
    void doit_detecter_les_negations_des_annotations_de_composant_et_d_application() {

        // When
        boolean isNotServiceOnPlain = AnnotationHelper.isNotAnnotatedService(PlainFixture.class);
        boolean isNotServiceOnService = AnnotationHelper.isNotAnnotatedService(ServiceFixture.class);

        boolean isNotAdapterOnPlain = AnnotationHelper.isNotAnnotatedAdapter(PlainFixture.class);
        boolean isNotAdapterOnAdapter = AnnotationHelper.isNotAnnotatedAdapter(AdapterFixture.class);

        boolean isNotEntryPointOnPlain = AnnotationHelper.isNotAnnotatedEntryPointService(PlainEntryPointFixture.class);
        boolean isNotEntryPointOnEntryPoint = AnnotationHelper.isNotAnnotatedEntryPointService(EntryPointFixture.class);

        boolean isNotWithEntryPointOnPlain = AnnotationHelper.isNotAnnotatedWithEntryPointService(PlainEntryPointFixture.class);
        boolean isNotWithEntryPointOnEntryPoint = AnnotationHelper.isNotAnnotatedWithEntryPointService(EntryPointFixture.class);

        boolean isNotWithMicroBeanAppOnAnnotated = AnnotationHelper.isNotAnnotatedWithMicroBeanApplication(MicroBeanApplicationFixture.class);
        boolean isNotWithMicroBeanAppOnPlain = AnnotationHelper.isNotAnnotatedWithMicroBeanApplication(PlainFixture.class);

        // Then
        assertThat(isNotServiceOnPlain).isTrue();
        assertThat(isNotServiceOnService).isFalse();

        assertThat(isNotAdapterOnPlain).isTrue();
        assertThat(isNotAdapterOnAdapter).isFalse();

        assertThat(isNotEntryPointOnPlain).isTrue();
        assertThat(isNotEntryPointOnEntryPoint).isFalse();

        assertThat(isNotWithEntryPointOnPlain).isTrue();
        assertThat(isNotWithEntryPointOnEntryPoint).isFalse();

        assertThat(isNotWithMicroBeanAppOnAnnotated).isFalse();
        assertThat(isNotWithMicroBeanAppOnPlain).isTrue();
    }

    @Test
    @DisplayName("Doit détecter @Service et @Adapter via méta-annotations")
    void doit_detecter_service_et_adapter_via_meta_annotations() {

        // When
        boolean isServiceFromMeta = AnnotationHelper.isAnnotatedService(MetaAnnotatedServiceFixture.class);
        boolean isAdapterFromMeta = AnnotationHelper.isAnnotatedAdapter(MetaAnnotatedAdapterFixture.class);

        boolean isServiceFromNonMatchingAnnotation = AnnotationHelper.isAnnotatedService(UnrelatedAnnotatedFixture.class);
        boolean isAdapterFromNonMatchingAnnotation = AnnotationHelper.isAnnotatedAdapter(UnrelatedAnnotatedFixture.class);

        boolean isNotServiceFromMeta = AnnotationHelper.isNotAnnotatedService(MetaAnnotatedServiceFixture.class);
        boolean isNotAdapterFromMeta = AnnotationHelper.isNotAnnotatedAdapter(MetaAnnotatedAdapterFixture.class);

        // Then
        assertThat(isServiceFromMeta).isTrue();
        assertThat(isAdapterFromMeta).isTrue();
        assertThat(isServiceFromNonMatchingAnnotation).isFalse();
        assertThat(isAdapterFromNonMatchingAnnotation).isFalse();
        assertThat(isNotServiceFromMeta).isFalse();
        assertThat(isNotAdapterFromMeta).isFalse();
    }

    private static Method methodOf(Class<?> owner, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        return owner.getDeclaredMethod(name, parameterTypes);
    }

    @Primary
    static class PrimaryFixture {
    }

    @Service
    static class ServiceFixture {
    }

    @Adapter
    static class AdapterFixture {
    }

    @Profile("dev")
    static class ProfiledFixture {
    }

    @Condition(AlwaysTrueConditionEvaluator.class)
    static class ConditionedFixture {
    }

    @MicroBeanApplication
    static class MicroBeanApplicationFixture {
    }

    static class PlainFixture {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Service
    @interface CustomService {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Adapter
    @interface CustomAdapter {
    }

    @CustomService
    static class MetaAnnotatedServiceFixture {
    }

    @CustomAdapter
    static class MetaAnnotatedAdapterFixture {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface UnrelatedAnnotation {
    }

    @UnrelatedAnnotation
    static class UnrelatedAnnotatedFixture {
    }

    @EntryPointService(lifecycle = ONE_SHOT)
    static class EntryPointFixture implements ApplicationEntryPoint {

        @Override
        public void main(String[] args) {
        }
    }

    static class PlainEntryPointFixture implements ApplicationEntryPoint {

        @Override
        public void main(String[] args) {
        }
    }

    @SuppressWarnings("unused")
    static class MethodFixtures {

        @Bean
        public void beanMethod() {
        }

        public void notBeanMethod() {
        }

        @PostConstruct
        public void postConstructMethod() {
        }

        public void publicMethod() {
        }

        @SuppressWarnings("unused")
        private void privateMethod() {
        }

        public void namedConsumer(@Named("alpha") String namedValue, String plainValue) {
        }
    }

    public static class AlwaysTrueConditionEvaluator implements ConditionEvaluator {

        @Override
        public boolean validate(String[] args) {
            return true;
        }
    }
}
