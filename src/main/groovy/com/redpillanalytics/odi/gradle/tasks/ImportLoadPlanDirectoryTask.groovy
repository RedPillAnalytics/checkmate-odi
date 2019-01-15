package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportLoadPlanDirectoryTask extends ImportDirectoryTask {

   @Input
   String category = 'load-plan'

   @TaskAction
   def taskAction() {
      importXmlFiles()
   }
}
