package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportDirectoryTask extends ImportTask {

   /**
    * The base directory to import content from. Default: 'src/main/project' for ODI projects, and 'src/main/model' for ODI models.
    */
   @Input
   @Optional
   @Option(option = "source-dir",
           description = "The base directory to import content from. Default: 'src/main/project' for ODI projects, and 'src/main/model' for ODI models."
   )
   String sourceDir

   @InputDirectory
   File getImportDir() {

      log.debug "sourceBase: $sourceBase"

      if (!sourceDir) {
         return sourceBase
      } else {
         File dir = new File(sourceDir)
         return dir.exists() ? project.file(sourceDir) : project.file(sourceBase)
      }
   }

   /**
    * Smart Import the File List of Objects.
    */
   @Internal
   def smartImportXmlFiles(List<File> smartImportFiles) {

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      smartImportFiles.each { file ->
         log.info "Importing file '$file.canonicalPath'..."
         smartImportObject(file)
      }

      instance.endTxn()
   }

   /**
    * Import the File List of Objects.
    */
   @Internal
   def importXmlFiles(List<File> importFiles) {

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      importFiles.each { file ->
         log.info "Importing file '$file.canonicalPath'..."
         importObject(file)
      }

      instance.endTxn()
   }

}
