# 📄 Environment & Arguments [🧩 Runtime API]

## 🎯 Description

`Environment` représente le contexte d'exécution accessible par les composants applicatifs. Il encapsule les arguments de démarrage via un objet `Arguments` et expose aussi le profil actif.

`Arguments` est une classe utilitaire orientée manipulation des paramètres CLI (recherche, indexation, préfixes, représentation texte).

> Dans MicroBean, `Environment` est **créé automatiquement au démarrage** puis **enregistré dans le conteneur** comme singleton. Il peut donc être injecté dans n'importe quel `@Bean`, `@Service` ou `@Adapter`.

## 🧠 Rôle dans l'architecture

### Environment

`Environment` agit comme un point d'accès central aux informations d'exécution:
- les arguments passés à l'application (`getArguments()`),
- le profil actif (`getProfile()`).

Pendant l'initialisation, MicroBean instancie `Environment` avec les arguments d'entrée, puis l'enregistre dans le contexte IoC (`registerSingleton(Environment.class, environment)`). Cela garantit une instance unique, cohérente et injectable partout.

### Arguments

`Arguments` encapsule les arguments de la ligne de commande et fournit une API pratique pour:
- accéder à un argument par index,
- rechercher la présence d'arguments,
- tester des préfixes (`--profile=`, `--port=`, etc.),
- reconstruire une représentation texte sûre (gestion des guillemets/espaces).

## 🔗 Relations

- Dépend de :
  - `Environment` dépend de `Arguments` pour le stockage/manipulation des arguments.
  - `Environment#getProfile()` s'appuie sur `MicroBean.getActiveProfile()`.
- Utilisé par :
  - Le conteneur MicroBean lors de l'initialisation runtime.
  - Tous les composants injectables ayant besoin d'accéder au contexte de démarrage.
- Concepts liés :
  - [`@Bean`](./Bean.md), [`@Service`](./Service.md), [`@Adapter`](./Adapter.md) : composants pouvant injecter `Environment`.
  - [`Profile`](./Profile.md) : sélection du profil d'exécution.

## ⚙️ API principale

### Environment

| Méthode                      | Retour      | Rôle                                                          |
|------------------------------|-------------|---------------------------------------------------------------|
| `Environment(String[] args)` | -           | Construit l'environnement à partir des arguments de démarrage |
| `getArguments()`             | `Arguments` | Retourne l'objet de gestion des arguments                     |
| `getProfile()`               | `String`    | Retourne le profil actif (ou `null` s'il n'est pas défini)    |

### Arguments

| Méthode                               | Retour     | Rôle                                                    |
|---------------------------------------|------------|---------------------------------------------------------|
| `getArgs()`                           | `String[]` | Retourne une copie des arguments                        |
| `getArg(int index)`                   | `String`   | Retourne l'argument à une position donnée               |
| `size()`                              | `int`      | Nombre total d'arguments                                |
| `contains(String arg)`                | `boolean`  | Vérifie la présence d'un argument                       |
| `containsAny(String... args)`         | `boolean`  | Vérifie si au moins un argument est présent             |
| `indexOfArgWithPrefix(String prefix)` | `int`      | Recherche le premier argument commençant par un préfixe |
| `hasArgWithPrefix(String prefix)`     | `boolean`  | Indique si un argument avec préfixe existe              |
| `toString()`                          | `String`   | Recompose les arguments en chaîne exploitable           |

## 💡 Exemples d'utilisation

```java
@Service
public class StartupLogger {

    private final Environment environment;

    public StartupLogger(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void logRuntimeContext() {
        System.out.println("Profile actif: " + environment.getProfile());
        System.out.println("Args: " + environment.getArguments());
    }
}
```

---

```java
@Service
public class FeatureToggleService {

    private final Arguments arguments;

    public FeatureToggleService(Environment environment) {
        this.arguments = environment.getArguments();
    }

    public boolean isVerboseMode() {
        return arguments.containsAny("--verbose", "-v");
    }

    public String configuredPortOrDefault() {
        int index = arguments.indexOfArgWithPrefix("--port=");
        return index >= 0
                ? arguments.getArg(index).substring("--port=".length())
                : "8080";
    }
}
```

## 🔄 Cycle de vie runtime

1. MicroBean lit les arguments de démarrage.
2. Un objet `Environment` est créé avec ces arguments.
3. `Environment` est enregistré dans le conteneur comme singleton.
4. Lors de la résolution des dépendances, `Environment` peut être injecté dans n'importe quel bean géré.
5. Les composants accèdent ensuite à `Arguments` via `environment.getArguments()`.

## ⚠️ Limitations / cas particuliers

- `getProfile()` peut retourner `null` si aucun profil actif n'est défini.
- `Arguments#getArg(int)` lève une exception si l'index est invalide.
- `Arguments#toString()` applique un échappement/quotage ; la sortie est pensée pour la lisibilité, pas comme parseur complet shell universel.
- `Arguments` expose une vue logique des arguments de démarrage, mais ne modifie pas les paramètres système réels.

## 📍 Notes internes MicroBean

- L'instance `Environment` est initialisée très tôt dans la phase de bootstrap.
- L'enregistrement en singleton garantit une source unique de vérité pour le contexte runtime.
- Cette approche simplifie les services transverses (logs, feature flags, diagnostics, configuration légère).

## 📚 Voir aussi

- [`@Service`](./Service.md) – stéréotype de service injectable
- [`@Bean`](./Bean.md) – composant géré par le conteneur
- [`@Adapter`](./Adapter.md) – composant d'adaptation technique
- [`@Profile`](./Profile.md) – gestion du profil d'exécution
- [`@PostConstruct`](./PostConstruct.md) – initialisation après injection
