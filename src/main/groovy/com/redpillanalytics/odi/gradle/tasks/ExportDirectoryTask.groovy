package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportDirectoryTask extends ExportTask {

   /**
    * The base directory to export content to. Default: 'src/main/project' for ODI projects, and 'src/main/model' for ODI models.
    */
   @Input
   @Optional
   @Option(option = "source-dir",
           description = "The base directory to export content to. Default: 'src/main/project' for ODI projects, and 'src/main/model' for ODI models."
   )
   String sourceDir

   @OutputDirectory
   File getExportDir() {
      return sourceDir ? project.file(sourceDir) : buildDir
   }
}
