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
    def taskAction() {

        instance.connect()

        try {

            instance.beginTxn()

            // export all the scenario folders
            instance.findAllScenarioFolders().each {
                exportObject(it, "${exportDir.canonicalPath}/scenario-folder", true, false)
            }

            // export all the scenarios
            instance.findAllScenarios().each {
                exportObject(it, "${exportDir.canonicalPath}/scenario")
            }

            instance.endTxn()

            instance.close()

        } catch(Exception e) {
            // End the Transaction
            instance.endTxn()
            // Close the Connection
            instance.close()
            // Throw the Exception
            throw e
        }

        // execute the export stage process
        exportStageDir()

    }
}