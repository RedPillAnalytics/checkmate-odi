package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportProjectDirectoryTask extends ImportDirectoryTask {

   @Input
   String category = 'project'

   @Internal
   Instance instance

   /**
    * The ODI project code to import. Default: value of 'odi.projectName', or the name of the project subdirectory.
    */
   @Input
   @Option(option = "project-code",
           description = "The ODI project code to import. Default: value of 'odi.projectName', or the name of the project subdirectory.")
   String projectCode

   @TaskAction
   def taskAction() {

      //Make the Connection
      instance.connect()

      try {

         def projectFilePrefix = ['VAR', 'UFN', 'SEQ']

         def folderFilePrefix = ['TRT','REUMAP','MAP','PACK']

         // Import the Project Object
         if (!instance.findProject(projectCode, true)) {
            log.info("Importing Project ${projectCode}...")
            importXmlFiles(getImportFiles('PROJ'))
         } else {
            log.info("Found project ${projectCode}...")
         }

         // Smart Import the Project KMs
         log.info("Importing knowledge-modules...")
         smartImportXmlFiles(getImportFiles('KM'))

         // Import the Project Objects
         projectFilePrefix.each {

            switch(it) {
               case 'VAR':
                  log.info("Importing variables...")
                  break
               case 'UFN':
                  log.info("Importing user-functions...")
                  break
               case 'SEQ':
                  log.info("Importing sequences...")
                  break
            }

            importXmlFiles(getImportFiles(it))

         }

         // Smart Import the Project Folders
         log.info("Importing folders...")
         smartImportXmlFiles(getImportFiles('FOLD'))

         // Import the Project Folder Objects
         folderFilePrefix.each {

            switch(it) {
               case 'TRT':
                  log.info("Importing procedures...")
                  break
               case 'REUMAP':
                  log.info("Importing reusable-mappings...")
                  break
               case 'MAP':
                  log.info("Importing mappings...")
                  break
               case 'PACK':
                  log.info("Importing packages...")
                  break
            }

            importXmlFiles(getImportFiles(it))

         }

         // Close the Connection
         instance.close()

      } catch(Exception e) {
         // End the Transaction
         instance.endTxn()
         // Close the Connection
         instance.close()
         // Throw the Exception
         throw e
      }

   }

}
