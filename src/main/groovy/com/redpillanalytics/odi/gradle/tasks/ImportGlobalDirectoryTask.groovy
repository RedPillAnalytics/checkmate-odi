package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Internal

@Slf4j
class ImportGlobalDirectoryTask extends ImportDirectoryTask {

    /**
     * Gets the hierarchical collection of XML files, sorted using folder structure and file name prefix logic.
     *
     * @return The List of export files.
     */
    @Internal
    List getImportFiles() {

        def filePrefix = ['KM', 'REUMAP', 'SEQ', 'UFN', 'VAR']

        def result = new LinkedList()

        filePrefix.each {
            result.addAll(project.fileTree(dir: importDir, include: "**/${it}_*.xml").toList())
        }

        return result
    }
}
