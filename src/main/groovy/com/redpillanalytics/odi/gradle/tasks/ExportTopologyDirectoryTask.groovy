package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportTopologyDirectoryTask extends ExportDirectoryTask {

    @Internal
    String category = 'topology'

    @TaskAction
    def exportObjects() {

        instance.connect()

        instance.beginTxn()

        // export the topology in export directory
        exportTopology(exportDir.canonicalPath)

        instance.endTxn()

        instance.close()

        // execute the export stage process
        exportStageDir()

    }

}
