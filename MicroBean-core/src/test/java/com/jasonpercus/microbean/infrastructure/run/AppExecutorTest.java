package com.jasonpercus.microbean.infrastructure.run;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static java.lang.Thread.MAX_PRIORITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import com.jasonpercus.microbean.api.ApplicationEntryPoint;
import com.jasonpercus.microbean.api.EntryPointService;
import com.jasonpercus.microbean.api.LifecycleEntryPoint;
import com.jasonpercus.microbean.infrastructure.exception.MicroBeanException;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import com.jasonpercus.microbean.infrastructure.helpers.LogHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

@DisplayName("Tests unitaires de la classe AppExecutor")
class AppExecutorTest {

    @Test
    @DisplayName("Doit logger et lever MicroBeanException quand getTask capture une exception")
    void doit_logger_et_lever_microbeanexception_quand_gettask_capture_une_exception() {

        // Given
        RuntimeException cause = new RuntimeException("boom");
        ApplicationEntryPoint entryPoint = args -> {
            throw cause;
        };

        try (MockedStatic<LogHelper> logHelperMock = mockStatic(LogHelper.class)) {

            // When & Then
            assertThatThrownBy(() -> AppExecutor.getTask(entryPoint, new String[]{"arg"}))
                    .isInstanceOf(MicroBeanException.class)
                    .hasCause(cause);

            logHelperMock.verify(() -> LogHelper.error("MicroBean initialization failed", cause));
        }
    }

    @Test
    @DisplayName("Doit propager MicroBeanException quand un ONE_SHOT lève une exception")
    void doit_propager_microbeanexception_quand_un_one_shot_leve_une_exception() {

        // Given
        Context context = mock(Context.class);
        RuntimeException cause = new RuntimeException("entry point failure");
        ApplicationEntryPoint failingEntryPoint = args -> { throw cause; };
        when(context.getBean(FailingOneShotEntryPoint.class)).thenReturn(failingEntryPoint);

        @SuppressWarnings("unchecked")
        Class<? extends ApplicationEntryPoint>[] entryPoints = new Class[]{FailingOneShotEntryPoint.class};

        try (MockedStatic<LogHelper> logHelperMock = mockStatic(LogHelper.class)) {

            // When & Then
            assertThatThrownBy(() -> AppExecutor.loadAndExecuteEntryPointServices(null, new String[0], entryPoints, context))
                    .isInstanceOf(MicroBeanException.class)
                    .hasCause(cause);

            logHelperMock.verify(() -> LogHelper.error("MicroBean initialization failed", cause));
            verify(context, times(1)).getBean(FailingOneShotEntryPoint.class);
        }
    }

    @Test
    @DisplayName("Doit exécuter un entry point ONE_SHOT sur le thread appelant")
    void doit_executer_un_entry_point_one_shot_sur_le_thread_appelant() {

        // Given
        Context context = mock(Context.class);
        RecordingEntryPoint entryPoint = new RecordingEntryPoint();
        String[] args = new String[]{"arg1", "arg2"};
        Thread callingThread = Thread.currentThread();

        when(context.getBean(OneShotEntryPoint.class)).thenReturn(entryPoint);

        @SuppressWarnings("unchecked")
        Class<? extends ApplicationEntryPoint>[] entryPoints = new Class[]{OneShotEntryPoint.class};

        // When
        AppExecutor.loadAndExecuteEntryPointServices(null, args, entryPoints, context);

        // Then
        assertThat(entryPoint.callCount.get()).isEqualTo(1);
        assertThat(entryPoint.lastArgs.get()).containsExactly("arg1", "arg2");
        assertThat(entryPoint.executedThread.get()).isSameAs(callingThread);
        verify(context, times(1)).getBean(OneShotEntryPoint.class);
    }

    @Test
    @DisplayName("Doit considérer un entry point non annoté comme ONE_SHOT")
    void doit_considerer_un_entry_point_non_annote_comme_one_shot() {

        // Given
        Context context = mock(Context.class);
        RecordingEntryPoint entryPoint = new RecordingEntryPoint();
        Thread callingThread = Thread.currentThread();

        when(context.getBean(DefaultOneShotEntryPoint.class)).thenReturn(entryPoint);

        @SuppressWarnings("unchecked")
        Class<? extends ApplicationEntryPoint>[] entryPoints = new Class[]{DefaultOneShotEntryPoint.class};

        // When
        AppExecutor.loadAndExecuteEntryPointServices(null, new String[0], entryPoints, context);

        // Then
        assertThat(entryPoint.callCount.get()).isEqualTo(1);
        assertThat(entryPoint.executedThread.get()).isSameAs(callingThread);
        verify(context, times(1)).getBean(DefaultOneShotEntryPoint.class);
    }

    @Test
    @DisplayName("Doit exécuter un entry point LONG_RUNNING sur un thread dédié")
    void doit_executer_un_entry_point_long_running_sur_un_thread_dedie() throws InterruptedException {

        // Given
        Context context = mock(Context.class);
        AsyncRecordingEntryPoint entryPoint = new AsyncRecordingEntryPoint();
        String[] args = new String[]{"async"};
        Thread callingThread = Thread.currentThread();

        when(context.getBean(LongRunningEntryPoint.class)).thenReturn(entryPoint);

        @SuppressWarnings("unchecked")
        Class<? extends ApplicationEntryPoint>[] entryPoints = new Class[]{LongRunningEntryPoint.class};

        // When
        AppExecutor.loadAndExecuteEntryPointServices(null, args, entryPoints, context);

        // Then
        assertThat(entryPoint.finished.await(2, TimeUnit.SECONDS)).isTrue();
        assertThat(entryPoint.callCount.get()).isEqualTo(1);
        assertThat(entryPoint.lastArgs.get()).containsExactly("async");
        assertThat(entryPoint.executedThread.get()).isNotSameAs(callingThread);
        assertThat(entryPoint.executedThread.get().getPriority()).isEqualTo(MAX_PRIORITY);
        verify(context, times(1)).getBean(LongRunningEntryPoint.class);
    }

    @Test
    @DisplayName("Doit appeler le contextConsumer une fois par entry point traité")
    void doit_appeler_le_contextconsumer_une_fois_par_entry_point_traite() throws InterruptedException {

        // Given
        Context context = mock(Context.class);
        RecordingEntryPoint oneShotEntryPoint = new RecordingEntryPoint();
        AsyncRecordingEntryPoint longRunningEntryPoint = new AsyncRecordingEntryPoint();
        AtomicInteger consumerCallCount = new AtomicInteger();
        AtomicReference<Context> lastConsumedContext = new AtomicReference<>();
        Consumer<Context> consumer = consumedContext -> {
            consumerCallCount.incrementAndGet();
            lastConsumedContext.set(consumedContext);
        };

        when(context.getBean(OneShotEntryPoint.class)).thenReturn(oneShotEntryPoint);
        when(context.getBean(LongRunningEntryPoint.class)).thenReturn(longRunningEntryPoint);

        @SuppressWarnings("unchecked")
        Class<? extends ApplicationEntryPoint>[] entryPoints = new Class[]{OneShotEntryPoint.class, LongRunningEntryPoint.class};

        // When
        AppExecutor.loadAndExecuteEntryPointServices(consumer, new String[0], entryPoints, context);

        // Then
        assertThat(longRunningEntryPoint.finished.await(2, TimeUnit.SECONDS)).isTrue();
        assertThat(consumerCallCount.get()).isEqualTo(2);
        assertThat(lastConsumedContext.get()).isSameAs(context);
        assertThat(oneShotEntryPoint.callCount.get()).isEqualTo(1);
        assertThat(longRunningEntryPoint.callCount.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("Doit lever une exception si plus d'un entry point est ONE_SHOT")
    void doit_lever_une_exception_si_plus_d_un_entry_point_est_one_shot() {

        // Given
        Context context = mock(Context.class);
        RecordingEntryPoint firstEntryPoint = new RecordingEntryPoint();
        RecordingEntryPoint secondEntryPoint = new RecordingEntryPoint();

        when(context.getBean(OneShotEntryPoint.class)).thenReturn(firstEntryPoint);
        when(context.getBean(SecondOneShotEntryPoint.class)).thenReturn(secondEntryPoint);

        @SuppressWarnings("unchecked")
        Class<? extends ApplicationEntryPoint>[] entryPoints = new Class[]{OneShotEntryPoint.class, SecondOneShotEntryPoint.class};

        // When & Then
        assertThatThrownBy(() -> AppExecutor.loadAndExecuteEntryPointServices(null, new String[0], entryPoints, context))
                .isInstanceOf(MicroBeanException.class)
                .hasMessageContaining("Only one ApplicationEntryPoint can be ONE_SHOT");

        assertThat(firstEntryPoint.callCount.get()).isEqualTo(1);
        assertThat(secondEntryPoint.callCount.get()).isZero();
        verify(context, times(1)).getBean(OneShotEntryPoint.class);
        verify(context, times(1)).getBean(SecondOneShotEntryPoint.class);
    }

    @Test
    @DisplayName("Doit prioriser un entry point LONG_RUNNING devant un ONE_SHOT")
    void doit_prioriser_un_entry_point_long_running_devant_un_one_shot() {

        // Given
        Class<? extends ApplicationEntryPoint> longRunning = LongRunningEntryPoint.class;
        Class<? extends ApplicationEntryPoint> oneShot = OneShotEntryPoint.class;

        // When
        int result = AppExecutor.compareEntryPointsByLifecycle(longRunning, oneShot);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    @DisplayName("Doit placer un entry point ONE_SHOT après un LONG_RUNNING")
    void doit_placer_un_entry_point_one_shot_apres_un_long_running() {

        // Given
        Class<? extends ApplicationEntryPoint> oneShot = OneShotEntryPoint.class;
        Class<? extends ApplicationEntryPoint> longRunning = LongRunningEntryPoint.class;

        // When
        int result = AppExecutor.compareEntryPointsByLifecycle(oneShot, longRunning);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    @DisplayName("Doit retourner zéro quand deux entry points sont ONE_SHOT")
    void doit_retourner_zero_quand_deux_entry_points_sont_one_shot() {

        // Given
        Class<? extends ApplicationEntryPoint> firstOneShot = OneShotEntryPoint.class;
        Class<? extends ApplicationEntryPoint> secondOneShot = SecondOneShotEntryPoint.class;

        // When
        int result = AppExecutor.compareEntryPointsByLifecycle(firstOneShot, secondOneShot);

        // Then
        assertThat(result).isZero();
    }

    @Test
    @DisplayName("Doit considérer un entry point non annoté comme ONE_SHOT lors de la comparaison")
    void doit_considerer_un_entry_point_non_annote_comme_one_shot_lors_de_la_comparaison() {

        // Given
        Class<? extends ApplicationEntryPoint> defaultOneShot = DefaultOneShotEntryPoint.class;
        Class<? extends ApplicationEntryPoint> annotatedOneShot = OneShotEntryPoint.class;

        // When
        int result = AppExecutor.compareEntryPointsByLifecycle(defaultOneShot, annotatedOneShot);

        // Then
        assertThat(result).isZero();
    }

    @Test
    @DisplayName("Doit placer un entry point non annoté après un LONG_RUNNING")
    void doit_placer_un_entry_point_non_annote_apres_un_long_running() {

        // Given
        Class<? extends ApplicationEntryPoint> defaultOneShot = DefaultOneShotEntryPoint.class;
        Class<? extends ApplicationEntryPoint> longRunning = LongRunningEntryPoint.class;

        // When
        int result = AppExecutor.compareEntryPointsByLifecycle(defaultOneShot, longRunning);

        // Then
        assertThat(result).isPositive();
    }


    @EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
    static class OneShotEntryPoint implements ApplicationEntryPoint {

        @Override
        public void main(String[] args) {
        }
    }

    static class DefaultOneShotEntryPoint implements ApplicationEntryPoint {

        @Override
        public void main(String[] args) {
        }
    }

    @EntryPointService(lifecycle = LifecycleEntryPoint.LONG_RUNNING)
    static class LongRunningEntryPoint implements ApplicationEntryPoint {

        @Override
        public void main(String[] args) {
        }
    }

    @EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
    static class SecondOneShotEntryPoint implements ApplicationEntryPoint {

        @Override
        public void main(String[] args) {
        }
    }

    @EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
    static class FailingOneShotEntryPoint implements ApplicationEntryPoint {

        @Override
        public void main(String[] args) {
        }
    }

    private static class RecordingEntryPoint implements ApplicationEntryPoint {

        private final AtomicInteger callCount = new AtomicInteger();
        private final AtomicReference<String[]> lastArgs = new AtomicReference<>();
        private final AtomicReference<Thread> executedThread = new AtomicReference<>();

        @Override
        public void main(String[] args) {
            callCount.incrementAndGet();
            lastArgs.set(args == null ? null : args.clone());
            executedThread.set(Thread.currentThread());
        }
    }

    private static class AsyncRecordingEntryPoint implements ApplicationEntryPoint {

        private final AtomicInteger callCount = new AtomicInteger();
        private final AtomicReference<String[]> lastArgs = new AtomicReference<>();
        private final AtomicReference<Thread> executedThread = new AtomicReference<>();
        private final CountDownLatch finished = new CountDownLatch(1);

        @Override
        public void main(String[] args) {
            callCount.incrementAndGet();
            lastArgs.set(args == null ? null : args.clone());
            executedThread.set(Thread.currentThread());
            finished.countDown();
        }
    }
}
