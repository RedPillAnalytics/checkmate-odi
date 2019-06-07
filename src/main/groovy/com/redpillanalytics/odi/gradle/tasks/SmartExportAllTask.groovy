package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiProject
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
class SmartExportAllTask extends DefaultTask {

   @Internal
   Instance instance

   @Input
   @Option(option = "source-path",
           description = "The path to the export location. Defaults to the 'sourceBase' parameter value.")
   String sourcePath

   @OutputDirectory
   def getSourceBase() {

      return project.file(sourcePath)
   }

   @TaskAction
   def exportAllProjects() {

      instance.connect()

      def exportService = new SmartExportServiceImpl(instance.odi)
      def encdOption = new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9")

      //Finding all Projects and save into a smartExportList
      List<OdiProject> projects = instance.projects

      log.debug "projects: ${projects.toString()}"

      instance.beginTxn()

      projects.each {
         OdiProject project ->
            List<ISmartExportable> smartExportList = new LinkedList<ISmartExportable>()
            smartExportList.add(project)
            exportService.exportToXml(
                    smartExportList,
                    sourceBase.path,
                    project.getName(),
                    true,
                    false, encdOption,
                    false,
                    null,
                    null,
                    true
            )
      }

      instance.endTxn()
   }
}