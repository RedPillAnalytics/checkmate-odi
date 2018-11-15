package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportTask extends DefaultTask {

   /**
    * The base directory to export content to. Default: value of 'obi.sourceBase' or 'src/main/odi'.
    */
   @Input
   @Option(option = "source-path",
           description = "The base directory to export content to. Default: value of 'obi.sourceBase' or 'src/main/odi'."
   )
   String sourcePath

   @Input
   @Option(option = "project-code",
           description = "The ODI project code to export. Default: value of 'obi.projectName', or the name of the project subdirectory.")
   String projectCode

   @Internal
   Instance instance

   @OutputDirectory
   def getSourceBase() {

      return project.file(sourcePath)
   }

   @Internal
   getExportList() {
      return new LinkedList<IExportable>()
   }
}