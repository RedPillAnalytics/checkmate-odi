package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import oracle.odi.domain.runtime.loadplan.OdiLoadPlan
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportLoadPlanDirectoryTask extends ExportDirectoryTask {

   // specify the load-plan subdirectory
   String category = 'load-plan'

   @Internal
   Instance instance

   /**
    * The ODI scenario folder name to export. Default: null, which means all scenario folders are exported.
    */
   @Input
   @Optional
   @Option(option = "load-plan",
           description = "The ODI load plan name to export. Default: null, which means all load plans are exported.")
   List<String> loadPlanList

   @TaskAction
   def taskAction() {

      instance.connect()

      try{

         instance.beginTxn()

         // get the load plans
         def loadPlans = instance.findAllLoadPlans() as List<OdiLoadPlan>


         log.info('Exporting load-plans...')
         loadPlans.each { OdiLoadPlan object ->
            if(!loadPlanList || loadPlanList.collect{it.toLowerCase()}.contains(object.name.toLowerCase())) {
               exportObject(object, exportDir.canonicalPath)
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

      if ( !loadPlanList ) {
         // execute the export stage process
         exportStageDir()
      } else {
         // execute the export stage process without deleted objects
         exportStageDir(false)
      }

   }
}