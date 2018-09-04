package com.redpillanalytics.odi.gradle

import com.redpillanalytics.common.GradleUtils
import com.redpillanalytics.odi.gradle.containers.BuildGroupContainer
import com.redpillanalytics.odi.gradle.tasks.CreateProjectTask
import com.redpillanalytics.odi.gradle.tasks.GetProjectsTask
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

        // create the extension for the build group container
        // this facilitates creating multiple build groups
        // build groups are groups of similar tasks with slight configuration modifications
        project.odi.extensions.buildGroups = project.container(BuildGroupContainer)

        // we'll start with only a single build group
        project.extensions.odi.buildGroups.add(new BuildGroupContainer('current'))

        project.afterEvaluate {

            // define the method to get build parameters
            def getParameter = { name, defaultValue = null ->

                return GradleUtils.getParameter(project, name, 'odi')
            }

            String projectName
            String projectCode
            String sourceBase = getParameter('sourceBase')

            // see if there's an explicit project name
            if (getParameter('projectName')) {

                // use this throughout for the projectName
                projectName = getParameter('projectName')

                // also set our archive name to projectName
                project.archivesBaseName = projectName
            } else {

                // we don't have a projectName so we need one
                // just use the default archivesBaseName
                projectName = project.archivesBaseName
            }

            // if no project code is specified, create one
            projectCode = getParameter('projectCode') ?: project.extensions.odi.getProjectCode(projectName)

            // capture all the connection parameters
            def masterUrl = getParameter('masterUrl')
            log.debug "masterUrl: $masterUrl"
            def masterDriver = getParameter('masterDriver')
            log.debug "masterDriver: $masterDriver"
            def masterRepo = getParameter('masterRepo')
            log.debug "masterRepo: $masterRepo"
            def workRepo = getParameter('workRepo')
            log.debug "workRepo: $workRepo"
            def masterPassword = getParameter('masterPassword')
            //log.debug "masterPassword: $masterPassword"
            def odiUser = getParameter('odiUser')
            log.debug "odiUser: $odiUser"
            def odiPassword = getParameter('odiPassword')
            //log.debug "odiPassword: $odiPassword"

            // configure all build groups
            project.odi.buildGroups.all { bg ->

                if (project.extensions.odi.isDevelopment()) {

                    // Task that creates a project
                    project.task(bg.getTaskName('createProject'), type: CreateProjectTask) {

                        group 'project'

                        description = "Create a new project in the ODI Instance."

                        pname projectName

                        pcode projectCode

                        url masterUrl

                        driver masterDriver

                        master masterRepo

                        work workRepo

                        masterPass masterPassword

                        odi odiUser

                        odiPass odiPassword

                    }

                    // Task that executes the smart export of a project
                    project.task(bg.getTaskName('exportProject'), type: SmartExportTask) {

                        group 'project'

                        description = "Executes a Smart Export of a project in the ODI Instance."

                        url masterUrl

                        driver masterDriver

                        master masterRepo

                        work workRepo

                        masterPass masterPassword

                        odi odiUser

                        odiPass odiPassword

                        sourcePath sourceBase

                        pname projectName

                    }

                    // Task that executes the smart import of a project
                    project.task(bg.getTaskName('importProject'), type: SmartImportTask) {

                        group 'project'

                        description = "Executes a Smart Import of a project to the ODI Instance."

                        url masterUrl

                        driver masterDriver

                        master masterRepo

                        work workRepo

                        masterPass masterPassword

                        odi odiUser

                        odiPass odiPassword

                        sourcePath sourceBase

                    }

                    // Task that get all the existing projects in the Repository
                    project.task(bg.getTaskName('getProjects'), type: GetProjectsTask) {

                        group 'project'

                        description = "Get all the projects existing on the ODI Instance."

                        url masterUrl

                        driver masterDriver

                        master masterRepo

                        work workRepo

                        masterPass masterPassword

                        odi odiUser

                        odiPass odiPassword
                    }

                    // Task that executes the smart import of a project
                    project.task(bg.getTaskName('importAllXML'), type: SmartImportAllTask) {

                        group 'project'

                        description = "Executes a Smart Import of all the XML Files from a Source Path to the ODI Instance."

                        url masterUrl

                        driver masterDriver

                        master masterRepo

                        work workRepo

                        masterPass masterPassword

                        odi odiUser

                        odiPass odiPassword

                        sourcePath sourceBase

                    }
                }
            }
        }
    }
}
