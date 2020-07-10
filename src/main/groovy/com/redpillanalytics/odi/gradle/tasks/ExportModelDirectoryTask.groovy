package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import oracle.odi.domain.model.OdiModel
import oracle.odi.domain.model.OdiModelFolder
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
   String modelFolderName

   @TaskAction
   def taskAction() {

      instance.connect()

      try {

         // get all the model folders
         def modelFolders = instance.findAllModelFolders() as List<OdiModelFolder>

         // get all the models
         def models = instance.findAllModels() as List<OdiModel>

         instance.beginTxn()
         // export the model folders

         log.info('Exporting model-folders...')
         modelFolders.each { OdiModelFolder object ->
            if(!modelFolderName || modelFolderName.toLowerCase().contains(object.name.toLowerCase())) {
               exportObject(object, "${exportDir.canonicalPath}/model-folder", true, false)
            }
         }

         // export the models
         log.info('Exporting models...')
         models.each { OdiModel object ->
            if(!modelFolderName || ((object.getParentModelFolder() && modelFolderName) ?
                    modelFolderName.toLowerCase().contains(object.getParentModelFolder().name.toLowerCase()) : false)) {
               if(!modelCode || modelCode.toLowerCase().contains(object.name.toLowerCase())) {
                  exportObject(object, "${exportDir.canonicalPath}/model")
               }
            }
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

      if ( !modelCode && !modelFolderName ) {
         // execute the export stage process
         exportStageDir()
      } else {
         // execute the export stage process without deleted objects
         exportStageDir(false)
      }

   }
}