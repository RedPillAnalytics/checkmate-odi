package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.runtime.scenario.OdiScenario
import oracle.odi.domain.runtime.scenario.OdiScenarioFolder
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportScenarioDirectoryTask extends ExportDirectoryTask {

    // specify the scenario subdirectory
    String category = 'scenario'

    @Internal
    Instance instance

    /**
     * The ODI scenario to export. Default: null, which means all scenarios are exported.
     */
    @Input
    @Optional
    @Option(option = "scenario-name",
            description = "The ODI scenario name to export. Default: null, which means all scenarios are exported.")
    String scenarioName

    /**
     * The ODI scenario version to export. Default: null, which means all scenarios are exported.
     */
    @Input
    @Optional
    @Option(option = "scenario-version",
            description = "The ODI scenario version to export. Default: null, which means all scenarios are exported.")
    String scenarioVersion

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

            // get all the scenario folders
            def scenarioFolders = instance.findAllScenarioFolders() as List<OdiScenarioFolder>

            // get all the scenarios
            def scenario = instance.findAllScenarios() as List<OdiScenario>

            instance.beginTxn()

            // export the scenario folders
            log.info('Exporting scenario-folders...')
            scenarioFolders.each { OdiScenarioFolder object ->
                if(!scenarioFolderName || scenarioFolderName.toLowerCase().contains(object.name.toLowerCase()))
                exportObject(object, "${exportDir.canonicalPath}/scenario-folder", true, false)
            }

            // export the scenarios
            log.info('Exporting scenarios...')
            scenario.each { OdiScenario object ->
                if(!scenarioFolderName || scenarioFolderName.toLowerCase().contains(object.getScenarioFolder().name.toLowerCase())) {
                    if(!scenarioName || scenarioName.toLowerCase().contains(object.name.toLowerCase())) {
                        if(!scenarioVersion || scenarioVersion.toLowerCase().contains(object.getVersion().toLowerCase())) {
                            exportObject(object, "${exportDir.canonicalPath}/scenario")
                        }
                    }
                }
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

        if ( !scenarioName && !scenarioVersion && !scenarioFolderName ) {
            // execute the export stage process
            exportStageDir()
        } else {
            // execute the export stage process without deleted objects
            exportStageDir(false)
        }

    }
}