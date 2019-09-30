package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Internal

@Slf4j
class ImportTopologyDirectoryTask extends ImportDirectoryTask {

    /**
     * Gets the hierarchical collection of XML files, sorted using folder structure and alphanumeric logic.
     *
     * @return The List of export files.
     */
    @Internal
    List getImportFiles() {

        def filePrefix = ['CONN', 'PSC', 'AGENT', 'LAGENT', 'CONT', 'LSC']

        def result = new LinkedList()

        filePrefix.each {
            result.addAll(project.fileTree(dir: importDir, include: "**/${it}_*.xml").toList())
        }

        return result
    }
}
