package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ImportProjectDirectoryTask extends ImportDirectoryTask {

   @Input
   String category = 'project'

   /**
    * Gets the hierarchical collection of XML files, sorted using folder structure and file name prefix logic.
    *
    * @return The List of export files.
    */
   @Internal
   List getImportFiles() {

      def filePrefix = ['PROJ', 'VAR', 'UFN', 'SEQ', 'KM','FOLD','TRT','REUMAP','MAP','PACK']

      def result = new LinkedList()

      filePrefix.each {
         result.addAll(project.fileTree(dir: importDir, include: "**/${it}_*.xml").toList())
      }

      return result
   }

   /**
    * Gets the hierarchical collection of XML files, sorted using folder structure and file name prefix logic.
    *
    * @return The List of export files.
    */
   @Internal
   List getSmartImportFiles() {

      def filePrefix = ['KM']

      def result = new LinkedList()

      filePrefix.each {
         result.addAll(project.fileTree(dir: importDir, include: "**/${it}_*.xml").toList())
      }

      return result
   }

   @TaskAction
   def taskAction() {
      smartImportXmlFiles(smartImportFiles)
      importXmlFiles(importFiles)
   }

}
