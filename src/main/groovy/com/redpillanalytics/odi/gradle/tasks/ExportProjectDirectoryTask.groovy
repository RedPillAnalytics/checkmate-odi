package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiFolder
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportProjectDirectoryTask extends ExportDirectoryTask {

   @Internal
   List objectMaster = ['variable', 'sequence', 'knowledge-module', 'user-function', 'reusable-mapping', 'mapping', 'procedure', 'package']

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
   String category = 'project'

   @TaskAction
   def exportObjects() {

      objectList.each { object ->
         assert "'object' must be one of '${objectMaster.toString()}'." && objectMaster.contains(object)
      }

      instance.connect()

      log.debug "All projects: ${instance.projectFinder.findAll().toString()}"

      def folders = folderName ? instance.findFolder(folderName, projectCode, false) : instance.findFoldersProject(projectCode, false)

      Integer count = 0

      objectList.each { objectType ->

         log.info "Exporting ${objectType}s..."

         // capture the class name to use
         // fancy regex... but all of this is to make 'reusable-mapping' == 'ReusableMapping'
         def finder = objectType.replaceAll(~/([^-]+)(?:-)?(\w)?(.+)/) { String all, String first, String capital, String rest ->
            "find${first.capitalize()}${capital ? capital.toUpperCase() : ''}$rest"
         }

         // begin the transaction
         instance.beginTxn()

         // export the project objects
         if (['variable', 'sequence', 'knowledge-module', 'user-function'].contains(objectType)) {
            instance."$finder"(projectCode).each { object ->
               if (!nameList || nameList.contains(object.name)) {
                  count++
                  logger.debug "object name: ${object.name}"
                  exportObject(object, "${exportDir.canonicalPath}/${objectType}", true)
                  //smartExportObject(object, "${exportDir.canonicalPath}/${objectType}", object.name)
               }
            }
         }
         else {
            // export the folder objects
            folders.each { OdiFolder folder ->
               instance."$finder"(projectCode, folder.name).each { object ->
                  if (!nameList || nameList.contains(object.name)) {
                     count++
                     logger.debug "object name: ${object.name}"
                     exportObject(object, "${exportDir.canonicalPath}/${folder.name}/${objectType}", true)
                     //smartExportObject(object, "${exportDir.canonicalPath}/${folder.name}/${objectType}", object.name)
                  }
               }
            }
         }
      }

      instance.endTxn()
      if (count == 0) throw new Exception("No project objects match provided filters; folder: ${folderName?:'<none>'}; object types: ${objectList}")
   }
}
