# 📄 LifecycleEntryPoint [🧩 Enumération]

## 🎯 Description

`LifecycleEntryPoint` définit les types de cycle de vie pour un point d'entrée de service dans une application MicroBean. Cette énumération est utilisée par l'annotation [`@EntryPointService`](./EntryPointService.md) pour indiquer comment le service annoté doit être exécuté (mode unique ou longue durée).

## 🧠 Rôle dans l'architecture

`LifecycleEntryPoint` permet au conteneur MicroBean de déterminer la stratégie d'exécution des services principaux : exécution unique sur le thread principal ou exécution continue sur un thread dédié. Elle structure la gestion du cycle de vie applicatif et la supervision des points d'entrée.

## 🔗 Relations

- Utilisé par :
  - [`@EntryPointService`](./EntryPointService.md) pour configurer le mode d'exécution du service principal.
- Concepts liés :
  - [`ApplicationEntryPoint`](./ApplicationEntryPoint.md) : interface de point d'entrée applicatif.

## 🔧 Valeurs

| Nom          | Description                                                                    |
|--------------|--------------------------------------------------------------------------------|
| ONE_SHOT     | Exécution sur le thread courant. Un seul EntryPointService de ce type autorisé |
| LONG_RUNNING | Exécution sur un thread dédié, adapté aux services de longue durée             |

## 💡 Exemples d'utilisation

```java
@EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
public class MainService implements ApplicationEntryPoint{
    // Service exécuté une seule fois sur le thread principal
}
```

---

```java
@EntryPointService(lifecycle = LifecycleEntryPoint.LONG_RUNNING)
public class BackgroundService implements ApplicationEntryPoint {
    // Service de fond exécuté sur un thread séparé
}
```

## 🔄 Comportement du cycle de vie

1. Détermination du mode d'exécution lors de l'initialisation du point d'entrée.
2. Exécution du service selon la valeur de l'énumération :
   - `ONE_SHOT` : exécution sur le thread principal, unique dans l'application.
   - `LONG_RUNNING` : exécution sur un thread dédié, adapté aux services de fond.
3. Supervision du cycle de vie par le conteneur.

## ⚠️ Limitations / cas particuliers

- Un seul service de type `ONE_SHOT` peut exister dans l'application.
- Les services de type `LONG_RUNNING` doivent être conçus pour supporter une exécution continue ou asynchrone.
- L'énumération ne gère pas la terminaison automatique des services.

## 📍 Notes internes MicroBean

- `LifecycleEntryPoint` favorise la clarté et la robustesse de la gestion du cycle de vie applicatif.
- Elle permet une extension du conteneur pour supporter des stratégies avancées de supervision.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`@EntryPointService`](./EntryPointService.md) – annotation de configuration du point d'entrée
- [`ApplicationEntryPoint`](./ApplicationEntryPoint.md) – interface de point d'entrée applicatif
