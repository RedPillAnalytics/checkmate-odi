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

   @TaskAction
   def exportModelDirectory() {

      instance.connect()
      def models = modelCode ? instance.findModelbyCode(modelCode) : instance.findAllModels()

      instance.beginTxn()
      models.each {
         exportObjectWithParents(it, sourceBase.canonicalPath, true, true)
      }
      instance.endTxn()
   }
}