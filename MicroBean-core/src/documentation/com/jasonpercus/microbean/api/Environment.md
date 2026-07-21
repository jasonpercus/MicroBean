# 📄 Environment & Arguments [🧩 Runtime API]

## 🎯 Description

`Environment` représente le contexte d'exécution accessible par les composants applicatifs. Il encapsule :

- Les **arguments de démarrage** via un objet `Arguments`
- Le **profil actif** de l'application
- Les **propriétés de configuration** chargées depuis des fichiers YAML/JSON

`Arguments` est une classe utilitaire orientée manipulation des paramètres CLI (recherche, indexation, préfixes, représentation texte).

> Dans MicroBean, `Environment` est **créé automatiquement au démarrage** puis **enregistré dans le conteneur** comme singleton. Il peut donc être injecté dans n'importe quel `@Bean`, `@Service` ou `@Adapter`.

## 🧠 Rôle dans l'architecture

### Environment

`Environment` agit comme un point d'accès central aux informations d'exécution:
- les arguments passés à l'application (`getArguments()`),
- le profil actif (`getProfile()`),
- les propriétés de configuration (`getProperties()`, `getProperty(key)`).

Pendant l'initialisation, MicroBean :
1. instancie `Environment` avec les arguments d'entrée,
2. charge les fichiers de configuration par défaut (`application.yaml`, `application.yml`, `application.json`) s'ils existent,
3. charge les fichiers de profil (`application-{profile}.yaml`, etc.) si un profil est actif,
4. enregistre l'instance dans le contexte IoC (`registerSingleton(Environment.class, environment)`).

Cela garantit une source unique et cohérente accessible dans tous les composants.

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
  - `Initializer` charge les propriétés de configuration et les enregistre dans `Environment`.
- Utilisé par :
  - Le conteneur MicroBean lors de l'initialisation runtime.
  - Tous les composants injectables ayant besoin d'accéder au contexte de démarrage et aux propriétés.
- Concepts liés :
  - [`@Bean`](./Bean.md), [`@Service`](./Service.md), [`@Adapter`](./Adapter.md) : composants pouvant injecter `Environment`.
  - [`@MicroBeanApplication`](./MicroBeanApplication.md) : annotation pour spécifier les fichiers de configuration explicites.
  - [`Profile`](./Profile.md) : sélection du profil d'exécution.

## ⚙️ API principale

### Environment

| Méthode                                 | Retour                | Rôle                                                                  |
|-----------------------------------------|-----------------------|-----------------------------------------------------------------------|
| `Environment(String[] args)`            | -                     | Construit l'environnement à partir des arguments de démarrage         |
| `getArguments()`                        | `Arguments`           | Retourne l'objet de gestion des arguments                             |
| `getProfile()`                          | `String`              | Retourne le profil actif (ou `null` s'il n'est pas défini)            |
| `getProperties(Class<T> type)`          | `T`                   | Retourne les propriétés de configuration mappées sur le type fourni   |
| `getProperties()`                       | `Map<String, Object>` | Retourne toutes les propriétés aplaties sous forme de map             |
| `getProperty(String key)`               | `Object`              | Retourne la valeur d'une propriété spécifique                         |
| `putProperty(String key, Object value)` | -                     | Ajoute/surcharge une propriété                                        |
| `putProperties(Map)`                    | -                     | Ajoute/fusion un ensemble de propriétés                               |

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

### Accéder aux arguments et au profil

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

### Accéder aux propriétés de configuration

```java
@Service
public class ServerService {

    private final Environment environment;

    public ServerService(Environment environment) {
        this.environment = environment;
    }

    public void startServer() {
        String host = (String) environment.getProperty("server.host-name");
        int port = (int) environment.getProperty("server.port");
        System.out.println("Server running on " + host + ":" + port);
    }
}
```

### Mapper les propriétés sur une classe

```java
public class ServerConfig {
    public String hostName;
    public int port;
}

@Service
public class ConfiguredService {

    private final ServerConfig config;

    public ConfiguredService(Environment environment) {
        this.config = environment.getProperties(ServerConfig.class);
    }

    public void displayConfig() {
        System.out.println("Host: " + config.hostName + ", Port: " + config.port);
    }
}
```

### Utiliser les arguments CLI

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
3. Les fichiers de configuration par défaut (`application.yaml`, `application.yml`, `application.json`) sont chargés s'ils existent.
4. Si un profil est actif, les fichiers de profil correspondants (`application-{profile}.yaml`, etc.) sont chargés et fusionnent les propriétés.
5. Les fichiers de configuration explicites (via `@MicroBeanApplication(configurationProperties=...)`) sont chargés.
6. `Environment` est enregistré dans le conteneur comme singleton.
7. Les composants accèdent ensuite aux propriétés via `environment.getProperties()`, `environment.getProperty(key)`, etc.

## ⚠️ Limitations / cas particuliers

- `getProfile()` peut retourner `null` si aucun profil actif n'est défini.
- `Arguments#getArg(int)` lève une exception si l'index est invalide.
- Les fichiers de configuration par défaut sont optionnels (pas d'erreur s'ils n'existent pas).
- Les fichiers de configuration explicites sont obligatoires (erreur s'ils n'existent pas).
- Les propriétés imbriquées sont aplaties en notation pointée (kebab-case).
- `Arguments` expose une vue logique des arguments de démarrage, mais ne modifie pas les paramètres système réels.

## 📍 Notes internes MicroBean

- L'instance `Environment` est initialisée très tôt dans la phase de bootstrap (dans `Initializer.init()`).
- Le chargement des propriétés est piloté par `Initializer.manageConfigurationProperties()`.
- L'enregistrement en singleton garantit une source unique de vérité pour le contexte runtime.
- Les propriétés sont aplaties et indexées dans `flatProperties` pour un accès rapide.
- Cette approche simplifie les services transverses (logs, feature flags, diagnostics, configuration légère).

## 📚 Voir aussi

- [`@Service`](./Service.md) – stéréotype de service injectable
- [`@Bean`](./Bean.md) – composant géré par le conteneur
- [`@Adapter`](./Adapter.md) – composant d'adaptation technique
- [`@MicroBeanApplication`](./MicroBeanApplication.md) – configuration de l'application
- [`@Profile`](./Profile.md) – gestion du profil d'exécution
- [`@PostConstruct`](./PostConstruct.md) – initialisation après injection
