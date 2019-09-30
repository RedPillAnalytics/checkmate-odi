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

        def tree = project.fileTree(
                dir: importDir,
                include: ['**/CONN_*.xml', '**/PSC_*.xml', '**/AGENT_*.xml', '**/LAGENT_*.xml', '**/CONT_*.xml', '**/LSC_*.xml' ])

        log.info("List of Objects to Import: ${tree.toList()} ")

        return tree.toList()
    }
}
