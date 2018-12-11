package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiFolder
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class StandardExportDirectoryTask extends ExportDirectoryTask {

   /**
    * The file to export content to. Default: value of 'obi.sourceBase' or 'src/main/odi' as the base directory, with the file named '<PROJECTCODE>.xml'.
    */
   @Input
   @Optional
   @Option(option = "project-folder",
           description = "The individual ODI project folder to export. Default: all folders."
   )
   String projectFolder

   @TaskAction
   def exportObjects() {

      instance.connect()

      log.debug "All projects: ${instance.projectFinder.findAll().toString()}"

      // create the export list
      def export = []

      // Validate project and folder
      if (!instance.findProjectName(projectCode)) {

         log.warn "Project Code '${projectCode}' does not exist."

      } else if (!instance.findFoldersProject(projectCode)[0]) {

         log.warn "No Folders found in the Project '${projectCode}'..."

      } else {
         //We have Folders! Let's go ahead and collect all the existing objects folder by folder
         def folders = instance.findFoldersProject(projectCode)

         log.warn "folders: ${folders.dump()}"

         // begin the transaction
         instance.beginTxn()

         // Loop through each folder
         folders.each { OdiFolder folder ->

            log.info "Exporting objects from  ${folder.name}..."

            instance.findMapping(projectCode, folder.name).each { object ->
               export << [object: object, folder: "${folder.name}/mappings"]
            }

            // list the reusable mappings
            instance.findReusableMapping(projectCode, folder.name).each { object ->
               export << [object: object, folder: "${folder.name}/reusable-mappings"]
            }

            // list the packages
            instance.findPackage(projectCode, folder.name).each { object ->
               export << [object: object, folder: "${folder.name}/packages"]
            }

            // list the procedures
            instance.findProcedure(projectCode, folder.name).each { object ->
               export << [object: object, folder: "${folder.name}/procedures"]
            }
         }
      }

      instance.beginTxn()

      export.each { object ->

         exportObject(
                 object.object,
                 "${exportDir.canonicalPath}/${object.folder}",
                 true,
                 true
         )
      }
      instance.endTxn()
   }
}
