package com.redpillanalytics.odi.gradle

import com.redpillanalytics.common.GradleUtils
import com.redpillanalytics.odi.Instance
import com.redpillanalytics.odi.gradle.containers.BuildGroupContainer
import com.redpillanalytics.odi.gradle.tasks.CreateProjectTask
import com.redpillanalytics.odi.gradle.tasks.DeleteProjectTask
import com.redpillanalytics.odi.gradle.tasks.ExportLoadPlansAndScenariosTask
import com.redpillanalytics.odi.gradle.tasks.ExportModelFolderTask
import com.redpillanalytics.odi.gradle.tasks.ExportModelTask
import com.redpillanalytics.odi.gradle.tasks.ExportProjectDirectoryTask

import com.redpillanalytics.odi.gradle.tasks.GetLoadPlansAndScenariosTask
import com.redpillanalytics.odi.gradle.tasks.ExportProjectFileTask
import com.redpillanalytics.odi.gradle.tasks.ImportProjectDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ImportProjectFileTask
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

         // default export file name
         String sourceXml = "${defaultProjectCode}.xml"

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

               // Task that creates a project
               project.task(bg.getTaskName('createProject'), type: CreateProjectTask) {

                  group taskGroup
                  description = "Create project name '${defaultProjectName}' with project code '${defaultProjectCode}' in the ODI repositorty."
                  projectCode defaultProjectCode
                  projectName defaultProjectName
                  instance odiInstance
               }

               // Task that deletes a project
               project.task(bg.getTaskName('deleteProject'), type: DeleteProjectTask) {

                  group taskGroup
                  description = "Delete project code '${defaultProjectCode}' from the ODI repositorty."
                  projectCode defaultProjectCode
                  instance odiInstance
               }

               project.task(bg.getTaskName('importProjectFile'), type: ImportProjectFileTask) {

                  group taskGroup
                  description "Import smart file '${sourceXml}' into the ODI repository."
                  instance odiInstance
                  sourceFile sourceXml
               }

               project.task(bg.getTaskName('importProjectDir'), type: ImportProjectDirectoryTask) {

                  group taskGroup
                  description "Import smart files from directory '${sourceBase}' into the ODI repository."
                  instance odiInstance
                  sourceDir sourceBase
               }

               // Task that executes the smart export of a project
               project.task(bg.getTaskName('exportProjectFile'), type: ExportProjectFileTask) {

                  group taskGroup
                  description "Export project '${defaultProjectCode}' from the ODI repository to smart file '${sourceXml}'."
                  sourceFile sourceXml
                  projectCode defaultProjectCode
                  instance odiInstance
               }

               // Task that executes the export of the objects of a project, one file per object
               project.task(bg.getTaskName('exportProjectDir'), type: ExportProjectDirectoryTask) {

                  group taskGroup
                  description "Export project '${defaultProjectCode}' from the ODI repository into directory '${sourceBase}' with a single object per file."
                  sourceDir sourceBase
                  projectCode defaultProjectCode
                  instance odiInstance
               }

               // Task that exports the Model Folders by Name in the Repository
               project.task(bg.getTaskName('exportModelFolder'), type: ExportModelFolderTask) {

                  group taskGroup
                  description = "Export the Model Folder with the target name in the ODI Instance."
                  instance odiInstance
                  sourceDir sourceBase
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

                  group taskGroup
                  description = "Executes a Export of all the Load Plans and Scenarios by Project Folder"
                  sourcePath sourceBase
                  projectCode defaultProjectCode
                  folder folderName
                  instance odiInstance
               }

               project.task(bg.getTaskName('export')) {
                  group taskGroup
                  description = "Executes all configured 'export' tasks."
               }

               project.task(bg.getTaskName('import')) {
                  group taskGroup
                  description = "Executes all configured 'import' tasks."
               }
            }
         }
      }
   }
}
