package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiFolder
import org.gradle.api.tasks.TaskAction

@Slf4j
class ExportObjectsTask extends ExportTask {

   @TaskAction
   def exportProjectObjects() {

      log.debug "sourcePath: ${sourcePath}"
      log.debug "sourceBase: ${sourceBase}"

      instance.connect()

      log.debug "All projects: ${instance.projectFinder.findAll().toString()}"

      // create the export list
      def export = []

      // Validate project and folder
      if (!instance.findProjectName(projectCode)) {

         log.warn "Project Code '${projectCode}' does not exist."

      } else if (!instance.findFoldersProject(projectCode)[0]) {

         log.warn "No Folders founded in the Project '${projectCode}'..."

      } else {
         //We have Folders! Let's go ahead and collect all the existing objects folder by folder
         def folders = instance.findFoldersProject(projectCode)

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

      export.each {

         exportObject(
                 it.object,
                 "${sourceBase.canonicalPath}/${it.folder}",
                 true,
                 true
         )
      }

      instance.endTxn()
   }
}