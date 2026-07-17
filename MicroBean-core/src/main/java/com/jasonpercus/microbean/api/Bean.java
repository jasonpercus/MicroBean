package com.jasonpercus.microbean.api;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indique qu'une méthode produit un <em>bean</em> géré par le conteneur MicroBean.
 * <p>
 * Cette annotation est destinée à marquer des méthodes de configuration (factory methods)
 * qui créent des instances managées par le conteneur. Les instances produites peuvent ensuite
 * être injectées dans d'autres composants du système via les mécanismes d'injection du framework.
 * </p>
 *
 * <h3>Attributs</h3>
 * <ul>
 *   <li>{@code scope} : définit la portée (cycle de vie) de l'instance créée. Par défaut {@link Scope#SINGLETON} — 
 *   le conteneur réutilise la même instance.</li>
 *   <li>{@code name} : nom optionnel du bean. Si laissé vide, le conteneur dérive un nom en utilisant la qualification 
 *   par type.</li>
 * </ul>
 *
 * <h3>Contrat</h3>
 * <ul>
 *   <li>La méthode annotée doit retourner l'instance à gérer par le conteneur.</li>
 *   <li>Les paramètres de la méthode sont résolus par injection (autres beans gérés par le conteneur) — 
 *   le conteneur doit fournir ces dépendances au moment de la création si elles existent dans le contexte.</li>
 *   <li>La méthode est exécutée par le conteneur ; en {@link Scope#SINGLETON} elle est invoquée 
 *   une seule fois (par défaut), en {@link Scope#PROTOTYPE} elle peut être invoquée à chaque demande.</li>
 * </ul>
 *
 * <h3>Exemples</h3>
 * <pre>{@code
 * public class AppConfig {
 *
 *     // Bean singleton (par défaut)
 *     @Bean
 *     public MainService mainService(OrderService orderService) {
 *         return new MainService(orderService);
 *     }
 *
 *     // Bean prototype : une nouvelle instance à chaque requête
 *     @Bean(scope = Scope.PROTOTYPE)
 *     public OrderService orderService() {
 *         return new OrderService();
 *     }
 *
 *     // Bean nommé — utile pour distinguer plusieurs beans du même type
 *     @Bean(name = "paypal")
 *     public PaymentService paypalService() {
 *         return new PaypalService();
 *     }
 *
 * }
 * }
 * </pre>
 *
 * <p>Injection d'un bean nommé (exemple d'utilisation) :</p>
 * <pre>{@code
 * public class PaymentController {
 *
 *     // injection par type si unique
 *     private final PaymentService paymentService;
 *
 *     public PaymentController(@Named("paypal") PaymentService paymentService) {
 *         this.paymentService = paymentService;
 *     }
 *
 * }
 * }
 * </pre>
 *
 * <h3>Bonnes pratiques</h3>
 * <ul>
 *   <li>Privilégiez {@link Scope#SINGLETON} pour les services stateless ou coûteux à créer.</li>
 *   <li>Utilisez {@link Scope#PROTOTYPE} pour des objets porteurs d'état ou non thread-safe qui doivent être isolés 
 *   par requête/injection.</li>
 *   <li>Si vous fournissez plusieurs beans du même type, utilisez {@code name} (ou une annotation de qualification) 
 *   pour éviter l'ambiguïté lors de l'injection.</li>
 *   <li>Évitez d'effectuer de lourds traitements dans les méthodes {@code @Bean} ; préférez l'initialisation paresseuse 
 *   ou la déléguation quand c'est possible.</li>
 * </ul>
 *
 * @see Scope
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Bean {

    /**
     * Définit la portée (lifecycle) du bean produit par la méthode annotée.
     *
     * <p>Valeurs possibles :
     * <ul>
     *   <li>{@link Scope#SINGLETON} (valeur par défaut) : le conteneur crée et réutilise une seule instance 
     *   pour l'ensemble du contexte.</li>
     *   <li>{@link Scope#PROTOTYPE} : le conteneur crée une nouvelle instance à chaque demande d'injection.</li>
     * </ul>
     *
     * @return la portée du bean
     */
    Scope scope() default Scope.SINGLETON;

    /**
     * Nom optionnel du bean. Si la valeur est la chaîne vide (valeur par défaut), le conteneur dérive 
     * un nom automatiquement en se basant sur la résolution par type.
     *
     * @return le nom du bean, ou la chaîne vide si non fourni
     */
    String name() default "";
}
