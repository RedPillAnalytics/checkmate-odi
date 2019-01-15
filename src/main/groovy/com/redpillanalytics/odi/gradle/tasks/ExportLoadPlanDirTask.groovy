package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ExportLoadPlansDirTask extends ExportDirectoryTask {

   // specify the model subdirectory
   String category = 'model'

   @Internal
   Instance instance

   @SuppressWarnings("GroovyAssignabilityCheck")
   @TaskAction
   def exportLoadPlans() {

      instance.connect()
      instance.beginTxn()

      instance.findAllLoadPlans().each {
         exportObject(it, sourceBase.canonicalPath, false)
      }
      instance.endTxn()
   }
}