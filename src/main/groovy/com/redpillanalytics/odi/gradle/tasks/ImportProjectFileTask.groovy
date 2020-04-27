package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportProjectFileTask extends ImportTask {

   @Internal
   String category = 'file'

   @Internal
   Instance instance

   /**
    * The ODI project code to import. Default: value of 'obi.projectName', or the name of the project subdirectory.
    */
   @Input
   @Option(option = "project-code",
           description = "The ODI project code to import. Default: value of 'obi.projectName', or the name of the project subdirectory.")
   String projectCode

   /**
    * The file to import content from. Default: 'src/main/file/<PROJECTCODE>.xml'.
    */
   @Input
   @Optional
   @Option(option = "source-file",
           description = "The file to import content from. Default: 'src/main/file/FILE_<PROJECTCODE>.xml'."
   )
   String sourceFile

   @InputFile
   File getImportFile() {

      log.debug "sourceBase: $sourceBase"

      if (sourceFile) {
         File file = new File(sourceBase, sourceFile)
         return file.exists() ? file : project.file(sourceFile)
      } else {
         return new File(sourceBase, "FILE_${projectCode}.xml")
      }
   }

   @TaskAction
   def importObjectXML() {

      //Make the Connection
      instance.connect()

      try {

         instance.beginTxn()

         smartImportObject(importFile)

         instance.endTxn()

         // Close the Connection
         instance.close()

      } catch(Exception e) {
         // End the Transaction
         instance.endTxn()
         // Close the Connection
         instance.close()
         // Throw the Exception
         throw e
      }

   }
}

