package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.smartie.ISmartExportable
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportProjectFileTask extends ExportTask {

   /**
    * The ODI project code to export. Default: value of 'obi.projectName', or the name of the project subdirectory.
    */
   @Input
   @Option(option = "project-code",
           description = "The ODI project code to export. Default: value of 'obi.projectName', or the name of the project subdirectory.")
   String projectCode

   @Internal
   String category = 'file'

   /**
    * The file to export content to. Default: 'src/main/file/FILE_<PROJECTCODE>.xml'.
    */
   @Input
   @Optional
   @Option(option = "source-file",
           description = "The file to export content to. Default: 'src/main/file/FILE_<PROJECTCODE>'."
   )
   String sourceFile

   @OutputFile
   File getExportFile() {
      return sourceFile ? project.file(sourceFile) : project.file("${sourceBase}/${category}/${projectCode}")
   }

   @TaskAction
   def exportProjectFile() {

      instance.connect()

      log.debug "All projects: ${instance.projectFinder.findAll()}"

      def projectList = new LinkedList<ISmartExportable>()

      projectList.add(instance.findProject(projectCode, false))

      instance.beginTxn()

      projectList.each { project ->
         smartExportObject(project, project.parent,'FILE', project.name)
      }

      instance.endTxn()
   }
}
