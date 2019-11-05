package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportScenarioDirectoryTask extends ImportDirectoryTask {

    @Input
    String category = 'scenario'

    /**
     * Gets the hierarchical collection of XML files, sorted using folder structure and file name prefix logic.
     *
     * @return The List of export files.
     */
    @Internal
    List getImportScenarioFolderFiles() {

        def result = new LinkedList()

        result.addAll(project.fileTree(dir: importDir, include: "**/SFOL*.xml").toList())

        return result
    }

    @Internal
    List getImportScenarioFiles() {

        def result = new LinkedList()

        result.addAll(project.fileTree(dir: importDir, include: "**/SCEN*.xml").toList())

        return result
    }


    @TaskAction
    def taskAction() {

        //Make the Connection
        instance.connect()

        // Import the Scenario Folders
        smartImportXmlFiles(importScenarioFolderFiles)

        // Import the Scenarios
        importXmlFiles(importScenarioFiles)
    }

}
