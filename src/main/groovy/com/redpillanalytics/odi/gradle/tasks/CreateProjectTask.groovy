package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiProject
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option


@Slf4j
class CreateProjectTask extends DefaultTask {


   @Input
   @Optional
   @Option(option = "project-code",
           description = "The ODI project code.")
   String projectCode

   @Input
   @Option(option = "project-name",
           description = "The ODI project name.")
   String projectName

   @Internal
   Instance instance

   @Internal
   def getProjectCode() {

      return projectCode ?: project.extensions.odi.getProjectCode(projectName)
   }

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
         instance.odi.getTransactionalEntityManager().persist(new OdiProject(projectName, projectCode))
         instance.endTxn()

         log.warn "Project '${pname}' created sucessfully."
      }
   }
}