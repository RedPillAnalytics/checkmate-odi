package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportTopologyDirectoryTask extends ExportDirectoryTask {

    @Internal
    String category = 'topology'

    @Internal
    Instance instance

    @TaskAction
    def taskAction() {

        instance.connect()

        try {

            instance.beginTxn()

            // Export the Topology
            exportTopology(exportDir.canonicalPath)

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
