package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportWorkRepoTask extends ImportDirectoryTask {

   /**
    * Imports all objects returned by the {@link #getImportFiles} FileTree object.
    */
   @TaskAction
   def importDir() {

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      importFiles.each { file ->
         importService.importObjectFromXml()
         importService.importObjectsFromXml(
                 file.path,
                 null,
                 true,
         )
      }
      instance.endTxn()
   }
}
