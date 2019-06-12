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
            log.warn 'SUCCESSFULLY CONNECTED TO ODI REPOSITORY!'
        }
        catch (NullPointerException e) {
            throw new Exception("FAILED TO CONNECT TO ODI REPOSITORY!")
        }
    }
}