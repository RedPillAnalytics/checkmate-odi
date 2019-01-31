package com.redpillanalytics.odi.gradle

import com.redpillanalytics.common.GradleUtils
import com.redpillanalytics.odi.Instance
import com.redpillanalytics.odi.gradle.containers.BuildGroupContainer
import com.redpillanalytics.odi.gradle.tasks.CreateProjectTask
import com.redpillanalytics.odi.gradle.tasks.DeleteProjectTask
import com.redpillanalytics.odi.gradle.tasks.ExportLoadPlanDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ExportModelDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ExportProjectDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ExportProjectFileTask
import com.redpillanalytics.odi.gradle.tasks.ImportDirectoryTask
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
         GradleUtils.setParameters(project, 'odi')

         // get the taskGroup
         String taskGroup = project.extensions.odi.taskGroup

         // TargetFolder variable to exportProjectFolder, that exports the objects contained in a specified folder on a project
         String projectFolder = project.extensions.odi.projectFolder

         // if no project code is specified, create one
         String defaultProjectName
         String defaultProjectCode

         if (project.extensions.odi.enableProjects) {

            assert "'odi.projectName' is a required property." && project.extensions.odi.projectName
            defaultProjectName = project.extensions.odi.projectName
            defaultProjectCode = project.extensions.odi.projectCode ?: project.extensions.odi.getProjectCode(defaultProjectName)
         }

         log.debug "defaultProjectCode: $defaultProjectCode"
         log.debug "defaultProjectName: $defaultProjectName"

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

         // assertions
         assert ['dir', 'file'].contains(contentPolicy)

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
                  projectCode defaultProjectCode
                  projectName defaultProjectName
                  instance odiInstance
                  description = "Create project name '${defaultProjectName}' with project code '${defaultProjectCode}' in the ODI repositorty."
               }

               // Task that deletes a project
               project.task(bg.getTaskName('deleteProject'), type: DeleteProjectTask) {

                  group taskGroup
                  description = "Delete project code '${defaultProjectCode}' from the ODI repositorty."
                  projectCode defaultProjectCode
                  instance odiInstance
               }

               // Task that deletes a project
//               project.task(bg.getTaskName('deleteModels'), type: DeleteModelsTask) {
//
//                  group taskGroup
//                  description = "Delete one or more models from the ODI repository."
//                  instance odiInstance
//               }

               project.task(bg.getTaskName('importProjectFile'), type: ImportProjectFileTask) {

                  group taskGroup
                  description "Import file '${sourceXml}' into the ODI repository."
                  projectCode defaultProjectCode
                  instance odiInstance
               }

               project.task(bg.getTaskName('importProjectDir'), type: ImportProjectDirectoryTask) {

                  group taskGroup
                  description "Import ODI project objects from source into the ODI repository."
                  instance odiInstance
                  category 'project'
               }

               project.task(bg.getTaskName('importModelDir'), type: ImportDirectoryTask) {

                  group taskGroup
                  description "Import ODI models from source into the ODI repository."
                  instance odiInstance
                  category 'model'
               }

               project.task(bg.getTaskName('importLoadPlanDir'), type: ImportDirectoryTask) {

                  group taskGroup
                  description "Import ODI load plans from source into the ODI repository."
                  instance odiInstance
                  category 'load-plan'
               }

               // Task that executes the smart export of a project
               project.task(bg.getTaskName('exportProjectFile'), type: ExportProjectFileTask) {

                  group taskGroup
                  description "Export project '${defaultProjectCode}' from the ODI repository to smart file '${sourceXml}'."
                  projectCode defaultProjectCode
                  instance odiInstance
                  outputs.upToDateWhen { false }
               }

               project.task(bg.getTaskName('exportProjectDir'), type: ExportProjectDirectoryTask) {

                  group taskGroup
                  description """Run all project directory export tasks from ODI project '${defaultProjectCode}'${
                     projectFolder ? " for folder '$projectFolder'" : ''
                  }."""
                  projectCode defaultProjectCode
                  folderName projectFolder
                  instance odiInstance
                  outputs.upToDateWhen { false }
               }

               project.task(bg.getTaskName('exportModelDir'), type: ExportModelDirectoryTask) {

                  group taskGroup
                  description "Export one or more models from the ODI repository into source control."
                  instance odiInstance
                  outputs.upToDateWhen { false }
               }

               project.task(bg.getTaskName('exportLoadPlanDir'), type: ExportLoadPlanDirectoryTask) {

                  group taskGroup
                  description "Export one or more load plans from the ODI repository into source control."
                  instance odiInstance
                  outputs.upToDateWhen { false }
               }

//               // Task that exports the Model Folders by Name in the Repository
//               project.task(bg.getTaskName('exportWorkRepo'), type: ExportWorkRepoTask) {
//
//                  group taskGroup
//                  description = "Traditional export of all items in the ODI repository work repository."
//                  instance odiInstance
//                  //sourceDir projectSource
//               }
//
//               // Task that exports the Model Folders by Name in the Repository
//               project.task(bg.getTaskName('importWorkRepo'), type: ImportWorkRepoTask) {
//
//                  group taskGroup
//                  description "Traditional import of all work items in the ODI repository."
//                  instance odiInstance
//                  sourceDir odiSource
//                  category 'model'
//               }

//               // Task that executes the export of all the Load Plans and Scenarios by Project Folder
//               project.task(bg.getTaskName('exportLoadPlansAndScenarios'), type: ExportLoadPlansAndScenariosTask) {
//
//                  group taskGroup
//                  description = "Executes a Export of all the Load Plans and Scenarios by Project Folder"
//                  sourcePath sourceBase
//                  projectCode defaultProjectCode
//                  folder projectFolder
//                  instance odiInstance
//               }

               project.task(bg.getTaskName('export')) {
                  group taskGroup
                  description = "Executes all configured 'export' tasks."
               }

               project.task(bg.getTaskName('import')) {
                  group taskGroup
                  description = "Executes all configured 'import' tasks."
               }

               if (project.extensions.odi.enableProjects) {
                  if (contentPolicy == 'dir') {
                     project."${bg.getTaskName('import')}".dependsOn project."${bg.getTaskName('importProjectDir')}"
                     project."${bg.getTaskName('export')}".dependsOn project."${bg.getTaskName('exportProjectDir')}"
                  } else if (contentPolicy == 'file') {
                     project."${bg.getTaskName('import')}".dependsOn project."${bg.getTaskName('importProjectFile')}"
                     project."${bg.getTaskName('export')}".dependsOn project."${bg.getTaskName('exportProjectFile')}"
                  }
               }

               if (project.extensions.odi.enableModels) {
                  if (contentPolicy == 'dir') {
                     project."${bg.getTaskName('import')}".dependsOn project."${bg.getTaskName('importModelDir')}"
                     project."${bg.getTaskName('export')}".dependsOn project."${bg.getTaskName('exportModelDir')}"
                  }
               }

               if (project.extensions.odi.enableLoadPlans) {
                  project."${bg.getTaskName('import')}".dependsOn project."${bg.getTaskName('importLoadPlanDir')}"
                  project."${bg.getTaskName('export')}".dependsOn project."${bg.getTaskName('exportLoadPlanDir')}"

               }
            }
         }
      }
   }
}
