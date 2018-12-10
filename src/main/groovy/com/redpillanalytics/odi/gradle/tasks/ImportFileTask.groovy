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
class ImportFileTask extends ImportTask {

   /**
    * The base directory to import content from. Default: value of 'obi.sourceBase' or 'src/main/odi'.
    */
   @Input
   @Option(option = "source-file",
           description = "The import."
   )
   String sourceFile

   @InputFile
   File getImportFile() {
      File file = project.file("${project.extensions.odi.sourceBase}/$sourceFile")

      return file.exists() ? file : project.file(sourceFile)
   }
}
