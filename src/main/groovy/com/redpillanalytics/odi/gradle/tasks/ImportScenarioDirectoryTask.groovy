package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportScenarioDirectoryTask extends ImportDirectoryTask {

    @Input
    String category = 'scenario'

    @Internal
    Instance instance

    @TaskAction
    def taskAction() {

        //Make the Connection
        instance.connect()

        try {

            // Import the Scenario Folders
            log.info('Importing scenario-folders...')
            smartImportXmlFiles(getImportFiles('SFOL'))

            // Import the Scenarios
            log.info('Importing scenarios...')
            importXmlFiles(getImportFiles('SCEN'))

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
