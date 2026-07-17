# 📄 @Named [🧩 Annotation]

## 🎯 Description

L'annotation `@Named` permet de spécifier un nom explicite pour un paramètre lors de l'injection de dépendances. Elle est particulièrement utile pour distinguer plusieurs beans du même type dans le contexte d'injection, en levant toute ambiguïté lors de la résolution automatique par le conteneur MicroBean.

## 🧠 Rôle dans l'architecture

`@Named` introduit un mécanisme de qualification explicite lors de l'injection de dépendances. Elle permet au conteneur MicroBean de sélectionner précisément le bean à injecter parmi plusieurs candidats du même type, en se basant sur un nom unique. Elle s'intègre avec les mécanismes de nommage, de résolution de conflits et de gestion des stéréotypes (`@Bean`, `@Service`, `@Adapter`).

## 🔗 Relations

- Utilisé par :
  - Le conteneur MicroBean pour la résolution des dépendances nommées.
- Concepts liés :
  - [`@Bean`](./Bean.md), [`@Service`](./Service.md), [`@Adapter`](./Adapter.md) : sources potentielles de beans nommés.
  - [`@Primary`](./Primary.md) : résolution de conflit par priorité.

## ⚙️ Attributs

| Nom   | Type   | Valeur par défaut | Rôle / Impact à l'exécution                                 |
|-------|--------|-------------------|-------------------------------------------------------------|
| value | String | (obligatoire)     | Nom du bean à injecter. Doit correspondre à un bean déclaré |

## 💡 Exemples d'utilisation

```java
@Service
public class MonService {

  public MonService(@Named("specialBean") Bean bean) {
    // Utilisation du bean nommé "specialBean"
  }

}
```

---

```java
@Configuration
public class MaConfiguration {

  @Bean(name = "specialBean")
  public Bean specialBean() {
    return new Bean();
  }

}
```

## 🔄 Comportement du cycle de vie

1. Découverte de l'annotation lors de l'analyse des paramètres de constructeur ou de méthode.
2. Résolution du bean correspondant au nom fourni dans `value`.
3. Injection du bean nommé dans le composant cible.

## ⚠️ Limitations / cas particuliers

- Le nom fourni doit correspondre exactement à un bean déclaré dans le contexte d'injection.
- Si aucun bean ne correspond, une erreur de résolution sera levée au démarrage.
- Peut être combinée avec [`@Primary`](./Primary.md) pour lever les ambiguïtés restantes.

## 📍 Notes internes MicroBean

- L'annotation `@Named` favorise la clarté et la robustesse de l'injection de dépendances dans les cas complexes.
- Elle permet une extension du conteneur pour supporter des stratégies avancées de qualification et de sélection de beans.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`@Bean`](./Bean.md) – déclaration de méthodes de production de beans
- [`@Service`](./Service.md) – stéréotype pour les services métier
- [`@Adapter`](./Adapter.md) – stéréotype pour les adaptateurs techniques
- [`@Primary`](./Primary.md) – résolution de conflit de candidats
