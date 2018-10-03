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
   @Option(option = "sourcePath",
           description = "The path to the export location. Defaults to the 'sourceBase' parameter value.")
   String sourcePath

   @Input
   @Option(option = "pname",
           description = "The project name to export. Defaults to either 'projectName' or the subdirectory name in SCM.")
   String pname

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

      //Find The Target Project by the Project Code Value
      List<ISmartExportable> project = new LinkedList<ISmartExportable> ()
      project.add(((IOdiProjectFinder) instance.odi.getTransactionalEntityManager().getFinder(OdiProject.class)).findByCode(pname))

      instance.beginTxn()

      new SmartExportServiceImpl(instance.odi).exportToXml(
              project,
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
