package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportProjectDirectoryTask extends ImportDirectoryTask {

   @Input
   String category = 'project'

   /**
    * Gets the hierarchical collection of XML files, sorted using folder structure and file name prefix logic.
    *
    * @return The List of export files.
    */
   @Internal
   List getImportFiles(String filePrefix) {

      def result = new LinkedList()

      result.addAll(project.fileTree(dir: importDir, include: "**/${filePrefix}_*.xml").toList())

      return result
   }

   @Internal
   List getImportProjectFiles() {

      def result = new LinkedList()

      result.addAll(project.fileTree(dir: importDir, include: "**/PROJ_*.xml").toList())

      return result
   }

   @Internal
   List getImportFolderFiles() {

      def result = new LinkedList()

      result.addAll(project.fileTree(dir: importDir, include: "**/FOLD_*.xml").toList())

      return result
   }

   @Internal
   List getImportKMFiles() {

      def result = new LinkedList()

      result.addAll(project.fileTree(dir: importDir, include: "**/KM_*.xml").toList())

      return result
   }

   @TaskAction
   def taskAction() {

      def projectFilePrefix = ['VAR', 'UFN', 'SEQ']

      def folderFilePrefix = ['TRT','REUMAP','MAP','PACK']

      // Import the Project Object
      importXmlFiles(importProjectFiles, importService.IMPORT_MODE_SYNONYM_INSERT_UPDATE)

      // Import the Project KM
      smartImportXmlFiles(importKMFiles)

      // Import the Project Objects
      projectFilePrefix.each {
         importXmlFiles(getImportFiles(it))
      }

      // Import the Project Folders
      smartImportXmlFiles(importFolderFiles)

      // Import the Project Folder Objects
      folderFilePrefix.each {
         importXmlFiles(getImportFiles(it), importService.IMPORT_MODE_SYNONYM_INSERT)
      }

   }

}
