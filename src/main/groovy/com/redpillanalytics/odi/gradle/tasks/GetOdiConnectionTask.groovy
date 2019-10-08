package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class GetOdiConnectionTask extends DefaultTask {

    @Internal
    Instance instance

    @TaskAction
    def taskAction() {

        try{
            instance.connect()
            instance.beginTxn()
            log.warn 'Successfully Connected to ODI Repository!'
            instance.endTxn()
        }
        catch (NullPointerException e) {
            throw new Exception("Failed to Connect to ODI Repository!")
        }
    }
}