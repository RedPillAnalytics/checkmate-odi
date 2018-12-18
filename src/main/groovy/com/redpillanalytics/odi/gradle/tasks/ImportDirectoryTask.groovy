package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportDirectoryTask extends ImportTask {

   @Input
   String category = 'odi'

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
      File dir = project.file("${getSourceBase()}/$sourceDir")
      return dir.exists() ? dir : project.file(sourceDir)
   }

   /**
    * Gets the hierarchical collection of XML files, sorted using folder structure and alphanumeric logic.
    *
    * @return The List of export files.
    */
   @Internal
   List getImportFiles() {
      def tree = project.fileTree(dir: importDir, include: '**/*.xml')
      return tree.sort()
   }

   /**
    * Imports all objects returned by the {@link #getImportFiles} FileTree object.
    */
   @TaskAction
   def importDir() {

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      importFiles.each { file ->
         smartImportService.importObjectsFromXml(
                 file.path,
                 null,
                 true,
         )
      }
      instance.endTxn()
   }
}
