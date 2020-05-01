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

      if (!sourceDir) {
         return sourceBase
      } else {
         File dir = new File(sourceDir)
         return dir.exists() ? project.file(sourceDir) : project.file(sourceBase)
      }
   }

   /**
    * Gets the hierarchical collection of XML files, sorted using folder structure and file name prefix logic.
    *
    * @return The List of export files.
    */
   List getImportFiles(String filePrefix) {

      def result = new LinkedList()

      result.addAll(project.fileTree(dir: importDir, include: "**/${filePrefix}_*.xml").toList())

      return result
   }

   /**
    * Smart Import the File List of Objects.
    */
   def smartImportXmlFiles(List<File> smartImportFiles) {

      smartImportFiles.each { file ->
         // Begin the transaction
         instance.beginTxn()
         // Import the object
         log.info "Importing file '$file.canonicalPath'..."
         smartImportObject(file)
         // End the transaction
         instance.endTxn()
      }

   }

   /**
    * Import the File List of Objects.
    */
   def importXmlFiles(List<File> importFiles, int importMode = ImportServiceImpl.IMPORT_MODE_SYNONYM_INSERT_UPDATE) {

      importFiles.each { file ->
         // Begin the transaction
         instance.beginTxn()
         // Import the object
         importObject(file, importMode)
         // End the transaction
         instance.endTxn()
      }

   }

   /**
    * Import Topology from Folder.
    */
   def importTopologyDir() {

      // Begin the transaction
      instance.beginTxn()
      // Import the Topology
      importTopology(importDir.canonicalPath)
      // End the transaction
      instance.endTxn()

   }

}
