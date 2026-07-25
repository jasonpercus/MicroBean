package com.jasonpercus.microbean.infrastructure.run;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import com.jasonpercus.microbean.MicroBean;
import com.jasonpercus.microbean.api.OS;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import com.jasonpercus.microbean.infrastructure.run.processor.P_AdapterWindows;
import com.jasonpercus.microbean.infrastructure.run.processor.P_BeanConditionMethod;
import com.jasonpercus.microbean.infrastructure.run.processor.P_BeanConditionMethodNegate;
import com.jasonpercus.microbean.infrastructure.run.processor.P_BeanDeConfiguration;
import com.jasonpercus.microbean.infrastructure.run.processor.P_BeanIgnore;
import com.jasonpercus.microbean.infrastructure.run.processor.P_BeanProfile;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ConfigurationNominal;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ConfigurationWithBeanConditionNegate;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ConfigurationWithBeanConditionValid;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ConfigurationWithBeanPrimitiveVoid;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ConfigurationWithBeanPrivate;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ConfigurationWithBeanProfile;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ConfigurationWithBeanVoid;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ConfigurationWithMethodNotBean;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ServiceConditionError;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ServiceConditionFalse;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ServiceConditionTrue;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ServiceNominal;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ServiceProfileInvalid;
import com.jasonpercus.microbean.infrastructure.run.processor.P_ServiceProfileValid;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires de la classe Processor")
class ProcessorTest {

    private final String originalProfile = System.getProperty("app.profile");
    private final String originalMicroBeanOs = System.getProperty(MicroBean.PROPERTY_MICROBEAN_OS);

    @AfterEach
    @SuppressWarnings("all")
    void doit_restaurer_les_proprietes_systeme_apres_chaque_test() {

        // Given
        String profileAttendu = originalProfile;
        String osAttendu = originalMicroBeanOs;

        // When
        restoreProperty("app.profile", originalProfile);
        restoreProperty(MicroBean.PROPERTY_MICROBEAN_OS, originalMicroBeanOs);

        // Then
        assertThat(System.getProperty("app.profile")).isEqualTo(profileAttendu);
        assertThat(System.getProperty(MicroBean.PROPERTY_MICROBEAN_OS)).isEqualTo(osAttendu);
    }

    @Test
    @DisplayName("Doit enregistrer un bean de configuration et un service quand les règles sont satisfaites")
    void doit_enregistrer_un_bean_de_configuration_et_un_service_quand_les_regles_sont_satisfaites() {

        // Given
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ConfigurationNominal.class, P_ServiceNominal.class);

        // When
        Processor.execute(classes, context, new String[0]);
        Object beanConfiguration = context.getBean(P_BeanDeConfiguration.class);
        Object service = context.getBean(P_ServiceNominal.class);

        // Then
        assertThat(beanConfiguration).isInstanceOf(P_BeanDeConfiguration.class);
        assertThat(service).isInstanceOf(P_ServiceNominal.class);
    }

    @Test
    @DisplayName("Doit ignorer les méthodes non annotées @Bean dans une configuration")
    void doit_ignorer_les_methodes_non_annotees_bean_dans_une_configuration() {

        // Given
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ConfigurationWithMethodNotBean.class);

        // When
        Processor.execute(classes, context, new String[0]);

        // Then
        assertThat((Object) context.getBean(P_BeanDeConfiguration.class)).isInstanceOf(P_BeanDeConfiguration.class);
        assertThatThrownBy(() -> context.getBean(P_BeanIgnore.class)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Doit échouer si une méthode @Bean n'est pas publique")
    void doit_echouer_si_une_methode_bean_n_est_pas_publique() {

        // Given
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ConfigurationWithBeanPrivate.class);

        // When
        Throwable throwable = org.assertj.core.api.ThrowableAssert.catchThrowable(
                () -> Processor.execute(classes, context, new String[0])
        );

        // Then
        assertThat(throwable)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to process configuration")
                .hasCauseInstanceOf(RuntimeException.class);
        assertThat(throwable.getCause()).hasMessageContaining("method must be public");
    }

    @Test
    @DisplayName("Doit échouer si une méthode @Bean retourne void ou Void")
    void doit_echouer_si_une_methode_bean_retourne_void() {

        // Given
        Context context = createContext();
        Set<Class<?>> classesWithPrimitiveVoid = Set.of(P_ConfigurationWithBeanPrimitiveVoid.class);
        Set<Class<?>> classesWithVoid = Set.of(P_ConfigurationWithBeanVoid.class);

        // When
        Throwable throwableForPrimitiveVoid = org.assertj.core.api.ThrowableAssert.catchThrowable(
                () -> Processor.execute(classesWithPrimitiveVoid, context, new String[0])
        );
        Throwable throwableForVoid = org.assertj.core.api.ThrowableAssert.catchThrowable(
                () -> Processor.execute(classesWithVoid, context, new String[0])
        );

        // Then
        assertThat(throwableForPrimitiveVoid)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to process configuration")
                .hasCauseInstanceOf(RuntimeException.class);
        assertThat(throwableForVoid)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to process configuration")
                .hasCauseInstanceOf(RuntimeException.class);
        assertThat(throwableForPrimitiveVoid.getCause()).hasMessageContaining("must have a return type");
        assertThat(throwableForVoid.getCause()).hasMessageContaining("must have a return type");
    }

    @Test
    @DisplayName("Doit ignorer un bean de configuration quand le profil de la méthode est invalide")
    void doit_ignorer_un_bean_de_configuration_quand_le_profil_de_la_methode_est_invalide() {

        // Given
        System.setProperty("app.profile", "prod");
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ConfigurationWithBeanProfile.class);

        // When
        Processor.execute(classes, context, new String[0]);

        // Then
        assertThatThrownBy(() -> context.getBean(P_BeanProfile.class)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Doit enregistrer un bean de configuration quand le profil de la méthode est valide")
    void doit_enregistrer_un_bean_de_configuration_quand_le_profil_de_la_methode_est_valide() {

        // Given
        System.setProperty("app.profile", "dev");
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ConfigurationWithBeanProfile.class);

        // When
        Processor.execute(classes, context, new String[0]);
        Object bean = context.getBean(P_BeanProfile.class);

        // Then
        assertThat(bean).isInstanceOf(P_BeanProfile.class);
    }

    @Test
    @DisplayName("Doit ignorer un service quand le profil de la classe est invalide")
    void doit_ignorer_un_service_quand_le_profil_de_la_classe_est_invalide() {

        // Given
        System.setProperty("app.profile", "prod");
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ServiceProfileInvalid.class);

        // When
        Processor.execute(classes, context, new String[0]);

        // Then
        assertThatThrownBy(() -> context.getBean(P_ServiceProfileInvalid.class)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Doit enregistrer un service quand le profil de la classe est valide")
    void doit_enregistrer_un_service_quand_le_profil_de_la_classe_est_valide() {

        // Given
        System.setProperty("app.profile", "dev");
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ServiceProfileValid.class);

        // When
        Processor.execute(classes, context, new String[0]);
        Object service = context.getBean(P_ServiceProfileValid.class);

        // Then
        assertThat(service).isInstanceOf(P_ServiceProfileValid.class);
    }

    @Test
    @DisplayName("Doit ignorer un adaptateur quand l'OS est incompatible")
    void doit_ignorer_un_adaptateur_quand_l_os_est_incompatible() {

        // Given
        System.setProperty(MicroBean.PROPERTY_MICROBEAN_OS, OS.LINUX.name());
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_AdapterWindows.class);

        // When
        Processor.execute(classes, context, new String[0]);

        // Then
        assertThatThrownBy(() -> context.getBean(P_AdapterWindows.class)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Doit enregistrer un adaptateur quand l'OS est compatible")
    void doit_enregistrer_un_adaptateur_quand_l_os_est_compatible() {

        // Given
        System.setProperty(MicroBean.PROPERTY_MICROBEAN_OS, OS.WINDOWS.name());
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_AdapterWindows.class);

        // When
        Processor.execute(classes, context, new String[0]);
        Object adapter = context.getBean(P_AdapterWindows.class);

        // Then
        assertThat(adapter).isInstanceOf(P_AdapterWindows.class);
    }

    @Test
    @DisplayName("Doit enregistrer un bean de configuration quand la méthode est annotée avec une condition valide")
    void doit_enregistrer_un_bean_de_configuration_quand_la_methode_est_annotee_avec_une_condition_valide() {

        // Given
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ConfigurationWithBeanConditionValid.class);

        // When
        Processor.execute(classes, context, new String[0]);
        Object bean = context.getBean(P_BeanConditionMethod.class);

        // Then
        assertThat(bean).isInstanceOf(P_BeanConditionMethod.class);
    }

    @Test
    @DisplayName("Doit ignorer un bean de configuration quand la méthode utilise une condition négatée valide")
    void doit_ignorer_un_bean_de_configuration_quand_la_methode_utilise_une_condition_negatee_valide() {

        // Given
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ConfigurationWithBeanConditionNegate.class);

        // When
        Processor.execute(classes, context, new String[0]);

        // Then
        assertThatThrownBy(() -> context.getBean(P_BeanConditionMethodNegate.class)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Doit ignorer un service quand la condition demande un skip")
    void doit_ignorer_un_service_quand_la_condition_demande_un_skip() {

        // Given
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ServiceConditionFalse.class);

        // When
        Processor.execute(classes, context, new String[0]);

        // Then
        assertThatThrownBy(() -> context.getBean(P_ServiceConditionFalse.class)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Doit enregistrer un service quand la condition est satisfaite selon la logique du Processor")
    void doit_enregistrer_un_service_quand_la_condition_est_satisfaite_selon_la_logique_du_processor() {

        // Given
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ServiceConditionTrue.class);

        // When
        Processor.execute(classes, context, new String[0]);
        Object service = context.getBean(P_ServiceConditionTrue.class);

        // Then
        assertThat(service).isInstanceOf(P_ServiceConditionTrue.class);
    }

    @Test
    @DisplayName("Doit échouer quand l'évaluation d'une condition lève une exception")
    void doit_echouer_quand_l_evaluation_d_une_condition_leve_une_exception() {

        // Given
        Context context = createContext();
        Set<Class<?>> classes = Set.of(P_ServiceConditionError.class);

        // When
        Throwable throwable = org.assertj.core.api.ThrowableAssert.catchThrowable(
                () -> Processor.execute(classes, context, new String[0])
        );

        // Then
        assertThat(throwable)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to evaluate condition")
                .hasCauseInstanceOf(RuntimeException.class);
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
}
