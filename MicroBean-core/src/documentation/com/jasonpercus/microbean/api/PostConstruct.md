# 📄 @PostConstruct [🧩 Annotation]

## 🎯 Description

L'annotation `@PostConstruct` permet d'indiquer qu'une méthode d'un bean doit être exécutée automatiquement après l'initialisation complète de l'instance par le conteneur d'injection de dépendances MicroBean. Elle est utilisée pour réaliser des opérations d'initialisation personnalisées, telles que l'ouverture de ressources, la vérification de dépendances ou la configuration de l'état interne du bean.

## 🧠 Rôle dans l'architecture

`@PostConstruct` introduit un point d'extension dans le cycle de vie des beans gérés. Elle permet au conteneur MicroBean d'appeler automatiquement la méthode annotée après l'injection des dépendances, garantissant que l'instance est complètement initialisée avant toute utilisation. Elle s'intègre avec les mécanismes d'instanciation, d'injection et de destruction du framework.

## 🔗 Relations

- Utilisé par :
  - Le conteneur MicroBean pour l'initialisation post-construction des beans, services, adaptateurs, etc.
- Concepts liés :
  - [`@Bean`](./Bean.md), [`@Service`](./Service.md), [`@Adapter`](./Adapter.md) : cibles potentielles de l'annotation.

## 💡 Exemples d'utilisation

```java
@Service
public class MonBean {

    @PostConstruct
    public void initialiser() {
        // Code d'initialisation
    }

}
```

## 🔄 Comportement du cycle de vie

1. Découverte de la méthode annotée lors de l'instanciation du bean.
2. Appel automatique de la méthode après l'injection de toutes les dépendances.
3. L'instance est considérée comme prête à l'emploi après l'exécution de la méthode.

## ⚠️ Limitations / cas particuliers

- La méthode annotée ne doit pas avoir de paramètres.
- La méthode peut être privée, protégée ou publique.
- La méthode ne doit pas lever d'exception vérifiée.
- Une seule méthode `@PostConstruct` par classe est recommandée.

## 📍 Notes internes MicroBean

- L'annotation `@PostConstruct` favorise la séparation entre la construction de l'objet et son initialisation métier.
- Elle permet une extension du conteneur pour supporter des stratégies avancées d'initialisation.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion du cycle de vie des composants.

## 📚 Voir aussi

- [`@Bean`](./Bean.md) – déclaration de méthodes de production de beans
- [`@Service`](./Service.md) – stéréotype pour les services métier
- [`@Adapter`](./Adapter.md) – stéréotype pour les adaptateurs techniques
