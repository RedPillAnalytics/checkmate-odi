package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class SmartImportTask extends DefaultTask {

   @Internal
   Instance instance

   @Input
   @Option(option = "sourcePath",
           description = "The path to the source file to import. Defaults to the 'sourceBase' parameter value.")
   String sourcePath

   // setImportFile() is not used, but I added it for Gradle incremental build support
   @InputFile
   def getImportFile() {
      def file = project.file('sourcePath')

      if (file.directory) throw new GradleException("'${sourcePath}' points to a directory")

      //if (!(file.name =~ /\.xml/)) throw new GradleException("'${sourcePath}' does not appear to be an XML file.")

      return file
   }

   // setSourceBase() is not used, but I added it for Gradle incremental build support
   @InputDirectory
   def getSourceBase() {

      return getImportFile().getParentFile()
   }


   @TaskAction
   def importProject() {

      log.debug "sourcePath: ${sourcePath}"
      log.debug "sourceBase: ${sourceBase}"

      //Make the Connection
      instance.connect()

      instance.beginTxn()

      new SmartImportServiceImpl(instance.odi).importObjectsFromXml(
              getImportFile().canonicalPath,
              null,
              false,
      )

      instance.endTxn()

   }
}

