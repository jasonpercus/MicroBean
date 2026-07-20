# 📄 @MicroBeanApplication [🧩 Annotation]

## 🎯 Description

L'annotation `@MicroBeanApplication` permet de déclarer la classe principale d'une application utilisant le framework MicroBean. Elle configure le comportement du framework, notamment : les packages à scanner pour la détection automatique des composants (beans, services, etc.), l'affichage d'une bannière de démarrage, et la ressource à utiliser pour cette bannière. Elle doit être placée sur la classe principale de l'application (souvent celle contenant la méthode `main`).

## 🧠 Rôle dans l'architecture

`@MicroBeanApplication` structure le point d'entrée de l'application et la configuration globale du conteneur. Elle permet au framework de déterminer dynamiquement les packages à scanner, d'afficher une bannière personnalisée et d'initialiser le contexte d'injection. Elle s'intègre avec le système de scan, d'enregistrement des composants et de gestion du cycle de vie applicatif.

## 🔗 Relations

- Utilisé par :
  - Le conteneur MicroBean pour l'initialisation de l'application et le scan des composants.
- Concepts liés :
  - [`@EntryPointService`](./EntryPointService.md) : déclaration des points d'entrée applicatifs.
  - [`@Bean`](./Bean.md), [`@Service`](./Service.md), [`@Adapter`](./Adapter.md) : composants détectés lors du scan.

## ⚙️ Attributs

| Nom                     | Type     | Valeur par défaut | Rôle / Impact à l'exécution                                                                  |
|-------------------------|----------|-------------------|----------------------------------------------------------------------------------------------|
| scanPackages            | String[] | {}                | Liste des packages à scanner pour la détection automatique des composants                    |
| showBanner              | boolean  | true              | Indique si la bannière de démarrage doit être affichée                                       |
| bannerResource          | String   | "banner.txt"      | Nom de la ressource à utiliser pour la bannière de démarrage                                 |
| configurationProperties | String[] | {}                | Liste des chemins de fichiers de propriétés à charger pour la configuration de l'application |

## 💡 Exemples d'utilisation

```java
@MicroBeanApplication(scanPackages = {"com.example.app", "com.example.lib"})
public class Application {
    
    public static void main(String[] args) {
        // ...
    }
    
}
```

---

```java
@MicroBeanApplication(showBanner = false)
public class Application {
    
    public static void main(String[] args) {
        // ...
    }
    
}
```

---

```java
@MicroBeanApplication(bannerResource = "custom-banner.txt")
public class Application {
    
    public static void main(String[] args) {
        // ...
    }
    
}
```

---

```java
@MicroBeanApplication(configurationProperties = {"application.yaml", "database.json"})
public class Application {
    
    public static void main(String[] args) {
        // ...
    }
    
}
```

---

```java
@MicroBeanApplication(
    scanPackages = {"com.example.app", "com.example.lib"},
    showBanner = true,
    bannerResource = "custom-banner.txt", 
    configurationProperties = {"application.yaml", "database.json"}
)
public class Application {
    
    public static void main(String[] args) {
        // Point d'entrée de l'application
    }
    
}
```

## 🔄 Comportement du cycle de vie

1. Découverte de l'annotation sur la classe principale au démarrage de l'application.
2. Scan des packages spécifiés pour détecter et enregistrer les composants.
3. Affichage de la bannière de démarrage si `showBanner` est à `true`.
4. Chargement des fichiers de configuration spécifiés dans `configurationProperties`.
5. Initialisation du contexte d'injection et démarrage de l'application.

## ⚠️ Limitations / cas particuliers

- Si `scanPackages` est vide, le package de la classe annotée est utilisé par défaut.
- La ressource de bannière doit être présente dans le classpath.
- L'annotation ne gère pas la configuration avancée du contexte (profils, conditions, etc.).

## 📍 Notes internes MicroBean

- L'annotation `@MicroBeanApplication` favorise la centralisation de la configuration globale et la simplicité du bootstrap applicatif.
- Elle permet une extension du conteneur pour supporter des stratégies avancées de scan et d'initialisation.
- L'intégration avec les autres annotations de stéréotype assure une cohérence dans la gestion du cycle de vie des composants.

## 📚 Voir aussi

- [`@EntryPointService`](./EntryPointService.md) – déclaration des points d'entrée applicatifs
- [`@Bean`](./Bean.md) – déclaration de méthodes de production de beans
- [`@Service`](./Service.md) – stéréotype pour les services métier
- [`@Adapter`](./Adapter.md) – stéréotype pour les adaptateurs techniques
