package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportProjectDirectoryTask extends ImportDirectoryTask {

   @TaskAction
   def importAllObjectsXML() {

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      getImportFiles().each { file ->
         log.info "Importing file ${file.path}..."
         new SmartImportServiceImpl(instance.odi).importObjectsFromXml(
                 file.path,
                 null,
                 true,
         )
      }
      instance.endTxn()
   }
}
