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
   List getImportFiles(String filePrefix) {

      def result = new LinkedList()

      result.addAll(project.fileTree(dir: importDir, include: "**/${filePrefix}_*.xml").toList())

      return result
   }

   @TaskAction
   def taskAction() {

      //Make the Connection
      instance.connect()

      def projectFilePrefix = ['VAR', 'UFN', 'SEQ']

      def folderFilePrefix = ['TRT','REUMAP','MAP','PACK']

      // Import the Project Object
      importXmlFiles(getImportFiles('PROJ'))

      // Smart Import the Project KMs
      smartImportXmlFiles(getImportFiles('KM'))

      // Import the Project Objects
      projectFilePrefix.each {
         importXmlFiles(getImportFiles(it))
      }

      // Smart Import the Project Folders
      smartImportXmlFiles(getImportFiles('FOLD'))

      // Import the Project Folder Objects
      folderFilePrefix.each {
         importXmlFiles(getImportFiles(it))
      }

      // Close the Connection
      instance.close()

   }

}
