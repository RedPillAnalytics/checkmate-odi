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
               new EncodingOptions(EncodingOptions.DEFAULT_XML_VERSION,
                                   EncodingOptions.DEFAULT_JAVA_CHARSET,
                                   EncodingOptions.DEFAULT_XML_CHARSET),
              'checkmate-odi12c+' as char[],
              false
      )

      instance.endTxn()
   }
}
