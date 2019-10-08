package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal

@Slf4j
class InstanceTask extends DefaultTask {

//   InstanceTask() {
//      // if the ODI API plugin is installed, then ensure the API is there
//      if (project.plugins.findPlugin('com.redpillanalytics.checkmate.odi.api') && project.odi.extractOdiApi) {
//         dependsOn project.tasks.extractApi
//      }
//   }

   @Internal
   Instance instance

   @Internal
   String getCategory() {
      return 'odi'
   }

   @Internal
   def getSourceBase() {
      log.debug "category: ${category}"
      return project.file("${project.extensions.odi.sourceBase}/${category}")
   }

   @Internal
   def getBuildDir() {
      log.debug "category: ${category}"
      return project.file("${project.extensions.odi.buildDir}/${category}")
   }

}
