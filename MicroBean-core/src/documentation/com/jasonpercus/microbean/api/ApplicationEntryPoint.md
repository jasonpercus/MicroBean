# 📄 ApplicationEntryPoint [🧩 Interface]

## 🎯 Description

L'interface `ApplicationEntryPoint` définit le point d'entrée principal d'une application MicroBean. Elle doit être implémentée par toute classe qui souhaite définir le démarrage d'un service de l'application. Elle centralise la logique d'initialisation et de démarrage, et permet au conteneur d'invoquer la méthode principale de lancement.

## 🧠 Rôle dans l'architecture

`ApplicationEntryPoint` formalise le contrat d'entrée pour les services principaux d'une application MicroBean. Elle permet au conteneur ou au framework d'invoquer de manière uniforme la méthode `main(String[] args)` pour démarrer un service, assurant ainsi la cohérence du bootstrap applicatif. Elle s'intègre avec l'annotation [`@EntryPointService`](./EntryPointService.md) pour la gestion du cycle de vie.

## 🔗 Relations

- Utilisé par :
  - Le conteneur MicroBean pour l'exécution des points d'entrée applicatifs.
- Concepts liés :
  - [`@EntryPointService`](./EntryPointService.md) : annotation de configuration du cycle de vie du point d'entrée.
  - [`LifecycleEntryPoint`](./LifecycleEntryPoint.md) : mode d'exécution du service principal.
  - [`Environment`](./Environment.md) : accès injectable aux arguments et au profil actif.

## 🔧 Méthodes

| Signature                                 | Description                                          | Comportement IoC / Effets de bord                 |
|-------------------------------------------|------------------------------------------------------|---------------------------------------------------|
| void main(String[] args) throws Exception | Méthode principale appelée pour démarrer le service. | Invoquée par le conteneur au lancement du service |

## 💡 Exemples d'utilisation

```java
@EntryPointService(lifecycle = LifecycleEntryPoint.ONE_SHOT)
public class MonApplication implements ApplicationEntryPoint {
    
    @Override
    public void main(String[] args) throws Exception {
        // Logique de démarrage de l'application
    }
    
}
```

## 🔄 Comportement du cycle de vie

1. Découverte de la classe implémentant l'interface lors du scan des composants.
2. Instanciation de la classe par le conteneur.
3. Appel automatique de la méthode `main(String[] args)` lors du démarrage du service.
4. En parallèle, l'entry point peut aussi injecter `Environment` comme n'importe quel bean géré.

## ⚠️ Limitations / cas particuliers

- Une seule méthode `main(String[] args)` doit être implémentée par classe.
- L'interface ne gère pas la terminaison ou la supervision du service : cela relève de l'implémentation.
- Doit être combinée avec [`@EntryPointService`](./EntryPointService.md) pour la gestion du cycle de vie.
- Pour acceder au contexte runtime (profil/args), préférer l'injection de `Environment` en plus du `String[] args` reçu par `main`.

## 📍 Notes internes MicroBean

- L'interface `ApplicationEntryPoint` favorise la standardisation du démarrage des services applicatifs.
- Elle permet une extension du conteneur pour supporter des stratégies avancées de gestion du cycle de vie.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`@EntryPointService`](./EntryPointService.md) – annotation de configuration du point d'entrée
- [`LifecycleEntryPoint`](./LifecycleEntryPoint.md) – modes de cycle de vie des points d'entrée
- [`Environment`](./Environment.md) – contexte runtime injectable
