package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportFileTask extends ExportTask {

   /**
    * The file to export content to. Default: value of 'obi.sourceBase' or 'src/main/odi' as the base directory, with the file named '<PROJECTCODE>.xml'.
    */
   @Input
   @Option(option = "source-file",
           description = "The file to export content to. Default: value of 'obi.sourceBase' or 'src/main/odi' as the base directory, with the file named '<PROJECTCODE>.xml'."
   )
   String sourceFile

   @OutputFile
   File getExportFile() {
      File file = project.file("${project.extensions.odi.sourceBase}/$sourceFile")

      File returnFile = file.exists() ? file : project.file(sourceFile)
      //todo replace warn with info
      log.warn "Exportinging file: ${returnFile}"
      return returnFile
   }
}
