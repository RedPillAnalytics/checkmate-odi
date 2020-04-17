Checkmate for ODI
=========
Checkmate for ODI is a [Gradle Plugin](https://guides.gradle.org/designing-gradle-plugins/) and is available in the [Gradle plugin portal](https://plugins.gradle.org/plugin/com.redpillanalytics.checkmate.odi). It provides support in Oracle Data Integrator (ODI) for features such as source control integration, content versioning, automated regression and integration testing, and automated deployments.

```
Supported ODI versions:
- Data Integrator Version 12.2.1.4
- ODI SDK Build ODI_12.2.1.4.0OCIADWBP_GENERIC_200123.1539
```

Getting started with Checkmate for ODI
=========

###Checkmate Studio

A front-end desktop class application that works as Checkmate CLI’s interface designed to mostly import and export BI development artifacts.
Checkmate enables Continuous Delivery for products or platforms that don't naturally support it. This tool is also used on real multi-user development with git for source control management (SCM).

More: [Download Checkmate Studio](https://redpillanalytics.com/checkmate-studio-download)

###Checkmate ODI CLI

To use checkmate-odi gradle plugin create a `build.gradle` and `settings.gradle` in your source base folder with the following content:

####build.gradle
```
plugins {
  id "com.redpillanalytics.checkmate.odi" version "1.0.22"
}
```

####settings.gradle
```
pluginManagement {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
    jcenter()
    maven {
      url "http://maven.redpillanalytics.io.storage.googleapis.com/checkmate/"
    }
  }
}
```

####Checkmate ODI CLI Tasks

```
Checkmate ODI tasks
-------------------
export - Executes all configured 'export' tasks.
exportGlobalDir - Export global objects from the ODI repository into source control.
exportLoadPlanDir - Export one or more load plans from the ODI repository into source control.
exportModelDir - Export one or more models from the ODI repository into source control.
exportProjectDir - Run all project directory export tasks from ODI project 'TARGET-PROJECT'.
exportProjectFile - Export project 'TARGET-PROJECT' from the ODI repository to smart file 'TARGET-PROJECT.xml'.
exportScenarioDir - Export one or more scenarios from the ODI repository into source control.
exportTopologyDir - Export topology objects from the ODI repository into source control.
getOdiConnection - Test connection to ODI repository to validate connection parameters
import - Executes all configured 'import' tasks.
importGlobalDir - Import ODI global objects from source into the ODI repository.
importLoadPlanDir - Import ODI load plans from source into the ODI repository.
importModelDir - Import ODI models from source into the ODI repository.
importProjectDir - Import ODI project objects from source into the ODI repository.
importProjectFile - Import file 'TARGET-PROJECT.xml' into the ODI repository.
importScenarioDir - Import ODI load plans from source into the ODI repository.
importTopologyDir - Import ODI topology objects from source into the ODI repository.
waitForAgent - Wait until the ODI Agent is available.
```

Note: To run the checkmate-odi import tasks using cli, add Oracle Parser JVM property to command to avoid Smart Import issues.

```./gradlew -Djavax.xml.parsers.SAXParserFactory=oracle.xml.jaxp.JXSAXParserFactory -Si import```