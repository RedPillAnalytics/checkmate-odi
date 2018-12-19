package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option


@Slf4j
class DeleteProjectTask extends InstanceTask {

   /**
    * The ODI project code to delete. Default: value of 'obi.projectName', or the name of the project subdirectory.
    */
   @Input
   @Option(option = "project-code",
           description = "The ODI project code to delete. Default: value of 'obi.projectName', or the name of the project subdirectory.")
   String projectCode

   @TaskAction
   def deleteProject() {

      instance.connect()

      log.debug "All projects: ${instance.projects.toString()}"

      if (!instance.findProject(projectCode, true)) {
         log.warn "Project Code ${projectCode} does not exist."
      } else {
         instance.beginTxn()
         instance.odi.getTransactionalEntityManager().remove(instance.getOdiProject(projectCode))
         instance.endTxn()

         log.warn "Project Code '${projectCode}' deleted."
      }
   }
}