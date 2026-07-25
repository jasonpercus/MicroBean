package com.jasonpercus.microbean.infrastructure.helpers;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.Comparator;
import java.util.TreeSet;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.Adapter;
import com.jasonpercus.microbean.api.OS;
import com.jasonpercus.microbean.api.Service;
import com.jasonpercus.microbean.infrastructure.factory.BeanDefinition;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Tests unitaires de OperatingSystemHelper")
class OperatingSystemHelperTest {

    private final String originalOsName = System.getProperty("os.name");
    private final String originalMicroBeanOs = System.getProperty(MicroBean.PROPERTY_MICROBEAN_OS);

    @AfterEach
    void doit_restaurer_les_proprietes_systeme_apres_chaque_test() {
        restoreProperty("os.name", originalOsName);
        restoreProperty(MicroBean.PROPERTY_MICROBEAN_OS, originalMicroBeanOs);
    }

    @Test
    @DisplayName("Doit utiliser l'override d'OS configuré lorsqu'il est présent")
    void doit_utiliser_l_override_d_os_configure_lorsqu_il_est_present() {

        // Given
        System.setProperty("os.name", "SomeUnknownOS");
        MicroBean.setCurrentOS(OS.WINDOWS);

        // When
        OS currentOS = OperatingSystemHelper.getCurrentOS();
        boolean isWindowsCompatible = OperatingSystemHelper.isCompatibleWithCurrentOS(new OS[]{OS.WINDOWS});
        boolean isLinuxCompatible = OperatingSystemHelper.isCompatibleWithCurrentOS(new OS[]{OS.LINUX});

        // Then
        assertThat(currentOS).isEqualTo(OS.WINDOWS);
        assertThat(isWindowsCompatible).isTrue();
        assertThat(isLinuxCompatible).isFalse();
    }

    @Test
    @DisplayName("Doit retourner UNKNOWN quand l'OS ne peut pas être détecté")
    void doit_retourner_unknown_quand_l_os_ne_peut_pas_etre_detecte() {

        // Given
        System.setProperty("os.name", "SomeUnknownOS");
        MicroBean.clearCurrentOS();

        // When
        OS currentOS = OperatingSystemHelper.getCurrentOS();
        boolean isAllCompatible = OperatingSystemHelper.isCompatibleWithCurrentOS(new OS[]{OS.ALL});
        boolean isWindowsCompatible = OperatingSystemHelper.isCompatibleWithCurrentOS(new OS[]{OS.WINDOWS});

        // Then
        assertThat(currentOS).isEqualTo(OS.UNKNOWN);
        assertThat(isAllCompatible).isTrue();
        assertThat(isWindowsCompatible).isFalse();
    }

    @Test
    @DisplayName("Doit échouer rapidement quand l'override d'OS est invalide")
    void doit_echouer_rapidement_quand_l_override_d_os_est_invalide() {

        // Given
        System.setProperty(MicroBean.PROPERTY_MICROBEAN_OS, "solaris-like");

        // When & Then
        assertThatThrownBy(OperatingSystemHelper::getCurrentOS)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(MicroBean.PROPERTY_MICROBEAN_OS)
                .hasMessageContaining("solaris-like");
    }

    @ParameterizedTest(name = "os.name=''{0}'' -> {1}")
    @CsvSource({
            "'Windows 11', WINDOWS",
            "'Mac OS X', MAC",
            "Darwin, MAC",
            "Linux, LINUX",
            "nux, LINUX",
            "Unix, LINUX",
            "'GNU/nix', LINUX",
            "'AIX 7.2', LINUX"
    })
    @DisplayName("Doit détecter correctement l'OS à partir de os.name")
    void doit_detecter_correctement_l_os_a_partir_de_os_name(String osName, OS expectedOS) {

        // Given
        System.setProperty("os.name", osName);
        MicroBean.clearCurrentOS();

        // When
        OS currentOS = OperatingSystemHelper.getCurrentOS();

        // Then
        assertThat(currentOS).isEqualTo(expectedOS);
    }

    @Test
    @DisplayName("Doit retourner UNKNOWN quand os.name est absente")
    void doit_retourner_unknown_quand_os_name_est_absente() {

        // Given
        System.clearProperty("os.name");
        MicroBean.clearCurrentOS();

        // When
        OS currentOS = OperatingSystemHelper.getCurrentOS();

        // Then
        assertThat(currentOS).isEqualTo(OS.UNKNOWN);
    }

    @ParameterizedTest(name = "os.name=''{0}''")
    @ValueSource(strings = {"", " ", "   ", "\t"})
    @DisplayName("Doit retourner UNKNOWN quand os.name est vide ou blanche")
    void doit_retourner_unknown_quand_os_name_est_vide_ou_blanche(String osName) {

        // Given
        System.setProperty("os.name", osName);
        MicroBean.clearCurrentOS();

        // When
        OS currentOS = OperatingSystemHelper.getCurrentOS();

        // Then
        assertThat(currentOS).isEqualTo(OS.UNKNOWN);
    }

    @Test
    @DisplayName("Doit prioriser l'override sur la détection native")
    void doit_prioriser_l_override_sur_la_detection_native() {

        // Given
        System.setProperty("os.name", "Windows 11");
        MicroBean.setCurrentOS(OS.MAC);

        // When
        OS currentOS = OperatingSystemHelper.getCurrentOS();

        // Then
        assertThat(currentOS).isEqualTo(OS.MAC);
    }

    @Test
    @DisplayName("Doit accepter un override valide avec espaces et casse libre")
    void doit_accepter_un_override_valide_avec_espaces_et_casse_libre() {

        // Given
        System.setProperty("os.name", "Windows 11");
        System.setProperty(MicroBean.PROPERTY_MICROBEAN_OS, "  linux  ");

        // When
        OS currentOS = OperatingSystemHelper.getCurrentOS();

        // Then
        assertThat(currentOS).isEqualTo(OS.LINUX);
    }

    @ParameterizedTest(name = "override=''{0}''")
    @ValueSource(strings = {"", " ", "   ", "\t"})
    @DisplayName("Doit ignorer un override vide ou blanc")
    void doit_ignorer_un_override_vide_ou_blanc(String overriddenValue) {

        // Given
        System.setProperty("os.name", "Darwin");
        System.setProperty(MicroBean.PROPERTY_MICROBEAN_OS, overriddenValue);

        // When
        OS currentOS = OperatingSystemHelper.getCurrentOS();

        // Then
        assertThat(currentOS).isEqualTo(OS.MAC);
    }

    @ParameterizedTest(name = "candidat={0} -> compatible={1}")
    @CsvSource({
            "WINDOWS, true",
            "LINUX, false",
            "MAC, false",
            "ALL, true",
            "UNKNOWN, false"
    })
    @DisplayName("Doit évaluer la compatibilité pour un OS donné")
    void doit_evaluer_la_compatibilite_pour_un_os_donne(OS candidateOS, boolean expectedCompatibility) {

        // Given
        MicroBean.setCurrentOS(OS.WINDOWS);

        // When
        boolean compatible = OperatingSystemHelper.isCompatibleWithCurrentOS(new OS[]{candidateOS});

        // Then
        assertThat(compatible).isEqualTo(expectedCompatibility);
    }

    @Test
    @DisplayName("Doit évaluer la compatibilité depuis une BeanDefinition d'adapter")
    void doit_evaluer_la_compatibilite_depuis_une_bean_definition_d_adapter() {

        // Given
        Context context = createContext();
        BeanDefinition<?> beanDefinition = new BeanDefinition<>(AdapterWindowsDeTest.class, context);

        // When / Then
        MicroBean.setCurrentOS(OS.WINDOWS);
        assertThat(OperatingSystemHelper.isCompatibleWithCurrentOS(beanDefinition)).isTrue();

        MicroBean.setCurrentOS(OS.LINUX);
        assertThat(OperatingSystemHelper.isCompatibleWithCurrentOS(beanDefinition)).isFalse();
    }

    @Test
    @DisplayName("Doit considérer un service comme compatible avec tous les OS via BeanDefinition")
    void doit_considerer_un_service_comme_compatible_avec_tous_les_os_via_bean_definition() {

        // Given
        System.setProperty("os.name", "SomeUnknownOS");
        MicroBean.clearCurrentOS();
        Context context = createContext();
        BeanDefinition<?> beanDefinition = new BeanDefinition<>(ServiceSansRestrictionOsDeTest.class, context);

        // When
        boolean compatible = OperatingSystemHelper.isCompatibleWithCurrentOS(beanDefinition);

        // Then
        assertThat(beanDefinition.getOs()).containsExactly(OS.ALL);
        assertThat(compatible).isTrue();
    }

    private static Context createContext() {
        Comparator<Class<?>> classComparator = Comparator.comparing(Class::getName);
        return new Context(new TreeSet<>(classComparator), new TreeSet<>(classComparator));
    }

    private static void restoreProperty(String propertyName, String value) {
        if (value == null)
            System.clearProperty(propertyName);
        else
            System.setProperty(propertyName, value);
    }

    @Adapter(os = OS.WINDOWS)
    static class AdapterWindowsDeTest {
    }

    @Service
    static class ServiceSansRestrictionOsDeTest {

    }
}
