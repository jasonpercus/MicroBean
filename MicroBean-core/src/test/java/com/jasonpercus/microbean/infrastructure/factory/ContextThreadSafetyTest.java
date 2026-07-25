package com.jasonpercus.microbean.infrastructure.factory;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de sécurité de thread pour le Context")
class ContextThreadSafetyTest {

    @Test
    @DisplayName("Doit créer un singleton une seule fois même en cas d'accès concurrent")
    void doit_creer_singleton_une_seule_fois_cas_acces_concurrent() throws Exception {

        // Given
        ConcurrentSingletonConfig.createdCount.set(0);

        Context context = createContext();
        ConcurrentSingletonConfig config = new ConcurrentSingletonConfig();
        Method factoryMethod = ConcurrentSingletonConfig.class.getDeclaredMethod("singletonBean");
        BeanDefinition<?> definition = new BeanDefinition<>(config, factoryMethod, context);
        context.register(definition);

        int taskCount = 24;
        CountDownLatch startSignal = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(8);

        try {

            // When
            List<Callable<SingletonBean>> tasks = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                tasks.add(() -> {
                    assertThat(startSignal.await(2, TimeUnit.SECONDS)).isTrue();
                    return (SingletonBean) context.getBean(SingletonBean.class);
                });
            }

            List<Future<SingletonBean>> futures = tasks.stream()
                    .map(executor::submit)
                    .toList();

            startSignal.countDown();

            Set<SingletonBean> beans = new java.util.HashSet<>();
            for (Future<SingletonBean> future : futures) {
                beans.add(future.get(2, TimeUnit.SECONDS));
            }

            // Then
            assertThat(beans).hasSize(1);
            assertThat(ConcurrentSingletonConfig.createdCount.get()).isEqualTo(1);

        } finally {
            executor.shutdownNow();
        }
    }

    private static Context createContext() {
        Comparator<Class<?>> classComparator = Comparator.comparing(Class::getName);
        return new Context(new TreeSet<>(classComparator), new TreeSet<>(classComparator));
    }

    static class SingletonBean {
    }

    @Configuration
    static class ConcurrentSingletonConfig {

        static final AtomicInteger createdCount = new AtomicInteger();

        @Bean
        public SingletonBean singletonBean() throws InterruptedException {
            createdCount.incrementAndGet();
            Thread.sleep(50);
            return new SingletonBean();
        }
    }
}
