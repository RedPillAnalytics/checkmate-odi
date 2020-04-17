package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportTopologyDirectoryTask extends ImportDirectoryTask {

    @Input
    String category = 'topology'

    @TaskAction
    def taskAction() {

        // Make the connection
        instance.connect()

        // Import the topology directory
        importTopologyDir()

        // Close the connection
        instance.close()
    }

}
