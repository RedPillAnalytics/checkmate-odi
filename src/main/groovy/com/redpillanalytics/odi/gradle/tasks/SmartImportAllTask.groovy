package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.common.Utils
import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class SmartImportAllTask extends ImportTask {

   @TaskAction
   def importAllObjectsXML() {

      log.debug "sourcePath: ${sourcePath}"
      log.debug "sourceBase: ${sourceBase}"

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      Utils.getFilesByExt(sourceBase,'xml').each { file ->
         log.warn "Importing file ${file.path}..."
         new SmartImportServiceImpl(instance.odi).importObjectsFromXml(
                 file.path,
                 null,
                 true,
         )
      }

      instance.endTxn()
   }
}
