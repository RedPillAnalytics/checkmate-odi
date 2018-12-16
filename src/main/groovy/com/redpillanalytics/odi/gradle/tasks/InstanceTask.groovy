package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal

@Slf4j
class InstanceTask extends DefaultTask {

   @Internal
   Instance instance

   @Internal
   String category = ''

   @Internal
   String getSubDirectory() {
      return (category == '') ? '' : "${category}/"
   }
}
