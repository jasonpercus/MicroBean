# 📄 @Bean [🧩 Annotation]

## 🎯 Description

L'annotation `@Bean` indique qu'une méthode produit un bean géré par le conteneur MicroBean. Elle s'utilise sur des méthodes de configuration (factory methods) dans une classe annotée [`@Configuration`](./Configuration.md). Les instances produites sont enregistrées dans le contexte d'injection et peuvent être injectées dans d'autres composants du framework. Elle permet de centraliser la définition, la configuration et le cycle de vie des objets applicatifs.

## 🧠 Rôle dans l'architecture

`@Bean` formalise la création déclarative de composants dans un système IoC. Elle permet au conteneur MicroBean de détecter, instancier et injecter automatiquement les objets produits par les méthodes annotées, en gérant leur cycle de vie (`Scope`) et leur nommage (`name`). Elle intervient lors de la phase de scan et d'enregistrement des beans, et s'intègre avec les mécanismes d'injection, de profil, de condition et de résolution de conflits du framework.

## 🔗 Relations

- Dépend de :
  - [`Scope`](./Scope.md) : gestion du cycle de vie du bean (singleton, prototype).
- Utilisé par :
  - Le conteneur MicroBean pour l'enregistrement et l'injection des beans.
- Concepts liés :
  - [`@Configuration`](./Configuration.md) : déclaration des méthodes de production de beans.
  - [`@Service`](./Service.md), [`@Adapter`](./Adapter.md) : autres stéréotypes de composants gérés.
  - [`@Profile`](./Profile.md), [`@Condition`](./Condition.md) : activation conditionnelle avancée.
  - [`@Primary`](./Primary.md), [`@Named`](./Named.md) : gestion des conflits et du nommage.

## ⚙️ Attributs

| Nom   | Type   | Valeur par défaut | Rôle / Impact à l'exécution                                       |
|-------|--------|-------------------|-------------------------------------------------------------------|
| scope | Scope  | Scope.SINGLETON   | Définit le cycle de vie du bean (singleton, prototype, etc.)      |
| name  | String | ""                | Nom explicite du bean. Si vide, le nom est déduit automatiquement |

## 💡 Exemples d'utilisation

```java
public class AppConfig {
    
    // Bean singleton (par défaut)
    @Bean
    public MainService mainService(OrderService orderService) {
        return new MainService(orderService);
    }

    // Bean prototype : une nouvelle instance à chaque requête
    @Bean(scope = Scope.PROTOTYPE)
    public OrderService orderService() {
        return new OrderService();
    }

    // Bean nommé — utile pour distinguer plusieurs beans du même type
    @Bean(name = "paypal")
    public PaymentService paypalService() {
        return new PaypalService();
    }
    
}
```

---

```java
public class PaymentController {
    
    // injection par type si unique
    private final PaymentService paymentService;

    public PaymentController(@Named("paypal") PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
}
```

## 🔄 Comportement du cycle de vie

1. Découverte lors du scan des méthodes annotées dans les classes de configuration.
2. Instanciation du bean selon le `scope` défini (singleton ou prototype).
3. Injection automatique dans les composants consommateurs.
4. Initialisation post-construction si une méthode annotée [`@PostConstruct`](./PostConstruct.md) existe.
5. Destruction (si applicable) gérée par le conteneur pour les prototypes.

## ⚠️ Limitations / cas particuliers

- La méthode annotée doit retourner l'instance à gérer par le conteneur.
- Les paramètres de la méthode sont résolus par injection (autres beans gérés par le conteneur).
- Si plusieurs beans du même type existent, la résolution peut nécessiter [`@Primary`](./Primary.md) ou [`@Named`](./Named.md).
- L'utilisation de `scope = Scope.PROTOTYPE` implique une gestion attentive des ressources et de l'état.
- Évitez d'effectuer de lourds traitements dans les méthodes `@Bean` ; préférez l'initialisation paresseuse ou la délégation.

## 📍 Notes internes MicroBean

- L'annotation `@Bean` favorise la configuration déclarative et la centralisation des dépendances applicatives.
- Elle permet une extension du conteneur pour supporter des stratégies d'activation conditionnelle avancées (profil, condition personnalisée).
- L'intégration avec les autres stéréotypes (`@Service`, `@Adapter`) assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`Scope`](./Scope.md) – gestion du cycle de vie des composants
- [`@Configuration`](./Configuration.md) – déclaration des méthodes de production de beans
- [`@Service`](./Service.md) – stéréotype pour les services métier
- [`@Adapter`](./Adapter.md) – stéréotype pour les adaptateurs techniques
- [`@Profile`](./Profile.md) – activation selon le profil d'exécution
- [`@Condition`](./Condition.md) – activation conditionnelle personnalisée
- [`@Primary`](./Primary.md) – résolution de conflit de candidats
- [`@Named`](./Named.md) – nommage explicite des composants
- [`@PostConstruct`](./PostConstruct.md) – initialisation post-construction
