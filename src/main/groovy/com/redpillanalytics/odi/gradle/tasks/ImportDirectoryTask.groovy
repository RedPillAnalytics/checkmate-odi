package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportDirectoryTask extends ImportTask {

   /**
    * The base directory to import content from. Default: value of 'obi.sourceBase' or 'src/main/odi'.
    */
   @Input
   @Option(option = "source-dir",
           description = "The base directory to import content from. Default: value of 'obi.sourceBase' or 'src/main/odi'."
   )
   String sourceDir

   @InputDirectory
   File getImportDir() {
      File dir = project.file("${project.extensions.odi.sourceBase}/$importDir")

      return dir.exists() ? dir : project.file(sourceDir)
   }

   /**
    * Gets the hierarchical collection of export files, sorted using folder structure and alphanumeric logic.
    *
    * @return The List of export files.
    */
   @Internal
   List getImportFiles() {

      def tree = project.fileTree(dir: importDir, includes: ['**/*.xml', '**/*.XML'])

      return tree.sort()
   }
}
