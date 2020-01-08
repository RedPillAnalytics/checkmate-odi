package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@Slf4j
class WaitForAgentTask extends DefaultTask {

    WaitForAgentTask() {
        description = 'Wait until the ODI Agent is available.'
        group = project.extensions.odi.taskGroup
    }

    @Input
    String agentUrl

   @TaskAction
   def taskAction() {
       // hack to do bottom-checking loop
       for (; ;) { // infinite for
           logger.info "Waiting for ODI agent ${agentUrl} to be available..."
           def response
           try {
               def conn = agentUrl.toURL().openConnection()
               response = conn.responseCode
               conn.disconnect()
           } catch(ConnectException ce){
               logger.info "ConnectException occurred. Sleeping..."
               sleep 5000
           } catch(SocketException se) {
               logger.info "SocketException occurred. Sleeping..."
               sleep 10000
           }
           if (response == 200) {
               break
           }
       }
   }
}