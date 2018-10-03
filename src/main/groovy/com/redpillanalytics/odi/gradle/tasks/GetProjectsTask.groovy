package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiProject
import oracle.odi.domain.project.finder.IOdiProjectFinder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option


@Slf4j
class GetProjectsTask extends DefaultTask {

   @Internal
   Instance instance

   @TaskAction
   def getProjects() {

      instance.connect()

      //Get all projects and save into "projects"
      //def projects = ((IOdiProjectFinder) instance.odi.getTransactionalEntityManager().getFinder(OdiProject.class)).findAll().toArray()

      def projects = instance.projects

      instance.beginTxn()

      projects.each { OdiProject project ->
         //Action to do to the projects retrieved
         println(project.name)
      }
      instance.endTxn()

   }
}