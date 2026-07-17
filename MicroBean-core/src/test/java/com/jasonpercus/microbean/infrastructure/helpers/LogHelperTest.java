package com.jasonpercus.microbean.infrastructure.helpers;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import com.jasonpercus.microbean.MicroBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisplayName("Tests unitaires de LogHelper")
class LogHelperTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final String originalDebug = System.getProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG);

    @AfterEach
    void doit_restaurer_la_sortie_standard_et_erreur_et_la_propriete_debug() {

        // When
        System.setOut(originalOut);
        System.setErr(originalErr);
        restorePropertyMicroBeanDebug(originalDebug);

        // Then
        assertThat(System.out).isSameAs(originalOut);
        assertThat(System.err).isSameAs(originalErr);
        assertThat(System.getProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG)).isEqualTo(originalDebug);
    }

    @Test
    @DisplayName("Doit afficher un message debug quand le mode debug est activé")
    void doit_afficher_un_message_debug_quand_le_mode_debug_est_active() {

        // Given
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        MicroBean.setEnabledDebugMicroBean(true);

        // When
        LogHelper.debug("debug %s", "ok");
        String output = buffer.toString(StandardCharsets.UTF_8);

        // Then
        if (isLogActive())
            assertThat(output).isEmpty();
        else
            assertThat(output).isEqualTo("debug ok" + System.lineSeparator());
    }

    @Test
    @DisplayName("Doit ne rien afficher en debug quand le mode debug est désactivé")
    void doit_ne_rien_afficher_en_debug_quand_le_mode_debug_est_desactive() {

        // Given
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        MicroBean.setEnabledDebugMicroBean(false);

        // When
        LogHelper.debug("debug %s", "ko");
        String output = buffer.toString(StandardCharsets.UTF_8);

        // Then
        assertThat(output).isEmpty();
    }

    @Test
    @DisplayName("Doit toujours afficher un message trace")
    void doit_toujours_afficher_un_message_trace() {

        // Given
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        MicroBean.setEnabledDebugMicroBean(false);

        // When
        LogHelper.trace("trace %s", "ok");
        String output = buffer.toString(StandardCharsets.UTF_8);

        // Then
        if (isLogActive())
            assertThat(output).isEmpty();
        else
            assertThat(output).isEqualTo("trace ok" + System.lineSeparator());
    }

    @Test
    @DisplayName("Doit toujours afficher un message info")
    void doit_toujours_afficher_un_message_info() {

        // Given
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        MicroBean.setEnabledDebugMicroBean(false);

        // When
        LogHelper.info("info %s", "ok");
        String output = buffer.toString(StandardCharsets.UTF_8);

        // Then
        if (isLogActive())
            assertThat(output).isEmpty();
        else
            assertThat(output).isEqualTo("info ok" + System.lineSeparator());
    }

    @Test
    @DisplayName("Doit toujours afficher un message warn")
    void doit_toujours_afficher_un_message_warn() {

        // Given
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        MicroBean.setEnabledDebugMicroBean(false);

        // When
        LogHelper.warn("warn %s", "ok");
        String output = buffer.toString(StandardCharsets.UTF_8);

        // Then
        if (isLogActive())
            assertThat(output).isEmpty();
        else
            assertThat(output).isEqualTo("warn ok" + System.lineSeparator());
    }

    @Test
    @DisplayName("Doit afficher une banniere")
    void doit_afficher_une_banniere() {

        // Given
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        String banner = "=== DEMARRAGE ===";

        // When
        LogHelper.banner(banner);
        String output = buffer.toString(StandardCharsets.UTF_8);

        // Then
        if (isLogActive())
            assertThat(output).isEmpty();
        else
            assertThat(output).isEqualTo(banner + System.lineSeparator());
    }

    @Test
    @DisplayName("Doit afficher un message error sans throwable")
    void doit_afficher_un_message_error_sans_throwable() {

        // Given
        ByteArrayOutputStream errorBuffer = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorBuffer, true, StandardCharsets.UTF_8));

        // When
        LogHelper.error("erreur simple", null);
        String errorOutput = errorBuffer.toString(StandardCharsets.UTF_8);

        // Then
        if (isLogActive())
            assertThat(errorOutput).isEmpty();
        else
            assertThat(errorOutput).isEqualTo("erreur simple" + System.lineSeparator());
    }

    @Test
    @DisplayName("Doit afficher la stacktrace en error avec throwable")
    void doit_afficher_la_stacktrace_en_error_avec_throwable() {

        // Given
        ByteArrayOutputStream errorBuffer = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errorBuffer, true, StandardCharsets.UTF_8));
        IllegalStateException throwable = new IllegalStateException("boom");

        // When
        LogHelper.error("erreur throwable", throwable);
        String errorOutput = errorBuffer.toString(StandardCharsets.UTF_8);

        // Then
        if (isLogActive())
            assertThat(errorOutput).isEmpty();
        else {
            assertThat(errorOutput).contains("java.lang.IllegalStateException: boom");
            assertThat(errorOutput).contains("doit_afficher_la_stacktrace_en_error_avec_throwable");
        }
    }

    @Test
    @DisplayName("Doit logger en debug quand active=true et debug activé")
    void doit_logger_en_debug_quand_active_true_et_debug_active() throws Exception {

        // Given
        Logger loggerMock = mock(Logger.class);
        MicroBean.setEnabledDebugMicroBean(true);

        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(() -> LoggerFactory.getLogger("MicroBean")).thenReturn(loggerMock);
            IsolatedLogHelperHarness harness = IsolatedLogHelperHarness.load();

            // When
            harness.debug("debug %s", "ok");

            // Then
            verify(loggerMock).debug("debug ok");
        }
    }

    @Test
    @DisplayName("Ne doit pas logger en debug quand active=true et debug desactivé")
    void ne_doit_pas_logger_en_debug_quand_active_true_et_debug_desactive() throws Exception {

        // Given
        Logger loggerMock = mock(Logger.class);
        MicroBean.setEnabledDebugMicroBean(false);

        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(() -> LoggerFactory.getLogger("MicroBean")).thenReturn(loggerMock);
            IsolatedLogHelperHarness harness = IsolatedLogHelperHarness.load();

            // When
            harness.debug("debug %s", "ko");

            // Then
            verify(loggerMock, never()).debug("debug ko");
        }
    }

    @Test
    @DisplayName("Doit logger en trace quand active=true")
    void doit_logger_en_trace_quand_active_true() throws Exception {

        // Given
        Logger loggerMock = mock(Logger.class);

        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(() -> LoggerFactory.getLogger("MicroBean")).thenReturn(loggerMock);
            IsolatedLogHelperHarness harness = IsolatedLogHelperHarness.load();

            // When
            harness.trace("trace %s", "ok");

            // Then
            verify(loggerMock).trace("trace ok");
        }
    }

    @Test
    @DisplayName("Doit logger la banniere quand active=true")
    void doit_logger_la_banniere_quand_active_true() throws Exception {

        // Given
        Logger loggerMock = mock(Logger.class);
        String banner = "=== DEMARRAGE ===";

        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(() -> LoggerFactory.getLogger("MicroBean")).thenReturn(loggerMock);
            IsolatedLogHelperHarness harness = IsolatedLogHelperHarness.load();

            // When
            harness.banner(banner);

            // Then
            verify(loggerMock).info("\n{}", banner);
        }
    }

    @Test
    @DisplayName("Doit logger en error quand active=true")
    void doit_logger_en_error_quand_active_true() throws Exception {

        // Given
        Logger loggerMock = mock(Logger.class);
        IllegalStateException throwable = new IllegalStateException("boom");

        try (MockedStatic<LoggerFactory> loggerFactoryMock = mockStatic(LoggerFactory.class)) {
            loggerFactoryMock.when(() -> LoggerFactory.getLogger("MicroBean")).thenReturn(loggerMock);
            IsolatedLogHelperHarness harness = IsolatedLogHelperHarness.load();

            // When
            harness.error("erreur throwable", throwable);

            // Then
            verify(loggerMock).error("erreur throwable", throwable);
        }
    }

    private static boolean isLogActive() {
        try {
            Field activeField = LogHelper.class.getDeclaredField("active");
            activeField.setAccessible(true);
            return activeField.getBoolean(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Impossible de lire l'etat du logger", e);
        }
    }

    private static void restorePropertyMicroBeanDebug(String value) {
        if (value == null)
            System.clearProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG);
        else
            System.setProperty(MicroBean.PROPERTY_MICROBEAN_DEBUG, value);
    }

    @SuppressWarnings("all")
    private static final class IsolatedLogHelperHarness {

        private static final String CLASS_NAME = "com.jasonpercus.microbean.infrastructure.helpers.LogHelper";

        private final Method debug;
        private final Method trace;
        private final Method banner;
        private final Method error;

        private IsolatedLogHelperHarness(Method debug, Method trace, Method banner, Method error) {
            this.debug = debug;
            this.trace = trace;
            this.banner = banner;
            this.error = error;
        }

        static IsolatedLogHelperHarness load() throws Exception {
            ClassLoader isolatedLoader = new ClassLoader(LogHelperTest.class.getClassLoader()) {
                @Override
                protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    if (!CLASS_NAME.equals(name))
                        return super.loadClass(name, resolve);

                    Class<?> loaded = findLoadedClass(name);
                    if (loaded != null)
                        return loaded;

                    try {
                        Path classFile = Path.of(System.getProperty("user.dir"), "target", "classes")
                                .resolve(name.replace('.', '/') + ".class");

                        byte[] bytecode = Files.readAllBytes(classFile);

                        Class<?> defined = defineClass(name, bytecode, 0, bytecode.length);

                        if (resolve)
                            resolveClass(defined);

                        return defined;
                    } catch (Exception e) {
                        throw new ClassNotFoundException("Impossible de charger " + name, e);
                    }
                }
            };

            Class<?> clazz = Class.forName(CLASS_NAME, true, isolatedLoader);

            Method debugMethod = clazz.getMethod("debug", String.class, Object[].class);
            Method traceMethod = clazz.getMethod("trace", String.class, Object[].class);
            Method bannerMethod = clazz.getMethod("banner", String.class);
            Method errorMethod = clazz.getMethod("error", String.class, Throwable.class);

            return new IsolatedLogHelperHarness(debugMethod, traceMethod, bannerMethod, errorMethod);
        }

        void debug(String message, Object... args) throws Exception {
            debug.invoke(null, message, args);
        }

        void trace(String message, Object... args) throws Exception {
            trace.invoke(null, message, args);
        }

        void banner(String value) throws Exception {
            banner.invoke(null, value);
        }

        void error(String message, Throwable throwable) throws Exception {
            error.invoke(null, message, throwable);
        }
    }
}
