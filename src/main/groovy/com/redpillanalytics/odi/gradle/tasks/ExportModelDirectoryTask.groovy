package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportModelDirectoryTask extends ExportDirectoryTask {

   // specify the model subdirectory
   String category = 'model'

   @Internal
   Instance instance

   /**
    * The ODI model code to export. Default: null, which means all models are exported.
    */
   @Input
   @Optional
   @Option(option = "model-code",
           description = "The ODI model code to export. Default: null, which means all models are exported.")
   String modelCode

   /**
    * The ODI model folder name to export. Default: null, which means all models folders are exported.
    */
   @Input
   @Optional
   @Option(option = "model-folder",
           description = "The ODI model folder name to export. Default: null, which means all model folders are exported.")
   String modelfolderName

   @OutputDirectory
   File getExportDir() {
      return sourceDir ? project.file(sourceDir) : ( !modelCode && !modelfolderName ) ? buildDir : sourceBase
   }

   @TaskAction
   def taskAction() {

      instance.connect()

      try {

         // get the model folders
         def modelfolders = modelfolderName ? instance.findModelFolderbyName(modelfolderName) : instance.findAllModelFolders()

         // get the models
         def models = modelCode ? instance.findModelbyCode(modelCode) : instance.findAllModels()

         instance.beginTxn()
         // export the model folders
         log.info('Exporting model-folders...')
         modelfolders.each {
            exportObject(it as IExportable, "${exportDir.canonicalPath}/model-folder", true, false)
         }

         // export the models
         log.info('Exporting models...')
         models.each {
            exportObject(it as IExportable, "${exportDir.canonicalPath}/model")
         }

         instance.endTxn()

         instance.close()

      } catch(Exception e) {
         // End the Transaction
         instance.endTxn()
         // Close the Connection
         instance.close()
         // Throw the Exception
         throw e
      }

      if ( !modelCode && !modelfolderName ) {
         // execute the export stage process
         exportStageDir()
      }

   }
}