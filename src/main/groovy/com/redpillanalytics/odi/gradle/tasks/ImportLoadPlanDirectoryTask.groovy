package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportLoadPlanDirectoryTask extends ImportDirectoryTask {

    @Input
    String category = 'load-plan'

    @Internal
    Instance instance

    @TaskAction
    def taskAction() {

        //Make the Connection
        instance.connect()

        try {

            // Import the Load Plans
            importXmlFiles(getImportFiles('LP'))

            // Close the Connection
            instance.close()

        } catch(Exception e) {
            // End the Transaction
            instance.endTxn()
            // Close the Connection
            instance.close()
            // Throw the Exception
            throw e
        }

    }
}
