package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportProjectFileTask extends ImportFileTask {

   @Internal
   String category = 'project'

   @TaskAction
   def importObjectXML() {

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      smartImportService.importObjectsFromXml(
              importFile.canonicalPath,
              null,
              true,
      )
      instance.endTxn()
   }
}

