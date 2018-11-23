package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportTask extends DefaultTask {

   /**
    * The base directory to import content from. Default: value of 'obi.sourceBase' or 'src/main/odi'.
    */
   @Input
   @Option(option = "source-path",
           description = "The base directory to export content to. Default: value of 'obi.sourceBase' or 'src/main/odi'."
   )
   String sourcePath

   @Internal
   Instance instance

   @InputDirectory
   def getSourceBase() {
      return project.file("${project.extensions.odi.sourceBase}/$sourcePath")
   }

   @InputFile
   File getImportFile() {
      return project.file("${sourceBase}.xml")
   }
}
