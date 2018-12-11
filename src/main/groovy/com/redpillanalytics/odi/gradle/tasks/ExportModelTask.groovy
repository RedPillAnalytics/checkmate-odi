package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.model.OdiModel
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.support.ExportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportModelTask extends DefaultTask {

   @Input
   @Option(option = "source-path",
           description = "The path to the export location. Defaults to the 'sourceBase' parameter value.")
   String sourcePath

   @Input
   @Option(option = "model-code",
           description = "The code of the model to export.")
   String modelCode

   @Internal
   Instance instance

   // setSourceBase is not used, but I added it to support Gradle Incremental Build support
   @OutputDirectory
   def getSourceBase() {

      return project.file(sourcePath)
   }

   @TaskAction
   def exportModel() {

      log.debug "sourcePath: ${sourcePath}"
      log.debug "sourceBase: ${sourceBase}"

      instance.connect()

      //Creating OdiModelObject
      OdiModel model

      log.debug "All Models: ${instance.modelFinder.findAll().toString()}"

      if (!instance.findModelbyCode(modelCode)) {

         log.warn "Cannot Find Model '${modelCode}' ..."

      } else {
         //We have Model a Model
         model = instance.findModelbyCode(modelCode)
         log.info "Exporting Model ${model.name} ..."

         instance.beginTxn()

         new ExportServiceImpl(this.instance.odi).exportToXmlWithParents(
                 model,
                 sourceBase.canonicalPath,
                 true,
                 true,
                 new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9"),
                 null,
                 true,
         )

         instance.endTxn()
      }
   }
}