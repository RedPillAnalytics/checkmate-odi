package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import org.gradle.api.tasks.TaskAction

@Slf4j
class ExportModelDirectoryTask extends ExportDirectoryTask {

   // specify the model subdirectory
   String category = 'model'

   @TaskAction
   def exportModelDirectory() {

      instance.connect()

      def folders = folderName ? instance.findModelFolderbyName(folderName) : instance.findAllModelFolders()

      instance.beginTxn()
      folders.each { IExportable folder ->

         exportObjectWithParents(
                 folder,
                 exportDir.canonicalPath,
                 true,
                 true
         )
      }
      instance.endTxn()
   }
}