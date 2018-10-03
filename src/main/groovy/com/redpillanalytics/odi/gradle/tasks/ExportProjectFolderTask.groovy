package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.smartie.ISmartExportable
import oracle.odi.impexp.smartie.impl.SmartExportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportProjectFolderTask extends DefaultTask {

   @Input
   @Option(option = "source-path",
           description = "The path to the export location. Defaults to the 'sourceBase' parameter value.")
   String sourcePath

   @Input
   @Option(option = "project-name",
           description = "The project name to export. Defaults to either 'projectName' or the subdirectory name in SCM.")
   String pname

   @Input
   @Option(option = "folder-name",
           description = "The target folder name containing the objects to export from the ODI Repository for the SmartExport.")
   String folder

   @Internal
   Instance instance

   // setSourceBase is not used, but I added it to support Gradle Incremental Build support
   @OutputDirectory
   def getSourceBase() {

      return project.file(sourcePath)
   }

   @TaskAction
   def exportProjectFolder() {

      log.debug "sourcePath: ${sourcePath}"
      log.debug "sourceBase: ${sourceBase}"

      instance.connect()

      // create the export list
      List<ISmartExportable> smartExportList = new LinkedList<ISmartExportable>()


      // Validate project and folder

      if (!instance.findProjectName(pname)) {

         log.warn "Project name '${pname}' not found."

      } else if (!instance.findFolder(folder, pname)[0]) {

         log.warn "Folder name '${folder}' not found."

      } else {

         // list the mappings
         instance.findMapping(pname, folder).each {
            smartExportList.add((ISmartExportable) it)
            log.info "Mapping ${it.name} added to export list..."
         }

         // list the packages
         instance.findPackage(pname, folder).each {
            smartExportList.add((ISmartExportable) it)
            log.info "Package ${it.name} added to export list..."
         }

         // list the procedures
         instance.findProcedure(pname, folder).each {
            smartExportList.add((ISmartExportable) it)
            log.info "Procedure ${it.name} added to export list..."
         }
      }

      instance.beginTxn()

      new SmartExportServiceImpl(instance.odi).exportToXml(
              smartExportList,
              sourceBase.canonicalPath,
              pname,
              true,
              false,
              new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9"),
              false,
              null,
              null,
              true
      )

      instance.endTxn()
   }
}