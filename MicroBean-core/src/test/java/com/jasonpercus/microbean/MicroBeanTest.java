package com.jasonpercus.microbean;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import java.util.function.Consumer;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.infrastructure.exception.MicroBeanException;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import com.jasonpercus.microbean.infrastructure.run.AppExecutor;
import com.jasonpercus.microbean.infrastructure.run.Banner;
import com.jasonpercus.microbean.infrastructure.run.Initializer;
import com.jasonpercus.microbean.infrastructure.run.Processor;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

@DisplayName("Tests unitaires de la classe MicroBean")
class MicroBeanTest {

    private final String originalDebug = System.getProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG);
    private final String originalMicroBeanOs = System.getProperty(MicroBean.PROPERTY_MICROBEAN_OS);
    private final String originalProfile = System.getProperty("app.profile");

    @AfterEach
    void doit_restaurer_les_proprietes_systeme_apres_chaque_test() {

        // When
        restoreProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG, originalDebug);
        restoreProperty(MicroBean.PROPERTY_MICROBEAN_OS, originalMicroBeanOs);
        restoreProperty("app.profile", originalProfile);

        // Then
        assertThat(System.getProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG)).isEqualTo(originalDebug);
        assertThat(System.getProperty(MicroBean.PROPERTY_MICROBEAN_OS)).isEqualTo(originalMicroBeanOs);
        assertThat(System.getProperty("app.profile")).isEqualTo(originalProfile);

        MicroBean.context = null;
    }

    @Test
    @DisplayName("Doit lever une exception si getContext() est appelé avant MicroBean.run()")
    void doit_lever_une_exception_si_getContext_est_appele_avant_run() {

        // When
        Throwable throwable = ThrowableAssert.catchThrowable(MicroBean::getContext);

        // Then
        assertThat(throwable)
                .isInstanceOf(MicroBeanException.class)
                .hasMessageContaining("Context is not initialized yet");
    }

    @Test
    @DisplayName("Doit lever une exception si run sans consumer reçoit une classe non annotée")
    void doit_lever_une_exception_si_run_sans_consumer_recoit_une_classe_non_annotee() {

        // Given
        String[] args = new String[0];

        // When
        Throwable throwable = ThrowableAssert.catchThrowable(() -> MicroBean.run(NotAnnotatedApp.class, args));

        // Then
        assertThat(throwable)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Missing @MicroBeanApplication")
                .hasMessageContaining("NotAnnotatedApp");
    }

    @Test
    @DisplayName("Doit lever une exception si run avec consumer reçoit une classe non annotée")
    void doit_lever_une_exception_si_run_avec_consumer_recoit_une_classe_non_annotee() {

        // Given
        String[] args = new String[0];

        // When
        Throwable throwable = ThrowableAssert.catchThrowable(
                () -> MicroBean.run(NotAnnotatedApp.class, context -> {
                }, args)
        );

        // Then
        assertThat(throwable)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Missing @MicroBeanApplication")
                .hasMessageContaining("NotAnnotatedApp");
    }

    @Test
    @DisplayName("Doit orchestrer le run nominal sans consumer et retourner le contexte")
    void doit_orchestrer_le_run_nominal_sans_consumer_et_retourner_le_contexte() {

        // Given
        String[] args = new String[]{"arg1"};
        Context context = mock(Context.class);
        Initializer initializer = mock(Initializer.class);

        @SuppressWarnings("unchecked")
        Class<? extends ApplicationEntryPoint>[] entryPoints = new Class[]{FakeEntryPoint.class};

        MockedStatic<Banner> bannerMock = mockStatic(Banner.class);
        MockedStatic<Initializer> initializerMock = mockStatic(Initializer.class);
        MockedStatic<Processor> processorMock = mockStatic(Processor.class);
        MockedStatic<AppExecutor> appExecutorMock = mockStatic(AppExecutor.class);

        initializerMock.when(() -> Initializer.init(NotAnnotatedApp.class, args, entryPoints)).thenReturn(initializer);
        when(initializer.getContext()).thenReturn(context);
        when(initializer.getClasses()).thenReturn(java.util.Set.of());

        // When
        Context result = MicroBean.run(NotAnnotatedApp.class, args, entryPoints);
        Context resultByGetContext = MicroBean.getContext();

        // Then
        assertThat(result).isSameAs(context).isSameAs(resultByGetContext);
        bannerMock.verify(() -> Banner.show(NotAnnotatedApp.class));
        initializerMock.verify(() -> Initializer.init(NotAnnotatedApp.class, args, entryPoints));
        processorMock.verify(() -> Processor.execute(any(), eq(context), eq(args)));
        appExecutorMock.verify(() -> AppExecutor.loadAndExecuteEntryPointServices(null, args, entryPoints, context));

        bannerMock.close();
        initializerMock.close();
        processorMock.close();
        appExecutorMock.close();
    }

    @Test
    @DisplayName("Doit orchestrer le run nominal avec consumer et retourner le contexte")
    void doit_orchestrer_le_run_nominal_avec_consumer_et_retourner_le_contexte() {

        // Given
        String[] args = new String[]{"arg1"};
        Context context = mock(Context.class);
        Initializer initializer = mock(Initializer.class);
        Consumer<Context> consumer = c -> {
        };

        @SuppressWarnings("unchecked")
        Class<? extends ApplicationEntryPoint>[] entryPoints = new Class[]{FakeEntryPoint.class};

        MockedStatic<Banner> bannerMock = mockStatic(Banner.class);
        MockedStatic<Initializer> initializerMock = mockStatic(Initializer.class);
        MockedStatic<Processor> processorMock = mockStatic(Processor.class);
        MockedStatic<AppExecutor> appExecutorMock = mockStatic(AppExecutor.class);

        initializerMock.when(() -> Initializer.init(NotAnnotatedApp.class, args, entryPoints)).thenReturn(initializer);
        when(initializer.getContext()).thenReturn(context);
        when(initializer.getClasses()).thenReturn(java.util.Set.of());

        // When
        Context result = MicroBean.run(NotAnnotatedApp.class, consumer, args, entryPoints);

        // Then
        assertThat(result).isSameAs(context);
        bannerMock.verify(() -> Banner.show(NotAnnotatedApp.class));
        initializerMock.verify(() -> Initializer.init(NotAnnotatedApp.class, args, entryPoints));
        processorMock.verify(() -> Processor.execute(any(), eq(context), eq(args)));
        appExecutorMock.verify(() -> AppExecutor.loadAndExecuteEntryPointServices(consumer, args, entryPoints, context));

        bannerMock.close();
        initializerMock.close();
        processorMock.close();
        appExecutorMock.close();
    }

    @Test
    @DisplayName("Doit activer le mode debug quand la valeur true est fournie")
    void doit_activer_le_mode_debug_quand_la_valeur_true_est_fournie() {

        // Given
        boolean expected = true;

        // When
        MicroBean.setEnabledDebugMicroBean(expected);

        // Then
        assertThat(MicroBean.isEnabledDebugMicroBean()).isTrue();
        assertThat(System.getProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG)).isEqualTo("true");
    }

    @Test
    @DisplayName("Doit désactiver le mode debug quand la valeur false est fournie")
    void doit_desactiver_le_mode_debug_quand_la_valeur_false_est_fournie() {

        // Given
        MicroBean.setEnabledDebugMicroBean(true);

        // When
        MicroBean.setEnabledDebugMicroBean(false);

        // Then
        assertThat(MicroBean.isEnabledDebugMicroBean()).isFalse();
        assertThat(System.getProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG)).isEqualTo("false");
    }

    @Test
    @DisplayName("Doit forcer l'OS courant quand une valeur est fournie")
    void doit_forcer_l_os_courant_quand_une_valeur_est_fournie() {

        // Given
        String expected = "LINUX";

        // When
        MicroBean.setCurrentOS(com.jasonpercus.microbean.api.OS.LINUX);

        // Then
        assertThat(System.getProperty(MicroBean.PROPERTY_MICROBEAN_OS)).isEqualTo(expected);
    }

    @Test
    @DisplayName("Doit supprimer l'OS forcé quand setCurrentOS reçoit null")
    void doit_supprimer_l_os_force_quand_setcurrentos_recoit_null() {

        // Given
        MicroBean.setCurrentOS(com.jasonpercus.microbean.api.OS.WINDOWS);

        // When
        MicroBean.setCurrentOS(null);

        // Then
        assertThat(System.getProperty(MicroBean.PROPERTY_MICROBEAN_OS)).isNull();
    }

    @Test
    @DisplayName("Doit supprimer l'OS forcé quand clearCurrentOS est appelé")
    void doit_supprimer_l_os_force_quand_clearcurrentos_est_appele() {

        // Given
        MicroBean.setCurrentOS(com.jasonpercus.microbean.api.OS.MAC);

        // When
        MicroBean.clearCurrentOS();

        // Then
        assertThat(System.getProperty(MicroBean.PROPERTY_MICROBEAN_OS)).isNull();
    }

    @Test
    @DisplayName("Doit retourner le profil actif défini dans les propriétés système")
    void doit_retourner_le_profil_actif_defini_dans_les_proprietes_systeme() {

        // Given
        String expected = "test";

        // When
        System.setProperty("app.profile", expected);
        String result = MicroBean.getActiveProfile();

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Doit retourner null quand le profil actif n'est pas défini")
    void doit_retourner_null_quand_le_profil_actif_n_est_pas_defini() {

        // Given
        System.clearProperty("app.profile");

        // When
        String result = MicroBean.getActiveProfile();

        // Then
        assertThat(result).isNull();
    }

    private static void restoreProperty(String propertyName, String value) {

        if (value == null)
            System.clearProperty(propertyName);
        else
            System.setProperty(propertyName, value);
    }

    static class NotAnnotatedApp {

    }

    static class FakeEntryPoint implements ApplicationEntryPoint {

        @Override
        public void main(String[] args) {
        }
    }
}
