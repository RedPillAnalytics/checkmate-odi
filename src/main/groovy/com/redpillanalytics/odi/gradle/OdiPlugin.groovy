package com.redpillanalytics.odi.gradle

import com.redpillanalytics.odi.gradle.tasks.ExportGlobalDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ExportTopologyDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ImportGlobalDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ImportLoadPlanDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ImportModelDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ImportScenarioDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ImportTopologyDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.WaitForAgentTask
import com.redpillanalytics.odi.odi.Instance
import com.redpillanalytics.odi.gradle.containers.BuildGroupContainer
import com.redpillanalytics.odi.gradle.tasks.CreateProjectTask
import com.redpillanalytics.odi.gradle.tasks.DeleteProjectTask
import com.redpillanalytics.odi.gradle.tasks.ExportLoadPlanDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ExportModelDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ExportProjectDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.ExportProjectFileTask
import com.redpillanalytics.odi.gradle.tasks.ExportScenarioDirectoryTask
import com.redpillanalytics.odi.gradle.tasks.GetOdiConnectionTask
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
      project.apply plugin: 'com.redpillanalytics.gradle-properties'

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

      // add ODI API plugin
//      if (project.plugins.findPlugin('com.redpillanalytics.checkmate.odi.api')) {
//         // add libsDir dependency
//         project.buildscript.dependencies.add('classpath', project.fileTree(dir: project.file(project.extensions.odiApi.libsDir), include: '*.jar'))
//      }

      project.afterEvaluate {

         // Go look for any -P properties that have "odi." in them
         // If so... update the extension value
         project.pluginProps.setParameters(project, 'odi')

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

         // let's go ahead and get an Instance object, but unconnected.
         def odiInstance = new Instance(masterUrl, masterDriver, masterRepo, workRepo, masterPassword, odiUser, odiPassword)

         // configure all build groups
         project.odi.buildGroups.all { bg ->

            if (project.extensions.odi.isDevelopment()) {

               // Task that conect to the ODI Repository to Validate Parameters
               project.task(bg.getTaskName('getOdiConnection'), type: GetOdiConnectionTask) {

                  group taskGroup
                  description = "Test connection to ODI repository to validate connection parameters"
                  instance odiInstance
               }

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


               project.task(bg.getTaskName('importTopologyDir'), type: ImportTopologyDirectoryTask) {

                  group taskGroup
                  description "Import ODI topology objects from source into the ODI repository."
                  instance odiInstance
                  category 'topology'
               }

               project.task(bg.getTaskName('importModelDir'), type: ImportModelDirectoryTask) {

                  group taskGroup
                  description "Import ODI models from source into the ODI repository."
                  instance odiInstance
                  category 'model'
               }

               project.task(bg.getTaskName('importLoadPlanDir'), type: ImportLoadPlanDirectoryTask) {

                  group taskGroup
                  description "Import ODI load plans from source into the ODI repository."
                  instance odiInstance
                  category 'load-plan'
               }

               project.task(bg.getTaskName('importScenarioDir'), type: ImportScenarioDirectoryTask) {

                  group taskGroup
                  description "Import ODI load plans from source into the ODI repository."
                  instance odiInstance
                  category 'scenario'
               }

               project.task(bg.getTaskName('importGlobalDir'), type: ImportGlobalDirectoryTask) {

                  group taskGroup
                  description "Import ODI global objects from source into the ODI repository."
                  instance odiInstance
                  category 'global'
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

               project.task(bg.getTaskName('exportScenarioDir'), type: ExportScenarioDirectoryTask) {

                  group taskGroup
                  description "Export one or more scenarios from the ODI repository into source control."
                  instance odiInstance
                  outputs.upToDateWhen { false }
               }

               project.task(bg.getTaskName('exportGlobalDir'), type: ExportGlobalDirectoryTask) {

                  group taskGroup
                  description "Export global objects from the ODI repository into source control."
                  instance odiInstance
                  outputs.upToDateWhen { false }
               }

               project.task(bg.getTaskName('exportTopologyDir'), type: ExportTopologyDirectoryTask) {

                  group taskGroup
                  description "Export topology objects from the ODI repository into source control."
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

               // Task that deletes a model
//               project.task(bg.getTaskName('deleteModels'), type: DeleteModelsTask) {
//
//                  group taskGroup
//                  description = "Delete one or more models from the ODI repository."
//                  instance odiInstance
//               }

//               // Task that creates a project
//               project.task(bg.getTaskName('createProject'), type: CreateProjectTask) {
//
//                  group taskGroup
//                  projectCode defaultProjectCode
//                  projectName defaultProjectName
//                  instance odiInstance
//                  description = "Create project name '${defaultProjectName}' with project code '${defaultProjectCode}' in the ODI repositorty."
//               }
//
//               // Task that deletes a project
//               project.task(bg.getTaskName('deleteProject'), type: DeleteProjectTask) {
//
//                  group taskGroup
//                  description = "Delete project code '${defaultProjectCode}' from the ODI repositorty."
//                  projectCode defaultProjectCode
//                  instance odiInstance
//               }

               project.task(bg.getTaskName('export')) {
                  group taskGroup
                  description = "Executes all configured 'export' tasks."
               }

               project.task(bg.getTaskName('import')) {
                  group taskGroup
                  description "Executes all configured 'import' tasks."
               }

               project.task('waitForAgent', type: WaitForAgentTask) {
                  agentUrl project.extensions.odi.agentUrl
               }

               // Add Export/Import task Dependency Level
               if (project.extensions.odi.enableProjects) {
                  if (contentPolicy == 'dir') {
                     project."${bg.getTaskName('import')}".dependsOn project."${bg.getTaskName('importProjectDir')}"
                     project."${bg.getTaskName('importProjectDir')}".mustRunAfter project."${bg.getTaskName('importModelDir')}"
                     project."${bg.getTaskName('export')}".dependsOn project."${bg.getTaskName('exportProjectDir')}"
                     project."${bg.getTaskName('exportProjectDir')}".mustRunAfter project."${bg.getTaskName('exportModelDir')}"
                  } else if (contentPolicy == 'file') {
                     project."${bg.getTaskName('import')}".dependsOn project."${bg.getTaskName('importProjectFile')}"
                     project."${bg.getTaskName('export')}".dependsOn project."${bg.getTaskName('exportProjectFile')}"
                  }
               }

               if (project.extensions.odi.enableModels) {
                  if (contentPolicy == 'dir') {
                     project."${bg.getTaskName('import')}".dependsOn project."${bg.getTaskName('importModelDir')}"
                     project."${bg.getTaskName('importModelDir')}".mustRunAfter project."${bg.getTaskName('importGlobalDir')}"
                     project."${bg.getTaskName('export')}".dependsOn project."${bg.getTaskName('exportModelDir')}"
                     project."${bg.getTaskName('exportModelDir')}".mustRunAfter project."${bg.getTaskName('exportGlobalDir')}"
                  }
               }

               if (project.extensions.odi.enableLoadPlans) {
                  if (contentPolicy == 'dir') {
                     project."${bg.getTaskName('import')}".dependsOn project."${bg.getTaskName('importLoadPlanDir')}"
                     project."${bg.getTaskName('importLoadPlanDir')}".mustRunAfter project."${bg.getTaskName('importScenarioDir')}"
                     project."${bg.getTaskName('export')}".dependsOn project."${bg.getTaskName('exportLoadPlanDir')}"
                     project."${bg.getTaskName('exportLoadPlanDir')}".mustRunAfter project."${bg.getTaskName('exportScenarioDir')}"
                  }
               }

               if (project.extensions.odi.enableScenarios) {
                  if (contentPolicy == 'dir') {
                     project."${bg.getTaskName('import')}".dependsOn project."${bg.getTaskName('importScenarioDir')}"
                     project."${bg.getTaskName('importScenarioDir')}".mustRunAfter project."${bg.getTaskName('importProjectDir')}"
                     project."${bg.getTaskName('export')}".dependsOn project."${bg.getTaskName('exportScenarioDir')}"
                     project."${bg.getTaskName('exportScenarioDir')}".mustRunAfter project."${bg.getTaskName('exportProjectDir')}"
                  }
               }

               if (project.extensions.odi.enableGlobals) {
                  if (contentPolicy == 'dir') {
                     project."${bg.getTaskName('import')}".dependsOn project."${bg.getTaskName('importGlobalDir')}"
                     project."${bg.getTaskName('importGlobalDir')}".mustRunAfter project."${bg.getTaskName('importTopologyDir')}"
                     project."${bg.getTaskName('export')}".dependsOn project."${bg.getTaskName('exportGlobalDir')}"
                     project."${bg.getTaskName('exportGlobalDir')}".mustRunAfter project."${bg.getTaskName('exportTopologyDir')}"
                  }
               }

               if (project.extensions.odi.enableTopologies) {
                  if (contentPolicy == 'dir') {
                     project."${bg.getTaskName('import')}".dependsOn project."${bg.getTaskName('importTopologyDir')}"
                     project."${bg.getTaskName('export')}".dependsOn project."${bg.getTaskName('exportTopologyDir')}"
                  }
               }
            }
         }
      }
   }
}
