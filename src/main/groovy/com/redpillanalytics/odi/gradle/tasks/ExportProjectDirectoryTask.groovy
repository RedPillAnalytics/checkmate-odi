package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import oracle.odi.domain.project.OdiFolder
import oracle.odi.impexp.smartie.ISmartExportable
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportProjectDirectoryTask extends ExportDirectoryTask {

   @Internal
   List objectMaster = ['knowledge-module', 'variable', 'sequence', 'user-function', 'reusable-mapping', 'mapping', 'procedure', 'package']

   /**
    * The ODI project code to export. Default: value of 'odi.projectName', or the name of the project subdirectory.
    */
   @Input
   @Option(option = "project-code",
           description = "The ODI project code to export. Default: value of 'odi.projectName', or the name of the project subdirectory.")
   String projectCode

   /**
    * The individual ODI Design folder to export. Default: all folders.
    */
   @Input
   @Optional
   @Option(option = "folder-name",
           description = "The individual ODI Design folder to export. Default: all folders."
   )
   String folderName

   /**
    * The type of ODI object to export. Default: all object types.
    */
   @Input
   @Optional
   @Option(option = "object-type",
           description = "The type of ODI object to export. Default: all object types."
   )
   List<String> objectList = objectMaster

   /**
    * The name of the ODI object to export. Default: all object names.
    */
   @Input
   @Optional
   @Option(option = "object-name",
           description = "The name of the ODI object to export. Default: all object names."
   )
   List<String> nameList

   @Internal
   Instance instance

   @Internal
   String category = 'project'

   @TaskAction
   def taskAction() {

      objectList.each { object ->
         assert "'object' must be one of '${objectMaster.toString()}'." && objectMaster.contains(object)
      }

      instance.connect()

      try {

         Integer count = 0

         // begin the transaction
         instance.beginTxn()

         // export the project
         log.info("Exporting Project ${projectCode}...")

         exportObject(instance.findProject(projectCode,false), "${exportDir.canonicalPath}", false,false)

         // get the project folders
         def folders = folderName ? instance.findFolder(folderName, projectCode, false) : instance.findFoldersProject(projectCode, false)

         // export the project folders
         folders.each { OdiFolder folder ->
            log.info("Exporting Folder ${folder.name}...")
            exportObject(folder, "${exportDir.canonicalPath}/folder/${folder.name}", true,false)
         }

         objectList.each { objectType ->

            log.info "Exporting ${objectType}s..."

            // capture the class name to use
            // fancy regex... but all of this is to make 'reusable-mapping' == 'ReusableMapping'
            def finder = objectType.replaceAll(~/([^-]+)(?:-)?(\w)?(.+)/) { String all, String first, String capital, String rest ->
               "find${first.capitalize()}${capital ? capital.toUpperCase() : ''}$rest"
            }

            // Export the project objects
            if(['knowledge-module'].contains(objectType)) {
               if (!nameList) {
                  // Export the knowledge modules
                  List<ISmartExportable> exportList = new LinkedList<ISmartExportable>()

                  instance."$finder"(projectCode).each { object ->
                     exportList.add(object as ISmartExportable)
                  }

                  smartExportList(exportList, "${exportDir.canonicalPath}/${objectType}", "KM", "Project_Knowledge_Modules")

               }

            } else if(['variable', 'sequence', 'user-function'].contains(objectType)) {

               instance."$finder"(projectCode).each { object ->
                  if (!nameList || nameList.collect{it.toLowerCase()}.contains(object.name.toLowerCase())) {
                     count++
                     logger.debug "object name: ${object.name}"
                     exportObject(object as IExportable, "${exportDir.canonicalPath}/${objectType}")
                  }
               }

            } else {

               // Export the folder objects
               folders.each { OdiFolder folder ->
                  // export the folder objects
                  logger.info("Folder ${folder.name}:")
                  instance."$finder"(projectCode, folder.name).each { object ->
                     if (!nameList || nameList.collect{it.toLowerCase()}.contains(object.name.toLowerCase())) {
                        count++
                        logger.debug "object name: ${object.name}"
                        exportObject(object as IExportable, "${exportDir.canonicalPath}/folder/${folder.name}/${objectType}")
                     }
                  }
               }

            }

         }

         instance.endTxn()

         instance.close()

         if (count == 0) throw new Exception("No project objects match provided filters; folder: ${folderName?:'<none>'}; object types: ${objectList}")

      } catch(Exception e) {
         // End the Transaction
         instance.endTxn()
         // Close the Connection
         instance.close()
         // Throw the Exception
         throw e
      }

      if ( objectList.size() == objectMaster.size() && !nameList && !folderName ) {
         // execute the export stage process
         exportStageDir()
      } else {
         // execute the export stage process without deleted objects
         exportStageDir(false)
      }

   }
}
