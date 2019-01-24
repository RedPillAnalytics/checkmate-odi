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

   /**
    * The ODI project code to export. Default: value of 'obi.projectName', or the name of the project subdirectory.
    */
   @Input
   @Option(option = "project-code",
           description = "The ODI project code to export. Default: value of 'obi.projectName', or the name of the project subdirectory.")
   String projectCode

   /**
    * The type of ODI object to export.
    */
   @Input
   @Option(option = "object-type",
           description = "The type of ODI object to export."
   )
   String objectType = 'mapping'

   /**
    * The individual ODI Design folder to export. Default: all folders.
    */
   @Input
   @Optional
   @Option(option = "folder-name",
           description = "The individual ODI Design folder to export. Default: all folders."
   )
   String folderName

   @Internal
   String category = 'project'

   @TaskAction
   def exportObjects() {

      assert ['mapping', 'reusable-mapping', 'package', 'procedure'].contains(objectType)

      instance.connect()

      log.debug "All projects: ${instance.projectFinder.findAll().toString()}"

      def folders = folderName ? instance.findFolder(folderName, projectCode, false) : instance.findFoldersProject(projectCode, false)

      // capture the class name to use
      // fancy regex... but all of this is to make 'reusable-mapping' == 'ReusableMapping'
      def finder = objectType.replaceAll(~/([^-]+)(?:-)?(\w)?(.+)/) { String all, String first, String capital, String rest ->
         "find${first.capitalize()}${capital ? capital.toUpperCase() : ''}$rest"
      }

      // begin the transaction
      instance.beginTxn()

      folders.each { OdiFolder folder ->
         log.info "Exporting folder '$folder'..."
         instance."$finder"(projectCode, folder.name).each { object ->
            exportObject(object, "${exportDir.canonicalPath}/${folder.name}/${objectType}", true)
         }
      }

      instance.endTxn()
   }
}
