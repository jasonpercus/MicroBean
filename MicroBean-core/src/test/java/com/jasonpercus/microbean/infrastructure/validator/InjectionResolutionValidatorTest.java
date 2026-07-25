package com.jasonpercus.microbean.infrastructure.validator;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import com.jasonpercus.microbean.api.Bean;
import com.jasonpercus.microbean.api.Configuration;
import com.jasonpercus.microbean.api.Named;
import com.jasonpercus.microbean.api.Service;
import com.jasonpercus.microbean.infrastructure.factory.Context;
import com.jasonpercus.microbean.infrastructure.run.Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitaires de InjectionResolutionValidator")
class InjectionResolutionValidatorTest {

    @Test
    @DisplayName("Doit réussir si une fabrique expose une interface et qu'un type interface est injecté")
    void doit_reussir_si_une_fabrique_expose_interface_et_qu_un_type_interface_est_injecte () {

        // Given
        Context context = createContext();

        // When & Then
        assertThatCode(() -> Processor.execute(Set.of(GoodConfig.class, InterfaceConsumer.class), context, new String[0]))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Doit échouer si une fabrique expose seulement de l'abstraction mais qu'un type concret est injecté")
    void doit_echouer_si_une_fabrique_expose_seulement_de_l_abstraction_mais_qu_un_type_concret_est_injecte() {

        // Given
        Context context = createContext();

        // When & Then
        assertThatThrownBy(() -> Processor.execute(Set.of(BadConfig.class, ConcreteConsumer.class), context, new String[0]))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unresolvable injection")
                .hasMessageContaining(PaymentServiceImpl.class.getName());
    }

    @Test
    @DisplayName("Doit réussir si une dépendance nommée est trouvée")
    void doit_reussir_si_une_dependance_nommee_est_trouvee() {

        // Given
        Context context = createContext();

        // When & Then
        assertThatCode(() -> Processor.execute(Set.of(NamedConfig.class, NamedPaypalConsumer.class), context, new String[0]))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Doit échouer si une dépendance nommée est manquante")
    void doit_echouer_si_une_dependance_nommee_est_manquante() {

        // Given
        Context context = createContext();

        // When & Then
        assertThatThrownBy(() -> Processor.execute(Set.of(NamedConfig.class, NamedConsumer.class), context, new String[0]))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unresolvable injection")
                .hasMessageContaining("stripe");
    }

    private static Context createContext() {
        Comparator<Class<?>> classComparator = Comparator.comparing(Class::getName);
        return new Context(new TreeSet<>(classComparator), new TreeSet<>(classComparator));
    }

    public interface PaymentApi {
    }

    public static class PaymentServiceImpl implements PaymentApi {
    }

    @Configuration
    public static class BadConfig {

        public BadConfig() {
        }

        @Bean
        @SuppressWarnings("unused")
        public PaymentApi paymentApi() {
            return new PaymentServiceImpl();
        }
    }

    @Configuration
    public static class GoodConfig {

        public GoodConfig() {
        }

        @Bean
        @SuppressWarnings("unused")
        public PaymentApi paymentApi() {
            return new PaymentServiceImpl();
        }
    }

    @Configuration
    public static class NamedConfig {

        public NamedConfig() {
        }

        @Bean(name = "paypal")
        @SuppressWarnings("unused")
        public PaymentApi paymentApi() {
            return new PaymentServiceImpl();
        }
    }

    @Service
    public static class ConcreteConsumer {

        @SuppressWarnings("unused")
        public ConcreteConsumer(PaymentServiceImpl paymentService) {
        }
    }

    @Service
    public static class InterfaceConsumer {

        @SuppressWarnings("unused")
        public InterfaceConsumer(PaymentApi paymentApi) {
        }
    }

    @Service
    public static class NamedConsumer {

        @SuppressWarnings("unused")
        public NamedConsumer(@Named("stripe") PaymentApi paymentApi) {
        }
    }

    @Service
    public static class NamedPaypalConsumer {

        @SuppressWarnings("unused")
        public NamedPaypalConsumer(@Named("paypal") PaymentApi paymentApi) {
        }
    }
}
