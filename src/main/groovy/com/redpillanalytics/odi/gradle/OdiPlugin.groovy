package com.redpillanalytics.odi.gradle

import com.redpillanalytics.odi.gradle.containers.TaskGroupContainer
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project

@Slf4j
class OdiPlugin implements Plugin<Project> {

   void apply(Project project) {

      // apply Gradle built-in plugins
      project.apply plugin: 'base'

      project.configure(project) {
         extensions.create('odi', OdiPluginExtension)
      }

      project.odi.extensions.buildGroups = project.container(TaskGroupContainer)

      project.afterEvaluate {



      }
   }
}
