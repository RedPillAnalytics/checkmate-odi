package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportDirectoryTask extends ExportTask {

   /**
    * The base directory to export content to. Default: value of 'obi.sourceBase' or 'src/main/odi'.
    */
   @Input
   @Option(option = "source-dir",
           description = "The base directory to export content to. Default: value of 'obi.sourceBase' or 'src/main/odi'."
   )
   String sourceDir

   @OutputDirectory
   File getExportDir() {
      File dir = project.file("${project.extensions.odi.sourceBase}/$sourceDir")
      return dir.exists() ? dir : project.file(sourceDir)
   }
}
