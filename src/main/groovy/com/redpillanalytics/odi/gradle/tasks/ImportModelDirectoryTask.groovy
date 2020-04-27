package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportModelDirectoryTask extends ImportDirectoryTask {

    @Input
    String category = 'model'

    @Internal
    Instance instance

    @TaskAction
    def taskAction() {

        //Make the Connection
        instance.connect()

        try {

            // Import the Model Folders
            smartImportXmlFiles(getImportFiles('MFOL'))

            // Import the Models
            importXmlFiles(getImportFiles('MOD'))

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
