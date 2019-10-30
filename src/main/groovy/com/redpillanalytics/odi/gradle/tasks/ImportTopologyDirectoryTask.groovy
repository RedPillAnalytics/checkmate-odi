package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
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
    @Internal
    List getImportFiles() {

        def filePrefix = ['TECH','CONN', 'PSC', 'AGENT', 'LAGENT', 'CONT', 'LSC']

        def result = new LinkedList()

        filePrefix.each {
            result.addAll(project.fileTree(dir: importDir, include: "**/${it}_*.xml").toList())
        }

        return result
    }

    @TaskAction
    def taskAction() {
        // Import the Topology Objects
        importXmlFiles(importFiles)
    }
}
