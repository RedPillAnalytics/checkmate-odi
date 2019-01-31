package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiProject
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option


@Slf4j
class CreateProjectTask extends InstanceTask {

   /**
    * The ODI project code to create. Default: value of 'obi.projectName', or the name of the project subdirectory.
    */
   @Input
   @Option(option = "project-code",
           description = "The ODI project code to create. Default: value of 'obi.projectName', or the name of the project subdirectory.")
   String projectCode

   /**
    * The ODI project name to create. Default: value of 'obi.projectName', or the name of the project subdirectory.
    */
   @Input
   @Option(option = "project-name",
           description = "The ODI project name to create. Default: value of 'obi.projectName', or the name of the project subdirectory.")
   String projectName

   @TaskAction
   def createProject() {

      instance.connect()

      if (instance.findProject(projectCode, true)) {
         log.warn "Project Code '${projectCode}' already exists."
      } else {
         instance.beginTxn()
         instance.odi.getTransactionalEntityManager().persist(new OdiProject(projectName, projectCode))
         instance.endTxn()
         log.warn "Project Name '${projectName}' with Project Code '${projectCode}' created."
      }
   }
}
