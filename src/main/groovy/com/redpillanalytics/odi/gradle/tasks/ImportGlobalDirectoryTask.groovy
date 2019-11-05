package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportGlobalDirectoryTask extends ImportDirectoryTask {

    @Input
    String category = 'global'

    /**
     * Gets the hierarchical collection of XML files, sorted using folder structure and file name prefix logic.
     *
     * @return The List of export files.
     */
    @Internal
    List getImportFiles(String filePrefix) {

        def result = new LinkedList()

        result.addAll(project.fileTree(dir: importDir, include: "**/${filePrefix}_*.xml").toList())

        return result
    }

    @Internal
    List getImportKMFiles() {

        def result = new LinkedList()

        result.addAll(project.fileTree(dir: importDir, include: "**/KM_*.xml").toList())

        return result
    }

    @TaskAction
    def taskAction() {

        //Make the Connection
        instance.connect()

        def filePrefix = ['REUMAP', 'SEQ', 'UFN', 'VAR']

        // Import the Global KM
        smartImportXmlFiles(importKMFiles)

        // Import the Global Objects
        filePrefix.each {

            // Import the files by prefix
            importXmlFiles(getImportFiles(it))

        }

    }

}
