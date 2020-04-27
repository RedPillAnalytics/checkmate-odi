package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportGlobalDirectoryTask extends ImportDirectoryTask {

    @Input
    String category = 'global'

    @Internal
    Instance instance

    @TaskAction
    def taskAction() {

        //Make the Connection
        instance.connect()

        try {

            def smartImportFilePrefix = ['KM']

            def importFilePrefix = ['REUMAP', 'SEQ', 'UFN', 'VAR']

            // Smart Import the Global Templates and KM
            smartImportFilePrefix.each {
                smartImportXmlFiles(getImportFiles(it))
            }

            // Import the Global Objects
            importFilePrefix.each {

                // Import the files by prefix
                importXmlFiles(getImportFiles(it))

            }

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
