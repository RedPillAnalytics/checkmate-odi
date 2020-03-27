package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportTopologyDirectoryTask extends ImportDirectoryTask {

    @Input
    String category = 'topology'

    /**
     * Gets the hierarchical collection of XML files, sorted using folder structure and file name prefix logic.
     *
     * @return The List of export files.
     */
    List getImportFiles(String filePrefix) {
        def result = new LinkedList()
        result.addAll(project.fileTree(dir: importDir, include: "**/${filePrefix}_*.xml").toList())
        return result
    }

    @TaskAction
    def taskAction() {

        //Make the Connection
        instance.connect()

        def filePrefix = ['TECH', 'DT', 'CONVDT' , 'ACT' , 'CONN', 'PSC', 'AGENT', 'LAGENT', 'CONT', 'LSC']

        // Import the Topology Objects
        filePrefix.each {
            // Get the import files by prefix
            importXmlFiles(getImportFiles(it))
        }

        // Close the Connection
        instance.close()
    }
}
