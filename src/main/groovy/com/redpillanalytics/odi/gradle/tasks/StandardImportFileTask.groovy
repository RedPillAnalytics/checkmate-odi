package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.impexp.support.ImportServiceImpl
import org.gradle.api.tasks.TaskAction

@Slf4j
class StandardImportFileTask extends ImportFileTask {

   @TaskAction
   def standardImport() {

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      new ImportServiceImpl(instance.odi).importObjectFromXml(
              ImportServiceImpl.IMPORT_MODE_SYNONYM_INSERT_UPDATE,
              importFile.canonicalPath,
              false,
              null,
              true,
      )
      instance.endTxn()
   }
}

