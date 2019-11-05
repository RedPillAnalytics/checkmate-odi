package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportLoadPlanDirectoryTask extends ImportDirectoryTask {

    @Input
    String category = 'load-plan'

    /**
     * Gets the hierarchical collection of XML files, sorted using folder structure and file name prefix logic.
     *
     * @return The List of export files.
     */
    @Internal
    List getImportFiles() {

        def result = new LinkedList()

        result.addAll(project.fileTree(dir: importDir, include: "**/LP_*.xml").toList())

        return result
    }

    @TaskAction
    def taskAction() {

        //Make the Connection
        instance.connect()

        // Import the Load Plans
        importXmlFiles(importFiles)
    }
}
