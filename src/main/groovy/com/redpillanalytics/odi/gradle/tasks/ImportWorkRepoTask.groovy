package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.impexp.support.ImportServiceImpl
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
   def importXmlFiles() {

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      importFiles.each { file ->
         importService.importObjectFromXml(
                 ImportServiceImpl.IMPORT_MODE_SYNONYM_INSERT_UPDATE,
                 file.canonicalPath,
                 false,
                 null,
                 true,
         )
      }
      instance.endTxn()
   }
}
