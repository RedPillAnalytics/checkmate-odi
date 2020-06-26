package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportScenarioDirectoryTask extends ExportDirectoryTask {

    // specify the model subdirectory
    String category = 'scenario'

    @Internal
    Instance instance

    /**
     * The ODI scenario to export. Default: null, which means all scenarios are exported.
     */
    @Input
    @Optional
    @Option(option = "scenario-name",
            description = "The ODI scenario code to export. Default: null, which means all scenarios are exported.")
    String scenarioName

    /**
     * The ODI scenario folder name to export. Default: null, which means all scenario folders are exported.
     */
    @Input
    @Optional
    @Option(option = "scenario-folder",
            description = "The ODI scenario folder name to export. Default: null, which means all scenario folders are exported.")
    String scenarioFolderName

    @TaskAction
    def taskAction() {

        instance.connect()

        try {

            // get the scenario folders
            def scenarioFolders = scenarioFolderName ? instance.findScenarioFolderByName(scenarioFolderName) : instance.findAllScenarioFolders()

            // get the scenarios
            def scenario = scenarioName ? instance.findScenarioByName(scenarioName) : instance.findAllScenarios()

            instance.beginTxn()

            // export all the scenario folders
            log.info('Exporting scenario-folders...')
            scenarioFolders.each {
                exportObject(it as IExportable, "${exportDir.canonicalPath}/scenario-folder", true, false)
            }

            // export all the scenarios
            log.info('Exporting scenarios...')
            scenario.each {
                exportObject(it as IExportable, "${exportDir.canonicalPath}/scenario")
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

        if ( !scenarioName && !scenarioFolderName ) {
            // execute the export stage process
            exportStageDir()
        } else {
            // execute the export stage process without deleted objects
            exportStageDir(false)
        }

    }
}