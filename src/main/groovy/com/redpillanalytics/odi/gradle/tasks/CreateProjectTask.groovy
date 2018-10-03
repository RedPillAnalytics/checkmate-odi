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
class CreateProjectTask extends DefaultTask {

   @Input
   @Option(option = "project-name",
           description = "The name of the project to create.")
   String pname

   @Input
   @Option(option = "project-code",
           description = "The code of the project to create.")
   String pcode

   @Internal
   Instance instance

   @TaskAction
   def createProject() {

      instance.connect()

      log.debug "All projects: ${instance.projectFinder.findAll().toString()}"

      if (instance.findProjectCode(pcode)) {

         log.warn "Project Code '${pcode}' already exists."

      } else if (instance.findProjectName(pname)) {

         log.warn "Project Name '${pname}' already exists."

      } else {
         instance.beginTxn()
         instance.odi.getTransactionalEntityManager().persist(new OdiProject(pname, pcode))
         instance.endTxn()

         log.warn "Project '${pname}' created sucessfully."
      }
   }
}