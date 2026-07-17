# 📄 @Configuration [🧩 Annotation]

## 🎯 Description

L'annotation `@Configuration` permet de déclarer une classe de configuration dans une application MicroBean. Une classe annotée ainsi regroupe des méthodes de configuration (souvent annotées [`@Bean`](./Bean.md)) qui produisent des beans gérés par le conteneur MicroBean. Elle centralise la définition des composants applicatifs, de leurs dépendances et de leur cycle de vie, et favorise une configuration déclarative, modulaire et testable de l'application.

## 🧠 Rôle dans l'architecture

`@Configuration` structure la configuration applicative en regroupant la déclaration des beans dans des classes dédiées. Elle permet au conteneur MicroBean de scanner, d'instancier et d'enregistrer automatiquement les méthodes de production de beans, tout en gérant l'injection des dépendances et le cycle de vie des objets. Elle s'intègre avec les autres mécanismes de configuration conditionnelle, de profil et de résolution de conflits du framework.

## 🔗 Relations

- Dépend de :
  - [`@Bean`](./Bean.md) : déclaration des méthodes de production de beans.
- Utilisé par :
  - Le conteneur MicroBean pour le scan et l'enregistrement des beans.
- Concepts liés :
  - [`@Profile`](./Profile.md), [`@Condition`](./Condition.md) : activation conditionnelle de la configuration.
  - [`@Service`](./Service.md), [`@Adapter`](./Adapter.md) : autres stéréotypes de composants gérés.

## 💡 Exemples d'utilisation

```java
@Configuration
public class AppConfig {
    
    @Bean
    public MainService mainService(OrderService orderService) {
        return new MainService(orderService);
    }
    
}
```

---

```java
@Configuration
public class AppConfig {
    
    @Bean
    public MainService mainService(OrderService orderService) {
        return new MainService(orderService);
    }

    @Bean
    public OrderService orderService() {
        return new OrderService();
    }
    
}
```

## 🔄 Comportement du cycle de vie

1. Découverte lors du scan des classes annotées dans le classpath.
2. Instanciation de la classe de configuration par le conteneur.
3. Enregistrement des méthodes annotées [`@Bean`](./Bean.md) comme producteurs de beans.
4. Injection automatique des dépendances dans les méthodes de configuration.
5. Initialisation post-construction des beans si applicable.

## ⚠️ Limitations / cas particuliers

- Une classe annotée `@Configuration` ne doit contenir que des méthodes de production de beans ou de la logique de configuration.
- Les dépendances nécessaires à la création des beans doivent être déclarées en paramètres des méthodes.
- L'ordre d'instanciation des beans dépend de la résolution des dépendances.
- Peut être combinée avec [`@Profile`](./Profile.md) ou [`@Condition`](./Condition.md) pour une activation conditionnelle.

## 📍 Notes internes MicroBean

- L'annotation `@Configuration` favorise la séparation des préoccupations et la testabilité de la configuration applicative.
- Elle permet une extension du conteneur pour supporter des stratégies avancées de configuration modulaire.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`@Bean`](./Bean.md) – déclaration de méthodes de production de beans
- [`@Profile`](./Profile.md) – activation selon le profil d'exécution
- [`@Condition`](./Condition.md) – activation conditionnelle personnalisée
- [`@Service`](./Service.md) – stéréotype pour les services métier
- [`@Adapter`](./Adapter.md) – stéréotype pour les adaptateurs techniques
