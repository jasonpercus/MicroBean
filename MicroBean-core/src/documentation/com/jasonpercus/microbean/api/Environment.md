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

| Méthode                                 | Retour                | Rôle                                                                     |
|-----------------------------------------|-----------------------|--------------------------------------------------------------------------|
| `Environment(String[] args)`            | -                     | Construit l'environnement à partir des arguments de démarrage            |
| `getArguments()`                        | `Arguments`           | Retourne l'objet de gestion des arguments                                |
| `getProfile()`                          | `String`              | Retourne le profil actif (ou `null` s'il n'est pas défini)               |
| `getProperties(Class<T> type)`          | `T`                   | Mappe les propriétés imbriquées sur un POJO via Jackson (kebab-case)     |
| `getProperties()`                       | `Map<String, Object>` | Retourne la map **imbriquée** brute des propriétés (structure YAML/JSON) |
| `getFlatProperties()`                   | `Map<String, Object>` | Retourne la map **aplatie** des propriétés (notation pointée)            |
| `getProperty(String key)`               | `Object`              | Retourne une valeur depuis les propriétés aplaties (`null` si absente)   |
| `putProperty(String key, Object value)` | -                     | Ajoute/surcharge une propriété dans la map aplatie                       |
| `putProperties(Map)`                    | -                     | Fusionne récursivement un ensemble de propriétés imbriquées              |

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
- **`getProperties()`** retourne la map imbriquée brute (structure YAML/JSON intacte). Elle est **mutable** (référence directe).
- **`getFlatProperties()`** retourne la map aplatie en notation pointée. Elle est **mutable** (référence directe).
- **`getProperty(String key)`** lit **uniquement dans `flatProperties`** ; les propriétés imbriquées dans `properties` ne sont pas accessibles directement via cette méthode.
- **`getProperty(null)`** retourne `null` sans exception.
- `putProperties(null)` et `putProperties(emptyMap)` sont silencieusement ignorés.
- `Arguments` expose une vue logique des arguments de démarrage, mais ne modifie pas les paramètres système réels.

## 🧪 Tests existants sur `Environment`

Fichier : `src/test/java/com/jasonpercus/microbean/api/EnvironmentTest.java`

### `getProperties(Class<T> type)`

| Test                                                                                | Scénario couvert                                 |
|-------------------------------------------------------------------------------------|--------------------------------------------------|
| `doit_mapper_les_proprietes_kebab_case_vers_un_pojo`                                | propriétés plates kebab-case → champs POJO       |
| `doit_mapper_les_proprietes_imbriquees_vers_un_pojo`                                | propriétés imbriquées → POJO hiérarchique        |
| `doit_retourner_un_objet_avec_valeurs_par_defaut_si_aucune_propriete_n_est_definie` | pas de propriétés → valeurs par défaut           |
| `doit_echouer_si_le_type_cible_est_null`                                            | `type = null` → `IllegalArgumentException`       |
| `doit_ignorer_les_cles_inconnues_lors_du_mapping`                                   | clé inconnue → silencieusement ignorée           |
| `doit_fusionner_recursivement_les_proprietes_imbriquees`                            | fusion récursive → last-writer-wins sur feuilles |

### `getProperties()`

| Test                                                                                  | Scénario couvert                          |
|---------------------------------------------------------------------------------------|-------------------------------------------|
| `doit_retourner_une_map_vide_pour_getproperties_quand_aucune_propriete_n_est_chargee` | map vide initiale                         |
| `doit_retourner_les_proprietes_brutes_apres_putproperties`                            | map imbriquée brute après `putProperties` |
| `doit_retourner_la_meme_reference_de_map_a_chaque_appel_de_getproperties`             | référence stable (même instance)          |
| `doit_ignorer_putproperties_null_sans_modifier_les_proprietes_existantes`             | `putProperties(null)` → no-op             |
| `doit_ignorer_putproperties_vide_sans_modifier_les_proprietes_existantes`             | `putProperties({})` → no-op               |

### `getFlatProperties()`

| Test                                                                                      | Scénario couvert                 |
|-------------------------------------------------------------------------------------------|----------------------------------|
| `doit_retourner_une_map_vide_pour_getflatproperties_quand_aucune_propriete_n_est_definie` | map vide initiale                |
| `doit_retourner_les_proprietes_plates_apres_putproperty`                                  | clés plates après `putProperty`  |
| `doit_retourner_la_meme_reference_de_map_a_chaque_appel_de_getflatproperties`             | référence stable (même instance) |
| `doit_accumuler_les_proprietes_plates_lors_de_plusieurs_appels_putproperty`               | accumulation correcte            |
| `doit_ecraser_la_valeur_existante_lors_de_putproperty_sur_une_cle_deja_presente`          | last-writer-wins                 |

### `getProperty(String key)`

| Test                                                                            | Scénario couvert                              |
|---------------------------------------------------------------------------------|-----------------------------------------------|
| `doit_retourner_null_pour_getproperty_quand_la_cle_est_inconnue`                | clé inexistante → `null`                      |
| `doit_retourner_la_valeur_correcte_pour_getproperty_apres_putproperty`          | valeur présente via `putProperty`             |
| `doit_retourner_null_pour_getproperty_avec_une_cle_null`                        | `key = null` → `null`                         |
| `doit_retourner_la_derniere_valeur_apres_plusieurs_putproperty_sur_la_meme_cle` | écrasement par dernière valeur                |
| `doit_acceder_aux_cles_plates_injectees_manuellement_via_getflatproperties`     | cohérence `getProperty` ↔ `getFlatProperties` |

---

## 📍 Notes internes MicroBean

- L'instance `Environment` est initialisée très tôt dans la phase de bootstrap (dans `Initializer.init()`).
- Le chargement des propriétés est piloté par `Initializer.manageConfigurationProperties()`.
- L'enregistrement en singleton garantit une source unique de vérité pour le contexte runtime.
- Les propriétés imbriquées (brutes) sont stockées dans `properties` ; les propriétés aplaties sont dans `flatProperties`.
- `getProperty(key)` lit dans `flatProperties` ; `getProperties(Class<T>)` lit dans `properties`.
- Cette dualité permet à la fois un accès rapide par clé plate et un mapping structuré sur un POJO.
- Cette approche simplifie les services transverses (logs, feature flags, diagnostics, configuration légère).

## 📚 Voir aussi

- [`@Service`](./Service.md) – stéréotype de service injectable
- [`@Bean`](./Bean.md) – composant géré par le conteneur
- [`@Adapter`](./Adapter.md) – composant d'adaptation technique
- [`@MicroBeanApplication`](./MicroBeanApplication.md) – configuration de l'application
- [`@Profile`](./Profile.md) – gestion du profil d'exécution
- [`@PostConstruct`](./PostConstruct.md) – initialisation après injection
