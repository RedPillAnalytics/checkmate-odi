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

   @Internal
   Instance instance

   // setSourceBase is not used, but I added it to support Gradle Incremental Build support
   @OutputDirectory
   def getSourceBase() {

      return project.file(sourcePath)
   }

   @TaskAction
   def exportProject() {

      log.debug "sourcePath: ${sourcePath}"
      log.debug "sourceBase: ${sourceBase}"

      instance.connect()

      // let's make sure the project exists
      log.debug "All projects: ${instance.projectFinder.findAll().toString()}"

      //Find The Target Project by the Project Code Value
      List<ISmartExportable> projectList = new LinkedList<ISmartExportable>()

      projectList.add(((IOdiProjectFinder) instance.getProjectFinder()).findByCode(projectCode))

      instance.beginTxn()

      log.debug "sourcePath: $sourcePath"
      log.debug "sourceBase: $sourceBase"
      log.debug "projectCode: $projectCode"

      new SmartExportServiceImpl(instance.odi).exportToXml(
              projectList,
              sourceBase.canonicalPath,
              projectCode,
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
