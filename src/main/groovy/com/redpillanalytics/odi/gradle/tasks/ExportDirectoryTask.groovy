package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportDirectoryTask extends ExportTask {

   /**
    * The file to export content to. Default: value of 'obi.sourceBase' or 'src/main/odi' as the base directory, with the file named '<PROJECTCODE>.xml'.
    */
   @Input
   @Optional
   @Option(option = "folder-name",
           description = "The individual ODI Design folder to export. Default: all folders."
   )
   String folderName

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
