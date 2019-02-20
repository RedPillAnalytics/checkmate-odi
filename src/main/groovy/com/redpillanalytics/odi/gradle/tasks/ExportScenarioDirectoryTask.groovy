package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
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

        instance.findAllScenarios().each {
            exportObject(it, sourceBase.canonicalPath, true)
        }
        instance.endTxn()
    }
}