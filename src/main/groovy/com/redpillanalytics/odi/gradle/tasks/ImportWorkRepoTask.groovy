package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.impexp.support.ImportServiceImpl
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportWorkRepoTask extends ImportDirectoryTask {

   @Input
   String category = 'odi'

   /**
    * Overrides {@link #taskAction} in {@code ImportDirectoryTask}.
    */
   @TaskAction
   def taskAction() {
      //Make the Connection
      instance.connect()

      importXmlFiles()
   }
}
