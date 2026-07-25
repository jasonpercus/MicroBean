# 🔎 ClassScanner (infrastructure.scanner)

> 📘 Documentation technique orientée maintenance et évolution.

## 1) 🧭 Vue d’ensemble

`ClassScanner` est responsable du **scan des classes annotées** dans les packages applicatifs.
Il sert de passerelle entre la découverte technique (ClassGraph) et les règles métier de sélection des composants.

Responsabilités principales :

- 🧱 lancer un scan des packages déclarés ;
- 🏷️ cibler uniquement les annotations composants supportées ;
- 🚫 exclure les interfaces, classes abstraites et annotations ;
- ✅ appliquer une validation métier via `ScanningValidator` ;
- 🐞 tracer en debug les composants retenus.

En pratique, cette classe répond au besoin suivant : **fournir la liste finale des classes candidates à l’injection/traitement** tout en filtrant les faux positifs.

Fichier source : `src/main/java/com/jasonpercus/microbean/infrastructure/scanner/ClassScanner.java`

---

## 2) 🔗 Positionnement dans le flux MicroBean

`ClassScanner` est utilisée pendant l’initialisation du runtime, via `Initializer` :

1. `Initializer.getPackagesPathsToScan()`
2. `new ClassScanner(packages, args).searchAnnotatedClass()`
3. classes retenues transmises à `Processor.execute(...)`

Elle intervient donc entre la configuration de démarrage et le traitement IoC.

Références :

- `src/main/java/com/jasonpercus/microbean/infrastructure/run/Initializer.java`
- `src/main/java/com/jasonpercus/microbean/infrastructure/run/Processor.java`

### 🧵 Diagramme de séquence (vue d’ensemble)

```mermaid
sequenceDiagram
    autonumber
    participant Init as Initializer
    participant CS as ClassScanner
    participant CG as ClassGraph
    participant SV as ScanningValidator
    participant Log as LogHelper

    Init->>CS: searchAnnotatedClass()
    CS->>CG: scanPackages()
    CG-->>CS: ScanResult
    loop pour chaque annotation composant
        CS->>CS: getClassesWithAnnotation(...)
        CS->>CS: checkingClass(...) (non interface/abstraite/annotation)
        CS->>SV: new ScanningValidator(loaded, args)
        CS->>SV: invalidate()
        alt valide
            CS->>CS: add(loaded)
            CS->>Log: debug(DEBUG_MESSAGE_COMPONENT_ANNOTATED_FOUND,...)
        else invalide
            CS-->>CS: ignore la classe
        end
    end
    CS-->>Init: Set<Class<?>>
```

---

## 3) 💡 Idée fonctionnelle : à quoi répond cette classe

`ClassScanner` traite deux problèmes essentiels.

- **Découverte technique** : retrouver toutes les classes portant des annotations composants.
- **Sélection utile** : ne conserver que les classes réellement exploitables et autorisées par les règles métier.

Sans cette étape, le framework risquerait :

- d’essayer d’instancier des interfaces/abstraites,
- d’inclure des classes non compatibles avec le contexte (profil/condition/OS),
- et de polluer le pipeline de traitement aval.

---

## 4) 🧠 Comportement méthode par méthode

### `public Set<Class<?>> searchAnnotatedClass()`

- Point d’entrée principal.
- Crée un ensemble de sortie (`LinkedHashSet`).
- Ouvre `ScanResult` via try-with-resources.
- Délègue le filtrage à `filterScannedClass(...)`.
- Retourne l’ensemble final des classes retenues.

### `private ScanResult scanPackages(String[] packages)`

- Configure et exécute ClassGraph :
  - `enableClassInfo()`
  - `enableAnnotationInfo()`
  - `acceptPackages(packages)`
- Retourne le résultat brut du scan.

### `private List<? extends Class<? extends Annotation>> getAnnotationClassToScan()`

- Scanne le package `com.jasonpercus.microbean.api`.
- Conserve uniquement les annotations :
  - avec `@Retention(RUNTIME)`,
  - ciblant `ElementType.TYPE`.
- Exclut explicitement `@Condition`, `@MicroBeanApplication`, `@Primary`, `@Profile`.

### `private void filterScannedClass(ScanResult scanResult, Set<Class<?>> componentsClasses, Set<Class<?>> othersClasses)`

- Parcourt les annotations retournées par `getAnnotationClassToScan()`.
- Extrait et isole les classes annotées `@ModuleInit` de la map.
- Appelle `getOthersAnnotationsToKeep(moduleInitClassInfo)` pour obtenir les annotations à conserver.
- Pour chaque annotation restante :
  - récupère les classes annotées,
  - applique `checkingClass(...)`,
  - appelle `analyseAndPushAnnotatedClass(...)`.

### `private Set<Class<? extends Annotation>> getOthersAnnotationsToKeep(Set<ClassInfo> moduleInitClassInfo)`

Récupère la liste des annotations à conserver pour les classes invalidées annotées via `@ModuleInit`.

Comportement détaillé :

- Si `moduleInitClassInfo` est `null` → retourne un ensemble vide immédiatement.
- Pour chaque classe dans `moduleInitClassInfo` :
  - vérifie si elle implémente `IModuleInit` ;
  - si oui, instancie la classe via le constructeur sans argument ;
  - appelle `keepAnnotatedClassForContext(set)` pour enrichir l'ensemble des annotations ;
  - si le constructeur lève une exception : journalise l'erreur via `LogHelper.error(...)` et ignore le module.
- Retourne l'ensemble complet des annotations déclarées par tous les modules valides.

### `private void analyseAndPushAnnotatedClass(...)`

- Charge la classe (`classInfo.loadClass()`).
- Instancie `ScanningValidator` avec `args`.
- Si `validator.invalidate()` vaut `true` :
  - vérifie si la classe porte une annotation présente dans `annotationsSearchedToAddToOthersClasses` ;
  - si oui : l'ajoute à `otherClasses` ;
  - sinon : la classe est ignorée.
- Sinon : ajoute la classe à `componentClasses` et log en debug.

### `private static boolean filterRetentionAndTarget(Class<? extends Annotation> annotation)`

- Vérifie que l’annotation est conservée en `RetentionPolicy.RUNTIME`.
- Vérifie que `@Target` contient `ElementType.TYPE`.
- Retourne `false` si `@Retention` est absente / non runtime, ou si `@Target` est absent / incompatible.

### `private static boolean checkingClass(ClassInfo classInfo)`

- Vérifie que la classe n’est :
  - ni interface,
  - ni abstraite,
  - ni annotation.

### `private static boolean isNotInterface(...)`, `isNotAbstract(...)`, `isNotAnnotation(...)`

- Helpers de lisibilité pour les filtres techniques.

### 🌊 Diagramme de flux (filtrage complet)

```mermaid
flowchart TD
    A[searchAnnotatedClass] --> B["scanPackages(basePackages)"]
    B --> C[ScanResult]
    C --> C1[getAnnotationClassToScan]
    C1 --> D["Trier annotations — @ModuleInit en tête"]
    D --> E[filterScannedClass]
    E --> F["Extraire classes @ModuleInit"]
    F --> G["getOthersAnnotationsToKeep(moduleInitClassInfo)"]
    G --> G1{moduleInitClassInfo null ?}
    G1 -- Oui --> G2[Retourner ensemble vide]
    G1 -- Non --> G3["Pour chaque classe @ModuleInit"]
    G3 --> G4{implémente IModuleInit ?}
    G4 -- Non --> G5[Ignorer silencieusement]
    G4 -- Oui --> G6[Instancier + keepAnnotatedClassForContext]
    G6 --> G7{Exception constructeur ?}
    G7 -- Oui --> G8[LogHelper.error + ignorer]
    G7 -- Non --> G9[Ajouter annotations au set]
    G2 --> H
    G9 --> H
    H[Pour chaque annotation restante] --> I[getClassesWithAnnotation]
    I --> J{checkingClass ?}
    J -- Non --> K[Ignorer]
    J -- Oui --> L[loadClass]
    L --> M[ScanningValidator.invalidate]
    M -- False --> N[componentClasses.add + Log debug]
    M -- True --> O{porte une annotation du set IModuleInit ?}
    O -- Oui --> P[otherClasses.add]
    O -- Non --> K
    N --> H
    P --> H
    K --> H
    H --> Q["Retourner (componentClasses, otherClasses)"]
```

---

## 5) 📐 Contrats implicites importants (pour la maintenance)

- Les annotations scannées sont découvertes dynamiquement dans le package `api` et `infrastructure.api`.
- Seules les annotations `RUNTIME` ciblant `TYPE` sont retenues.
- `@Condition`, `@MicroBeanApplication`, `@Primary` et `@Profile` sont volontairement exclues de la détection composant.
- Les classes `@ModuleInit` sont toujours traitées **en priorité** dans la boucle de scan.
- Les classes `@ModuleInit` ne sont **jamais** ajoutées à `componentClasses` ni à `otherClasses`.
- Si une classe `@ModuleInit` n'implémente pas `IModuleInit`, elle est silencieusement ignorée.
- Si le constructeur d'un `IModuleInit` lève une exception, elle est absorbée et journalisée.
- Le filtrage technique (`checkingClass`) est appliqué **avant** la validation métier.
- Une classe invalidée par `ScanningValidator` ET portant une annotation déclarée par un `IModuleInit` va dans `otherClasses`.
- Une classe invalidée sans annotation du set va dans aucun ensemble (ignorée totalement).
- Le résultat `componentClasses` est un `Set` : absence de doublons attendue.

---

## 6) ⚠️ Risques lors des modifications

Si vous modifiez `ClassScanner`, vérifier en priorité :

1. **Ordre des filtres** : ne pas inverser filtre technique et validation métier.
2. **Découverte des annotations** : tout changement sur `getAnnotationClassToScan(...)` ou `filterRetentionAndTarget(...)` impacte la découverte.
3. **Priorité de traitement de `@ModuleInit`** : si le comparateur change, les annotations des modules ne seront pas connues à temps.
4. **`getOthersAnnotationsToKeep`** : si le `remove(ModuleInit...)` est supprimé, les classes `@ModuleInit` pourraient atterrir dans `componentClasses`.
5. **Performance** : le scan de packages larges peut coûter cher.
6. **Traçage debug** : garder des logs fidèles aux classes réellement retenues.
7. **Compatibilité aval** : `Processor` dépend de la qualité de la liste renvoyée dans `componentClasses`.

> 🛡️ Recommandation : toute évolution de cette classe doit être validée en unitaire (`ClassScannerTest`) et en Cucumber (`classscanner.feature`).

---

## 7) 🧪 Tests existants sur `ClassScanner`

### 7.1 ✅ Tests unitaires

Fichier : `src/test/java/com/jasonpercus/microbean/infrastructure/scanner/ClassScannerTest.java`

Cas couverts :

- scan nominal des classes annotées valides (`Service`, `Adapter`, `Configuration`, `EntryPointService`) ;
- exclusion des interfaces, classes abstraites et annotations ;
- résultat vide si aucun composant annoté ;
- exclusion d’une classe annotée invalidée par `ScanningValidator.invalidate() == true` ;
- couverture complète de `filterRetentionAndTarget(...)` :
  - retention absente,
  - retention non runtime,
  - target absent,
  - target sans `TYPE`,
  - target incluant `TYPE`.

**Tests dédiés à `getOthersAnnotationsToKeep` :**

| Test                                                                                                 | Scénario couvert                                      |
|------------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| `doit_retourner_un_ensemble_vide_quand_moduleInitClassInfo_est_null`                                 | paramètre `null` → ensemble vide retourné             |
| `doit_collecter_les_annotations_via_un_imoduleinit_valide`                                           | `ValidModuleInit` expose `@CustomComponentAnnotation` |
| `doit_ignorer_silencieusement_une_classe_moduleinit_sans_imoduleinit`                                | `ModuleInitWithoutIModuleInit` → silencieux           |
| `doit_absorber_l_exception_de_constructeur_d_un_imoduleinit_defaillant`                              | `FailingModuleInit` → exception absorbée              |
| `doit_placer_dans_otherClasses_une_classe_invalidee_portant_une_annotation_declaree_par_imoduleinit` | classe invalidée par profil → `otherClasses`          |

Fixtures utilisées :

- `src/test/java/com/jasonpercus/microbean/infrastructure/scanner/fixtures/valid/...`
- `src/test/java/com/jasonpercus/microbean/infrastructure/scanner/fixtures/excluded/...`
- `src/test/java/com/jasonpercus/microbean/infrastructure/scanner/fixtures/empty/...`
- `src/test/java/com/jasonpercus/microbean/infrastructure/scanner/fixtures/invalidated/...`
- `src/test/java/com/jasonpercus/microbean/infrastructure/scanner/fixtures/moduleinit/...` _(nouveau)_

### 7.2 🥒 Tests Cucumber (intégration comportementale)

Feature dédiée :

- `src/test/resources/com/jasonpercus/microbean/cucumber/classscanner.feature`

Scénarios couverts :

1. scan nominal des composants annotés valides ;
2. exclusion des interfaces/abstraites/annotations ;
3. exclusion d’une classe invalidée par `ScanningValidator`.

Ces scénarios valident le comportement observable du scan dans un contexte d’exécution réel (sorties structurées `SCANNED:*`).

Feature globale liée au flux complet :

- `src/test/resources/com/jasonpercus/microbean/cucumber/microbean.feature`

Elle confirme indirectement que le scan alimente correctement le pipeline nominal complet.

---

## 8) 🧰 Ce qu'un mainteneur doit retenir

- `ClassScanner` est la porte d'entrée de la découverte des composants.
- Deux filtres s'enchaînent : technique puis métier.
- Les classes `@ModuleInit` sont traitées en priorité pour alimenter le set `annotationsSearchedToAddToOthersClasses`.
- Une classe invalidée ET portant une annotation déclarée par un module finit dans `otherClasses` (pas ignorée).
- Le résultat `componentClasses` doit rester propre (classes concrètes, autorisées, sans doublon).
- La moindre régression ici impacte fortement `Processor` et le démarrage global.
- Les tests actuels couvrent à la fois le détail local et l'intégration comportementale.

---

## 9) 🗺️ Légende visuelle rapide

- ✅ Validation / précondition
- 🔎 Analyse / scan
- ⚠️ Point de vigilance
- 🧪 Couverture de tests
- 🛡️ Recommandation de fiabilité
