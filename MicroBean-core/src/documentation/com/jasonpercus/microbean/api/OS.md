# 📄 OS [🧩 Enumération]

## 🎯 Description

L'énumération `OS` permet de représenter les systèmes d'exploitation supportés pour la configuration conditionnelle des [`@Adapter`](./Adapter.md) ou de tout composant dépendant du système d'exploitation. Elle permet de cibler l'exécution ou l'activation de certains composants selon l'environnement d'exécution.

## 🧠 Rôle dans l'architecture

`OS` introduit un mécanisme de filtrage conditionnel dans le cycle de vie des composants MicroBean. Elle permet au conteneur de restreindre l'activation de certains adaptateurs à un ou plusieurs systèmes d'exploitation spécifiques, garantissant ainsi la portabilité et l'adaptabilité de l'application.

## 🔗 Relations

- Utilisé par :
  - [`@Adapter`](./Adapter.md) pour l'activation conditionnelle selon un ou plusieurs OS.
- Concepts liés :
  - [`@Condition`](./Condition.md) : activation conditionnelle personnalisée.

## 🔧 Valeurs

| Nom     | Description                              |
|---------|------------------------------------------|
| ALL     | Tous les systèmes d'exploitation         |
| WINDOWS | Système d'exploitation Microsoft Windows |
| LINUX   | Système d'exploitation Linux             |
| MAC     | Système d'exploitation macOS             |

## 💡 Exemples d'utilisation

```java
@Adapter(os = OS.WINDOWS)
public class MonAdapter {
    // Adaptateur spécifique à Windows
}
```

## 🔄 Comportement du cycle de vie

1. Découverte de la valeur de l'énumération lors de l'enregistrement du composant.
2. Activation du composant uniquement si l'OS courant correspond à la valeur spécifiée.
3. Si `ALL` est utilisé, le composant est activé sur tous les systèmes d'exploitation.

## ⚠️ Limitations / cas particuliers

- L'utilisation d'une valeur incorrecte ne lèvera pas d'erreur, mais rendra le composant inactif sur l'OS courant.
- Peut être combinée avec [`@Condition`](./Condition.md) pour des scénarios avancés.

## 📍 Notes internes MicroBean

- L'énumération `OS` favorise la portabilité et la robustesse de la configuration conditionnelle.
- Elle permet une extension du conteneur pour supporter de nouveaux systèmes d'exploitation si nécessaire.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`@Adapter`](./Adapter.md) – stéréotype pour les adaptateurs techniques
- [`@Condition`](./Condition.md) – activation conditionnelle personnalisée
