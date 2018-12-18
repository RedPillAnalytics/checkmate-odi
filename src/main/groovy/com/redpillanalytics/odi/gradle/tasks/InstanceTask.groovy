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

      return project.file("${project.extensions.odi.sourceBase}/${category ?: 'odi'}")
   }
}
