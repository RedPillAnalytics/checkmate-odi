package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option


@Slf4j
class DeleteModelsTask extends InstanceTask {

   // specify the model subdirectory
   String category = 'model'

   /**
    * The ODI model code to delete. Default: null, which means all models are exported.
    */
   @Input
   @Optional
   @Option(option = "model-code",
           description = "The ODI model code to delete. Default: null, which means all models are exported.")
   String modelCode

   @TaskAction
   def deleteModels() {

      instance.connect()

      def models = modelCode ? instance.findModelbyCode(modelCode, true) : instance.findAllModels()

      if (!models) {
         log.warn modelCode ? "Model code '${modelCode}' does not exist." : "No models exist."
      } else {
         instance.beginTxn()
         models.each {
            if (!instance.findProject(projectCode, true)) {
               log.warn "Project Code ${projectCode} does not exist."
            } else {
               instance.beginTxn()
               instance.odi.getTransactionalEntityManager().remove(instance.getOdiProject(projectCode))
               instance.endTxn()

               log.warn "Project Code '${projectCode}' deleted."
            }
         }
         instance.endTxn()
      }
   }
}