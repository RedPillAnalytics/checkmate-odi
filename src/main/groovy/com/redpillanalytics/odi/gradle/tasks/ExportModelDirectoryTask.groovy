package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportModelDirectoryTask extends ExportDirectoryTask {

   // specify the model subdirectory
   String category = 'model'

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

   @TaskAction
   def exportModelDirectory() {

      instance.connect()
      // get the model folders
      def modelfolders = modelfolderName ? instance.findModelFolderbyName(modelfolderName) : instance.findAllModelFolders()

      // get the models
      def models = modelCode ? instance.findModelbyCode(modelCode) : instance.findAllModels()

      instance.beginTxn()
      // export the model folders
      modelfolders.each {
         exportObject(it, "${exportDir.canonicalPath}/model-folder", true, false)
         //smartExportObject(it, exportDir.canonicalPath, it.name)
      }

      // export the models
      models.each {
         exportObject(it, "${exportDir.canonicalPath}/model", true)
         //smartExportObject(it, exportDir.canonicalPath, it.name)
      }

      instance.endTxn()
   }
}