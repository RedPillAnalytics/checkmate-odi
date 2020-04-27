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

            // Smart Import the Global  KM
            smartImportFilePrefix.each {

                log.info("Importing knowledge-modules...")
                smartImportXmlFiles(getImportFiles(it))

            }

            // Import the Global Objects
            importFilePrefix.each {

                switch(it) {
                    case 'REUMAP':
                        log.info("Importing reusable-mappings...")
                        break
                    case 'SEQ':
                        log.info("Importing sequences...")
                        break
                    case 'UFN':
                        log.info("Importing user-functions...")
                        break
                    case 'VAR':
                        log.info("Importing variables...")
                        break
                }

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
