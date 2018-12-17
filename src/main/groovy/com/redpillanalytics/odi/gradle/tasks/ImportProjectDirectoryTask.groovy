package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportProjectDirectoryTask extends ImportDirectoryTask {

   @Internal
   String getCategory() {
      return 'project'
   }

   @TaskAction
   def importAllObjectsXML() {

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      getImportFiles().each { file ->
         log.warn "Import file ${file}"
         importService.importObjectsFromXml(
                 file.path,
                 null,
                 true,
         )
      }
      instance.endTxn()
   }
}
