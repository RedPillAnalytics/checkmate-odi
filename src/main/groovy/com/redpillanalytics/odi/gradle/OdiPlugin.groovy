package com.redpillanalytics.odi.gradle

import com.redpillanalytics.common.GradleUtils
import com.redpillanalytics.odi.Instance
import com.redpillanalytics.odi.gradle.containers.BuildGroupContainer
import com.redpillanalytics.odi.gradle.tasks.CreateProjectTask
import com.redpillanalytics.odi.gradle.tasks.DeleteProjectTask
import com.redpillanalytics.odi.gradle.tasks.ExportLoadPlansAndScenariosTask
import com.redpillanalytics.odi.gradle.tasks.ExportModelFolderTask
import com.redpillanalytics.odi.gradle.tasks.ExportModelTask
import com.redpillanalytics.odi.gradle.tasks.ExportObjectsTask
import com.redpillanalytics.odi.gradle.tasks.ExportProjectFolderTask
import com.redpillanalytics.odi.gradle.tasks.GetLoadPlansAndScenariosTask
import com.redpillanalytics.odi.gradle.tasks.GetModelsTask
import com.redpillanalytics.odi.gradle.tasks.GetProjectsTask
import com.redpillanalytics.odi.gradle.tasks.ImportObjectTask
import com.redpillanalytics.odi.gradle.tasks.SmartExportAllTask
import com.redpillanalytics.odi.gradle.tasks.SmartExportTask
import com.redpillanalytics.odi.gradle.tasks.SmartImportAllTask
import com.redpillanalytics.odi.gradle.tasks.SmartImportTask
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project

@Slf4j
class OdiPlugin implements Plugin<Project> {

   void apply(Project project) {

      // apply Gradle built-in plugins
      project.apply plugin: 'base'

      project.configure(project) {
         extensions.create('odi', OdiPluginExtension)
      }

      // create configuration for JDBC
      //project.configurations { jdbc }

      // create the extension for the build group container
      // this facilitates creating multiple build groups
      // build groups are groups of similar tasks with slight configuration modifications
      project.odi.extensions.buildGroups = project.container(BuildGroupContainer)

      // we'll start with only a single build group
      project.extensions.odi.buildGroups.add(new BuildGroupContainer('default'))

      project.afterEvaluate {

         // set all 'obi.<property>' -P options to the 'obi' extension
         GradleUtils.setParameters(project,'odi')

         String defaultProjectName
         String defaultProjectCode
         String sourceBase = project.extensions.odi.sourceBase

         // get the taskGroup
         String taskGroup = project.extensions.odi.taskGroup

         // TargetFolder variable to exportProjectFolder, that exports the objects contained in a specified folder on a project
         String folderName = project.extensions.odi.folderName

         // Model Folder Name to find and Export
         String modelFolder = project.extensions.odi.modelFolderName

         // Model Folder Name to find and Export
         String modCode = project.extensions.odi.modelCode

         // see if there's an explicit project name
         if (project.extensions.odi.projectName) {

            // use this throughout for the projectName
            defaultProjectName = project.extensions.odi.projectName
            // also set our archive name to projectName
            project.archivesBaseName = defaultProjectName

         } else {
            // we don't have a projectName so we need one
            // just use the default archivesBaseName
            defaultProjectName = project.archivesBaseName
         }

         // if no project code is specified, create one
         defaultProjectCode = project.extensions.odi.projectCode ?: project.extensions.odi.getProjectCode(defaultProjectName)

         log.warn "defaultProjectCode: $defaultProjectCode"
         log.warn "defaultProjectName: $defaultProjectName"

         // capture all the connection parameters
         def masterUrl = project.extensions.odi.masterUrl
         log.debug "masterUrl: $masterUrl"
         def masterDriver = project.extensions.odi.masterDriver
         log.debug "masterDriver: $masterDriver"
         def masterRepo = project.extensions.odi.masterRepo
         log.debug "masterRepo: $masterRepo"
         def workRepo = project.extensions.odi.workRepo
         log.debug "workRepo: $workRepo"
         def masterPassword = project.extensions.odi.masterPassword
         log.debug "masterPassword: $masterPassword"
         def odiUser = project.extensions.odi.odiUser
         log.debug "odiUser: $odiUser"
         def odiPassword = project.extensions.odi.odiPassword
         log.debug "odiPassword: $odiPassword"

         // What's our content policy... multiple objects, or a single file
         def contentPolicy = project.extensions.odi.contentPolicy
         log.debug "contentPolicy: $contentPolicy"

//         // Let's JIT load the JDBC driver
//         URLClassLoader loader = GroovyObject.class.classLoader
//         project.configurations.jdbc.each { File file ->
//            log.warn "jdbc driver JAR: $file"
//            loader.addURL(file.toURI().toURL())
//            DriverManager.registerDriver(loader.loadClass(masterDriver).newInstance())
//         }

         // let's go ahead and get an Instance object, but unconnected.
         def odiInstance = new Instance(masterUrl, masterDriver, masterRepo, workRepo, masterPassword, odiUser, odiPassword)

         // configure all build groups
         project.odi.buildGroups.all { bg ->

            if (project.extensions.odi.isDevelopment()) {

               // Task that executes the smart import of a project
               project.task(bg.getTaskName('importObject'), type: ImportObjectTask) {

                  group taskGroup
                  description "Executes a Regular Import of a XML Object to the ODI Instance."
                  instance odiInstance
                  sourcePath defaultProjectName
               }

               // Task that executes the smart import of a project
               project.task(bg.getTaskName('importObjectXML'), type: SmartImportTask) {

                  group taskGroup
                  description "Executes a Smart Import of a XML Object to the ODI Instance."
                  instance odiInstance
                  sourcePath defaultProjectName
               }

               // Task that executes the smart import of a project
               project.task(bg.getTaskName('importAllObjectsXML'), type: SmartImportAllTask) {

                  group taskGroup
                  description "Executes a Smart Import of all the XML Files from a Source Path to the ODI Instance."
                  instance odiInstance
                  sourcePath sourceBase

               }

               // Task that creates a project
               project.task(bg.getTaskName('createProject'), type: CreateProjectTask) {

                  group taskGroup
                  description = "Create a new project in the ODI Instance."
                  projectCode defaultProjectCode
                  projectName defaultProjectName
                  instance odiInstance

               }

               // Task that creates a project
               project.task(bg.getTaskName('deleteProject'), type: DeleteProjectTask) {

                  group taskGroup
                  description = "Delete a new project in the ODI Instance."
                  projectCode defaultProjectCode
                  instance odiInstance

               }

               // Task that executes the smart export of a project
               project.task(bg.getTaskName('exportProject'), type: SmartExportTask) {

                  group 'project'
                  description = "Executes a Smart Export of a project in the ODI Instance."
                  sourcePath sourceBase
                  projectCode defaultProjectCode
                  instance odiInstance

               }

               // Task that executes the export of the objects of a project, one file per object
               project.task(bg.getTaskName('exportProjectObjects'), type: ExportObjectsTask) {

                  group taskGroup
                  description = "Executes a Export of the objects of a project, one file per object, in the ODI Instance."
                  sourcePath sourceBase
                  projectCode defaultProjectCode
                  instance odiInstance

               }

               // Task that executes the smart export of all code from a folder in the target project
               project.task(bg.getTaskName('exportProjectFolder'), type: ExportProjectFolderTask) {

                  group taskGroup
                  description = "Executes a Smart Export of the objects in a specified folder in the ODI Instance."
                  sourcePath sourceBase
                  projectCode defaultProjectCode
                  folder folderName
                  instance odiInstance
               }

               // Task that executes the smart export of a project
               project.task(bg.getTaskName('exportAllProjects'), type: SmartExportAllTask) {

                  group taskGroup
                  description = "Executes a Smart Export of All Projects in the ODI Instance."
                  instance odiInstance
                  sourcePath sourceBase

               }

               // Task that get all the existing projects in the Repository
               project.task(bg.getTaskName('getProjects'), type: GetProjectsTask) {

                  group taskGroup
                  description = "Get all the projects existing in the ODI Instance."
                  instance odiInstance
               }

               // Task that get all the existing models in the Repository
               project.task(bg.getTaskName('getModels'), type: GetModelsTask) {

                  group taskGroup
                  description = "Get all the Models existing in the ODI Instance."
                  instance odiInstance
               }

               // Task that exports the Model Folders by Name in the Repository
               project.task(bg.getTaskName('exportModelFolder'), type: ExportModelFolderTask) {

                  group taskGroup
                  description = "Export the Model Folder with the target name in the ODI Instance."
                  instance odiInstance
                  sourcePath sourceBase
                  modelFolderName modelFolder

               }

               // Task that exports a Model find by Model Code
               project.task(bg.getTaskName('exportModel'), type: ExportModelTask) {

                  group taskGroup
                  description = "Export the Model with the target model code in the ODI Instance."
                  instance odiInstance
                  sourcePath sourceBase
                  modelCode modCode

               }

               // Task that get All the Load Plans and Scenarios existing on the ODI Repository
               project.task(bg.getTaskName('getLoadPlansAndScenarios'), type: GetLoadPlansAndScenariosTask) {

                  group taskGroup
                  description = "Get all the Load Plans and Scenarios in the ODI Instance."
                  instance odiInstance

               }

               // Task that executes the export of all the Load Plans and Scenarios by Project Folder
               project.task(bg.getTaskName('exportLoadPlansAndScenarios'), type: ExportLoadPlansAndScenariosTask) {

                  group 'project'
                  description = "Executes a Export of all the Load Plans and Scenarios by Project Folder"
                  sourcePath sourceBase
                  projectCode defaultProjectCode
                  folder folderName
                  instance odiInstance
               }
            }
         }
      }
   }
}
