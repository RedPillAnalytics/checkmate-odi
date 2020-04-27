package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ExportLoadPlanDirectoryTask extends ExportDirectoryTask {

   // specify the load-plan subdirectory
   String category = 'load-plan'

   @Internal
   Instance instance

   @SuppressWarnings("GroovyAssignabilityCheck")
   @TaskAction
   def exportLoadPlans() {

      instance.connect()

      try{

         instance.beginTxn()

         instance.findAllLoadPlans().each {
            exportObject(it, exportDir.canonicalPath)
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

      // execute the export stage process
      exportStageDir()

   }
}