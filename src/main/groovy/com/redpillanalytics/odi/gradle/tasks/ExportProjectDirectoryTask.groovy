package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportProjectDirectoryTask extends DefaultTask {

   /**
    * The ODI project code to export. Default: value of 'obi.projectName', or the name of the project subdirectory.
    */
   @Input
   @Option(option = "project-code",
           description = "The ODI project code to export. Default: value of 'obi.projectName', or the name of the project subdirectory.")
   String projectCode

   /**
    * The individual ODI Design folder to export. Default: all folders.
    */
   @Input
   @Optional
   @Option(option = "folder-name",
           description = "The individual ODI Design folder to export. Default: all folders."
   )
   String folderName
}
