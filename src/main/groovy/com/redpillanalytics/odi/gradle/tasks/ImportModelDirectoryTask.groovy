package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportModelDirectoryTask extends ImportDirectoryTask {

    @Input
    String category = 'model'

    /**
     * Gets the hierarchical collection of XML files, sorted using folder structure and file name prefix logic.
     *
     * @return The List of export files.
     */
    @Internal
    List getImportModelFolderFiles() {

        def result = new LinkedList()

        result.addAll(project.fileTree(dir: importDir, include: "**/MFOL_*.xml").toList())

        return result
    }

    @Internal
    List getImportModelFiles() {

        def result = new LinkedList()

        result.addAll(project.fileTree(dir: importDir, include: "**/MOD_*.xml").toList())

        return result
    }

    @TaskAction
    def taskAction() {

        //Make the Connection
        instance.connect()

        // Import the Model Folders
        smartImportXmlFiles(importModelFolderFiles)

        // Import the Models
        importXmlFiles(importModelFiles)
    }
}
