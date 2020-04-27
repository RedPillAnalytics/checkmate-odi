package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportProjectDirectoryTask extends ImportDirectoryTask {

   @Input
   String category = 'project'

   /**
    * The ODI project code to import. Default: value of 'odi.projectName', or the name of the project subdirectory.
    */
   @Input
   @Option(option = "project-code",
           description = "The ODI project code to import. Default: value of 'odi.projectName', or the name of the project subdirectory.")
   String projectCode

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
      try{

         def projectFilePrefix = ['VAR', 'UFN', 'SEQ']

         def folderFilePrefix = ['TRT','REUMAP','MAP','PACK']

         // Import the Project Object
         if (!instance.findProject(projectCode, true)) {
            importXmlFiles(getImportFiles('PROJ'))
         }

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

      } catch(Exception e ) {
         // Close the Connection
         instance.close()
         // Throw the Exception
         throw e
      }

   }

}
