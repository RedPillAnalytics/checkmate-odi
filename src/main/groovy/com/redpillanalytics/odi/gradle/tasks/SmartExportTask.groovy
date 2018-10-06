package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiProject
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.smartie.ISmartExportable
import oracle.odi.impexp.smartie.impl.SmartExportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class SmartExportTask extends DefaultTask {

   @Input
   @Option(option = "source-path",
           description = "The path to the export location. Defaults to the 'sourceBase' parameter value.")
   String sourcePath

   @Input
   @Option(option = "project-code",
           description = "The code of the project to create.")
   String projectCode

   @Input
   @Optional
   @Option(option = "project-name",
           description = "The project name to export. Defaults to either 'projectName' or the subdirectory name in SCM.")
   String projectName

   @Internal
   Instance instance

   // setSourceBase is not used, but I added it to support Gradle Incremental Build support
   @OutputDirectory
   def getSourceBase() {

      return project.file(sourcePath)
   }

   @Internal
   def getProjectName() {

      //instance.connect()
      return projectName ?: instance.findProjectName(projectCode)
   }

   @TaskAction
   def exportProject() {

      log.debug "sourcePath: ${sourcePath}"
      log.debug "sourceBase: ${sourceBase}"

      instance.connect()

      //Find The Target Project by the Project Code Value
      List<ISmartExportable> projectList = new LinkedList<ISmartExportable> ()

      projectList.add(((IOdiProjectFinder) instance.getProjectFinder()).findByCode(projectCode))

      instance.beginTxn()
      def encoding = new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9")

      if (encoding) {
         log.warn "encoding: $encoding"
      }
      log.warn "sourcePath: $sourcePath"
      log.warn "sourceBase: $sourceBase"

      new SmartExportServiceImpl(instance.odi).exportToXml(
              projectList,
              sourceBase.canonicalPath,
              projectName,
              true,
              false,
              encoding,
              false,
              null,
              null,
              true
      )

      instance.endTxn()

   }
}
