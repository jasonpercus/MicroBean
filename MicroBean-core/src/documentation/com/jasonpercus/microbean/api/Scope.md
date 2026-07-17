# 📄 Scope [🧩 Enumération]

## 🎯 Description

L'énumération `Scope` définit la portée (cycle de vie) d'un bean géré par le conteneur MicroBean. Elle est utilisée par le framework et les annotations de configuration pour spécifier la stratégie de création et de gestion des instances de composants. Elle structure la gestion du cycle de vie des objets applicatifs et permet d'adapter le comportement d'injection selon les besoins métier.

## 🧠 Rôle dans l'architecture

`Scope` introduit un mécanisme de gestion du cycle de vie des composants dans le conteneur MicroBean. Elle permet de choisir entre une instance partagée (singleton) ou une instance neuve à chaque injection (prototype), optimisant ainsi la consommation de ressources et la sécurité des accès concurrents.

## 🔗 Relations

- Utilisé par :
  - [`@Bean`](./Bean.md), [`@Service`](./Service.md), [`@Adapter`](./Adapter.md) pour définir la portée des composants.
- Concepts liés :
  - Cycle de vie, gestion de l'état, injection de dépendances.

## 🔧 Valeurs

| Nom       | Description                                                                                                                                                                                                  |
|-----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| SINGLETON | Une seule instance est créée et réutilisée par le conteneur pour l'ensemble du contexte d'application. À privilégier pour les objets thread-safe, coûteux à créer, ou qui doivent conserver un état partagé. |
| PROTOTYPE | Une nouvelle instance est créée à chaque fois que le bean est requis (par injection, par demande explicite, etc.). À utiliser pour des objets non thread-safe ou porteurs d'état transactionnel local.       |

## 💡 Exemples d'utilisation

```java
@Bean
public MainService mainService() {
    return new MainService();
}
// Par défaut, scope = Scope.SINGLETON
```

---

```java
@Bean(scope = Scope.PROTOTYPE)
public OrderService orderService() {
    return new OrderService();
}
```

## 🔄 Comportement du cycle de vie

1. Découverte de la valeur de l'énumération lors de l'enregistrement du composant.
2. Instanciation du bean selon la portée définie :
   - `SINGLETON` : une seule instance partagée dans le contexte d'application.
   - `PROTOTYPE` : une nouvelle instance à chaque injection ou demande explicite.
3. Gestion de la destruction et du nettoyage par le conteneur si applicable.

## ⚠️ Limitations / cas particuliers

- L'utilisation de `PROTOTYPE` implique une gestion attentive des ressources et de l'état.
- Le choix du scope doit être cohérent avec la nature métier et la thread-safety du composant.
- Peut être combinée avec d'autres mécanismes d'activation conditionnelle.

## 📍 Notes internes MicroBean

- L'énumération `Scope` favorise la flexibilité et la robustesse de la gestion du cycle de vie applicatif.
- Elle permet une extension du conteneur pour supporter de nouveaux modes de portée si nécessaire.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion des composants.

## 📚 Voir aussi

- [`@Bean`](./Bean.md) – déclaration de méthodes de production de beans
- [`@Service`](./Service.md) – stéréotype pour les services métier
- [`@Adapter`](./Adapter.md) – stéréotype pour les adaptateurs techniques
