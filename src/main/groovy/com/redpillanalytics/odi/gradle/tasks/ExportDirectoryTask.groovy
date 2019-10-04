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
      return sourceDir ? project.file(sourceDir) : buildDir
   }

   @Internal
   def xmlDiff(File controlXml, File testXml) {

      Diff result = DiffBuilder.compare(org.xmlunit.builder.Input.fromFile(controlXml))
              .withTest(org.xmlunit.builder.Input.fromFile(testXml))
              .withNodeFilter({ node -> node.getNodeName() != "Encryption" })
              .build()

      return result.hasDifferences()

   }

   @Internal
   def exportStageDir() {

      // Find and remove the deleted objects in the source base comparing with the build objects
      project.fileTree(dir: sourceBase, include: "**/*.xml").toList().each { File srcFile ->
         if (!project.fileTree(dir: buildDir, include: "**/*.xml").collect {it.name}.contains(srcFile.name)) {
            ant.delete(file: srcFile)
         }
      }

      // Create buildDir and sourceBase xml files list to compare
      def buildList = project.fileTree(dir: buildDir, include: "**/*.xml").toList()
      log.debug("Build Dir List: ${buildList}")
      def sourceList = project.fileTree(dir: sourceBase, include: "**/*.xml").toList()
      log.debug("Source base List: ${sourceList}")

      buildList.each { buildFile ->
         // Compare the XML files and if the file change copy from Build to Source Base
         sourceList.each { sourceFile ->
            if(buildFile.name == sourceFile.name) {
               if(xmlDiff(buildFile, sourceFile)) {
                  ant.copy(file: buildFile.canonicalPath,
                          tofile: sourceFile.canonicalPath,
                          overwrite: true)
                  println("Copying file: ${buildFile.name} ...")
               }

            }
         }
         // If the XML file does not exist in Source Base copy from Build to Source Base
         ant.copy(file: buildFile.canonicalPath,
                 tofile: "${sourceBase}/${buildFile.canonicalPath.minus(buildDir.canonicalPath)}",
                 overwrite: true)
      }

      // Delete buildDir when all the files are processed
      ant.delete(dir: buildDir)
   }

}
