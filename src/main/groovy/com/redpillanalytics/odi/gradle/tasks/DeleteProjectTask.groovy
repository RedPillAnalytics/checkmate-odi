package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiProject
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option


@Slf4j
class DeleteProjectTask extends DefaultTask {

   @Input
   @Option(option = "project-code",
           description = "The Project Code of the ODI Project.")
   String projectCode

   @Internal
   Instance instance

   @Internal

   @TaskAction
   def deleteProject() {

      instance.connect()

      log.debug "All projects: ${instance.projects.toString()}"

      if (!instance.findProjectName(projectCode)) {

         log.warn "Project Code ${projectCode} does not exist."

      } else {
         instance.beginTxn()
         instance.odi.getTransactionalEntityManager().remove(instance.getOdiProject(projectCode))
         instance.endTxn()

         log.warn "Project Code '${projectCode}' deleted sucessfully."
      }
   }
}