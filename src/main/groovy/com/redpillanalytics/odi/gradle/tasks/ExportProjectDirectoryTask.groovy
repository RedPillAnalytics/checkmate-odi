package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiFolder
import org.gradle.api.tasks.TaskAction

@Slf4j
class ExportProjectDirectoryTask extends ExportDirectoryTask {

   @TaskAction
   def exportObjects() {

      instance.connect()

      log.debug "All projects: ${instance.projectFinder.findAll().toString()}"

      // create the export list
      def export = []

      //We have Folders! Let's go ahead and collect all the existing objects folder by folder
      def folders = folderName ? instance.findFolder(folderName, projectCode) : instance.findFoldersProject(projectCode)

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
