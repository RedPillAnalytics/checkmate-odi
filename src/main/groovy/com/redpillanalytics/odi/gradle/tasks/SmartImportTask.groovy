package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class SmartImportTask extends DefaultTask {

   @Input
   @Option(option = "url",
           description = "The JDBC URL of the Master Repository.")
   String url

   @Input
   @Option(option = "driver",
           description = "The JDBC driver class of the Master Repository.")
   String driver

   @Input
   @Option(option = "master",
           description = "The schema name of the Master repository.")
   String master

   @Input
   @Option(option = "work",
           description = "The schema name of the Work repository.")
   String work

   @Input
   @Option(option = "masterPass",
           description = "The password for the Master repository.")
   String masterPass

   @Input
   @Option(option = "odi",
           description = "The name of the ODI user.")
   String odi

   @Input
   @Option(option = "odiPass",
           description = "The password of the ODI user.")
   String odiPass

   @Input
   @Option(option = "sourcePath",
           description = "The path to the source file to import. Defaults to the 'sourceBase' parameter value.")
   String sourcePath

   // setImportFile() is not used, but I added it for Gradle incremental build support
   @InputFile
   def getImportFile() {
      def file = project.file('sourcePath')

      if (file.directory) throw new GradleException("'${sourcePath}' points to a directory")

      if (!(file.name =~ /\.xml/)) throw new GradleException("'${sourcePath}' does not appear to be an XML file.")

      return file
   }

   // setSourceBase() is not used, but I added it for Gradle incremental build support
   @InputDirectory
   def getSourceBase() {

      return getImportFile().getParentFile()
   }


   @TaskAction
   def importProject() {

      def instance = new Instance(url, driver, master, work, masterPass, odi, odiPass)
      def importService = new SmartImportServiceImpl(instance.odi)

      instance.beginTxn()

      importService.importObjectsFromXml(getImportFile().canonicalPath, null, false)

      instance.endTxn()

   }
}
