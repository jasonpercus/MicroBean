# 📄 @Profile [🧩 Annotation]

## 🎯 Description

L'annotation `@Profile` permet de restreindre l'activation d'un bean, d'un service, d'un adaptateur ou d'une configuration à un ou plusieurs profils d'exécution. Si l'annotation n'est pas présente, le composant est activé quel que soit le profil actif. Elle permet d'adapter dynamiquement la configuration et le comportement de l'application selon l'environnement (développement, test, production, etc.).

## 🧠 Rôle dans l'architecture

`@Profile` introduit un mécanisme d'activation conditionnelle basé sur le contexte d'exécution. Elle permet au conteneur MicroBean de sélectionner dynamiquement les composants à activer selon le profil courant, facilitant la gestion multi-environnements et la séparation des configurations spécifiques. Elle s'intègre avec les autres mécanismes d'activation conditionnelle comme [`@Condition`](./Condition.md).

## 🔗 Relations

- Utilisé par :
  - Le conteneur MicroBean pour l'activation conditionnelle des composants.
- Concepts liés :
  - [`@Bean`](./Bean.md), [`@Service`](./Service.md), [`@Adapter`](./Adapter.md), [`@Configuration`](./Configuration.md) : cibles possibles de l'annotation.
  - [`@Condition`](./Condition.md) : activation conditionnelle personnalisée.

## ⚙️ Attributs

| Nom   | Type     | Valeur par défaut | Rôle / Impact à l'exécution                             |
|-------|----------|-------------------|---------------------------------------------------------|
| value | String[] | (obligatoire)     | Liste des profils pour lesquels le composant est activé |

## 💡 Exemples d'utilisation

```java
@Configuration
public class AppConfig {
    
    @Bean
    @Profile({"dev", "test"})
    public MainService mainService() {
        return new MainService();
    }
    
}
```

---

```java
@Configuration
@Profile({"prod"})
public class ProdConfig {
    // ...
}
```

---

```java
@Service
@Profile({"debug", "release"})
public class DebugService {
    // ...
}
```

---

```java
@Adapter
@Profile({"linux"})
public class FileLinuxAdapter {
    // ...
}
```

## 🔄 Comportement du cycle de vie

1. Découverte de l'annotation lors du scan des composants.
2. Vérification du profil actif de l'application.
3. Si le profil courant correspond à l'une des valeurs de `value`, le composant est activé et enregistré dans le contexte.
4. Sinon, le composant est ignoré par le conteneur.

## ⚠️ Limitations / cas particuliers

- Si aucun profil n'est précisé, le composant est toujours activé.
- La gestion des profils dépend de la configuration du contexte d'exécution.
- Peut être combinée avec [`@Condition`](./Condition.md) pour des scénarios avancés.

## 📍 Notes internes MicroBean

- Le mécanisme de profil favorise la séparation des environnements et la modularité de la configuration.
- Il permet une adaptation fine du comportement applicatif sans modifier le code métier.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`@Configuration`](./Configuration.md) – déclaration de classes de configuration
- [`@Bean`](./Bean.md) – déclaration de méthodes de production de beans
- [`@Service`](./Service.md) – stéréotype pour les services métier
- [`@Adapter`](./Adapter.md) – stéréotype pour les adaptateurs techniques
- [`@Condition`](./Condition.md) – activation conditionnelle personnalisée
