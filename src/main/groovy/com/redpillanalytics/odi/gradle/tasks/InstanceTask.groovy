package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal

@Slf4j
class InstanceTask extends DefaultTask {

   InstanceTask() {
      dependsOn project.tasks.extractApi
   }

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
}
