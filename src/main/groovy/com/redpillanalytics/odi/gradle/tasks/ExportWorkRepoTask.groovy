package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiFolder
import oracle.odi.impexp.EncodingOptions
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportWorkRepoTask extends ExportDirectoryTask {

   @Internal
   String category = 'odi'

   @TaskAction
   def exportObjects() {

      instance.connect()

      instance.beginTxn()
      exportService.exportWorkInFolder(
              exportDir.canonicalPath,
              new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9"),
              null, true
      )
      instance.endTxn()
   }
}
