package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.options.Option
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.diff.Diff

@Slf4j
class ExportDirectoryTask extends ExportTask {

   /**
    * The base directory to export content to. Default: 'src/main/project' for ODI projects, and 'src/main/model' for ODI models.
    */
   @Input
   @Optional
   @Option(option = "source-dir",
           description = "The base directory to export content to. Default: 'src/main/project' for ODI projects, and 'src/main/model' for ODI models."
   )
   String sourceDir

   @OutputDirectory
   File getExportDir() {
      return sourceDir ? project.file(sourceDir) : buildDir ? buildDir : sourceBase
   }

   static def xmlDiff(File controlXml, File testXml) {
      Diff result = DiffBuilder.compare(org.xmlunit.builder.Input.fromFile(controlXml))
              .withTest(org.xmlunit.builder.Input.fromFile(testXml))
              .withNodeFilter({
                 node ->
                    node.getNodeName() != "Encryption" &&
                            node.getNodeName() != "Admin" &&
                            node.getAttributes()?.getNamedItem('name')?.textContent != 'LastDate'
              })
              .ignoreWhitespace()
              .build()
      log.debug(result.toString())
      return result.hasDifferences()
   }

   def exportStageDir(Boolean deleteObjects = true) {

      if (deleteObjects) {

         // Find and remove the deleted objects in the source base comparing with the build objects
         project.fileTree(dir: sourceBase, include: "**/*.xml").toList().each { File srcFile ->
            if (!project.fileTree(dir: buildDir, include: "**/*.xml").collect {it.name}.contains(srcFile.name)) {
               ant.delete(file: srcFile)
               log.info("- File ${srcFile.name} deleted")
            }
         }

      }

      // Create buildDir and sourceBase xml files list to compare
      def buildList = project.fileTree(dir: buildDir, include: "**/*.xml").toList()
      log.debug("Build Dir List: ${buildList}")
      def sourceList = project.fileTree(dir: sourceBase, include: "**/*.xml").toList()
      log.debug("Source base List: ${sourceList}")
      Boolean flag

      buildList.each { buildFile ->

         // Compare the xml files and if the file change copy from buildDir to buildDir
         def sourceFile = sourceList.find({File sourceFile -> sourceFile.name == buildFile.name})

         if(sourceFile) {
            // If XML file exist in Source Base do the XML Diff and if changed copy from buildDir to sourceBase
            if (xmlDiff(buildFile, sourceFile)) {
               log.info("- File ${buildFile.name} changed")
               ant.copy(file: buildFile.canonicalPath,
                       tofile: sourceFile.canonicalPath,
                       overwrite: true)
            } else {
               log.info("- File ${buildFile.name} not changed")
            }
         } else {
            // If the XML file does not exist in Source Base copy from from buildDir to sourceBase
            ant.copy(file: buildFile.canonicalPath,
                    tofile: "${sourceBase}/${(buildFile.canonicalPath - buildDir.canonicalPath)}",
                    overwrite: true)
            log.info("- File ${buildFile.name} created")
         }

      }

      // Delete buildDir when all the files are processed
      try{
         ant.delete(dir: buildDir)
      } catch(Exception e ) { log.debug("Cannot delete ${buildDir}") }

   }

}
