package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportProjectFileTask extends ImportFileTask {

   @TaskAction
   def importObjectXML() {

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      new SmartImportServiceImpl(instance.odi).importObjectsFromXml(
              importFile.canonicalPath,
              null,
              true,
      )
      instance.endTxn()
   }
}

