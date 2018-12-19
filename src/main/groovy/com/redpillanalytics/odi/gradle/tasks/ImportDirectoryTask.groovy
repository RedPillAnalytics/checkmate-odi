package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.impexp.support.ImportServiceImpl
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportDirectoryTask extends ImportTask {

   @Input
   String category = 'odi'

   /**
    * The base directory to import content from. Default: 'src/main/project' for ODI projects, and 'src/main/model' for ODI models.
    */
   @Input
   @Optional
   @Option(option = "source-dir",
           description = "The base directory to import content from. Default: 'src/main/project' for ODI projects, and 'src/main/model' for ODI models."
   )
   String sourceDir

   @InputDirectory
   File getImportDir() {

      log.debug "sourceBase: $sourceBase"

      if (sourceDir) {
         File dir = new File(sourceBase, sourceDir)
         return dir.exists() ? dir : project.file(sourceDir)
      } else {
         return sourceBase
      }
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
    * Smart Imports all objects returned by the {@link #getImportFiles} FileTree object.
    */
   @Internal
   def smartImportXmlFiles() {

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

   /**
    * Imports all objects returned by the {@link #getImportFiles} FileTree object.
    */
   @Internal
   def importXmlFiles() {

      //Make the Connection
      instance.connect()
      instance.beginTxn()

      importFiles.each { file ->
         importService.importObjectFromXml(
                 ImportServiceImpl.IMPORT_MODE_SYNONYM_INSERT_UPDATE,
                 file.canonicalPath,
                 false,
                 null,
                 true,
         )
      }
      instance.endTxn()
   }

   @TaskAction
   def taskAction() {
      smartImportXmlFiles()
   }
}
