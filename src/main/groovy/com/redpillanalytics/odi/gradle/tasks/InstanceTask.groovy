package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory

@Slf4j
class InstanceTask extends DefaultTask {

   @Internal
   Instance instance

   @Internal
   String getCategory() {
      return null
   }

   @OutputDirectory
   def getSourceBase() {

      log.debug "category: ${category}"

      switch (category) {
         case 'project': project.extensions.odi.projectSource
            break
         case 'model': project.extensions.odi.modelSource
            break
         default: "${project.extensions.odi.sourceBase}/odi"
      }
   }
}
