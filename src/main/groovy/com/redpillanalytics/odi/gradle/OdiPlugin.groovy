package com.redpillanalytics.odi.gradle

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.base.plugins.LanguageBasePlugin

@Slf4j
class OdiPlugin implements Plugin<Project> {

   void apply(Project project) {

      // apply Gradle built-in plugins
      project.apply plugin: 'base'

      project.configure(project) {
         extensions.create('odi', OdiPluginExtension)
      }

      project.afterEvaluate {

      }
   }
}
