package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Internal

@Slf4j
class ImportProjectDirectoryTask extends ImportDirectoryTask {

   /**
    * Gets the hierarchical collection of XML files, sorted using folder structure and alphanumeric logic.
    *
    * @return The List of export files.
    */
   @Internal
   List getImportFiles() {
      def tree = project.fileTree(dir: importDir, include: '**/*.xml')
      return tree.sort()
   }
}
