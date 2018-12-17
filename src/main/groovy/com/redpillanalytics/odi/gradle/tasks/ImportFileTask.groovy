package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportFileTask extends ImportTask {

   /**
    * The file to import content from. Default: value of 'obi.sourceBase' or 'src/main/odi' as the base directory, with the file named '<PROJECTCODE>.xml'.
    */
   @Input
   @Option(option = "source-file",
           description = "The file to import content from. Default: value of 'obi.sourceBase' or 'src/main/odi' as the base directory, with the file named '<PROJECTCODE>.xml'."
   )
   String sourceFile

   @InputFile
   File getImportFile() {
      File file = project.file("${getSourceBase()}/$sourceFile")

      File returnFile = file.exists() ? file : project.file(sourceFile)
      log.info "import file: ${returnFile}"
      return returnFile
   }
}
