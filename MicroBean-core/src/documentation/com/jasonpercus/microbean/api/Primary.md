# 📄 @Primary [🧩 Annotation]

## 🎯 Description

L'annotation `@Primary` indique qu'un bean, un service ou un adaptateur doit être privilégié lors de l'injection si plusieurs candidats sont disponibles. Elle permet de désigner un composant comme principal lors de la résolution automatique des dépendances, sauf si une annotation [`@Named`](./Named.md) est utilisée pour lever l'ambiguïté.

## 🧠 Rôle dans l'architecture

`@Primary` introduit un mécanisme de résolution de conflit lors de l'injection de dépendances. Elle permet au conteneur MicroBean de sélectionner automatiquement le composant annoté comme principal parmi plusieurs candidats du même type, simplifiant la configuration dans les cas courants. Elle s'intègre avec les mécanismes de nommage et de qualification explicite (`@Named`).

## 🔗 Relations

- Utilisé par :
  - Le conteneur MicroBean pour la résolution automatique des dépendances multiples.
- Concepts liés :
  - [`@Bean`](./Bean.md), [`@Service`](./Service.md), [`@Adapter`](./Adapter.md) : cibles possibles de l'annotation.
  - [`@Named`](./Named.md) : qualification explicite prioritaire sur `@Primary`.

## 💡 Exemples d'utilisation

```java
@Configuration
public class AppConfig {

    @Bean
    @Primary
    public PaymentService defaultPaymentService() {
        return new DefaultPaymentService();
    }
    
}
```

```java
@Service
@Primary
public class DefaultPaymentService implements PaymentService {
    // ...
}
```

```java
@Adapter
@Primary
public class FileWindowsAdapter implements FileAdapter {
    // ...
}
```

## 🔄 Comportement du cycle de vie

1. Découverte de l'annotation lors du scan des composants.
2. Enregistrement du composant comme candidat principal pour son type.
3. Lors de l'injection, si plusieurs candidats existent, le composant annoté `@Primary` est sélectionné par défaut.
4. Si une annotation [`@Named`](./Named.md) est présente, elle prend le dessus sur `@Primary`.

## ⚠️ Limitations / cas particuliers

- `@Primary` ne s'applique qu'en cas de conflit entre plusieurs candidats du même type.
- Privilégiez l'utilisation de [`@Named`](./Named.md) pour une sélection explicite lorsque cela est possible.
- Peut être combinée avec [`@Profile`](./Profile.md) ou [`@Condition`](./Condition.md) pour une activation conditionnelle.

## 📍 Notes internes MicroBean

- L'annotation `@Primary` simplifie la configuration dans les cas courants de résolution de dépendances multiples.
- Elle permet une extension du conteneur pour supporter des stratégies avancées de sélection de candidats.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`@Service`](./Service.md) – stéréotype pour les services métier
- [`@Bean`](./Bean.md) – déclaration de méthodes de production de beans
- [`@Adapter`](./Adapter.md) – stéréotype pour les adaptateurs techniques
- [`@Named`](./Named.md) – nommage explicite des composants
- [`@Profile`](./Profile.md) – activation selon le profil d'exécution
- [`@Condition`](./Condition.md) – activation conditionnelle personnalisée
