package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ExportScenarioDirectoryTask extends ExportDirectoryTask {

    // specify the model subdirectory
    String category = 'scenario'

    @Internal
    Instance instance

    @SuppressWarnings("GroovyAssignabilityCheck")
    @TaskAction
    def exportScenarios() {

        instance.connect()

        instance.beginTxn()

        // export all the scenario folders
        instance.findAllScenarioFolders().each {
            exportObject(it, "${sourceBase.canonicalPath}/scenario-folder", true)
            //smartExportObject(it, "${sourceBase.canonicalPath}/scenario-folder", it.name)
        }

        // export all the scenarios
        instance.findAllScenarios().each {
            exportObject(it, "${sourceBase.canonicalPath}/scenario", true)
            //smartExportObject(it, "${sourceBase.canonicalPath}/scenario", it.name)
        }

        instance.endTxn()
    }
}