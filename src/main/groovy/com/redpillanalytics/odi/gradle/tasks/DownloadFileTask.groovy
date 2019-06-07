package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class DownloadFileTask extends DefaultTask {

   DownloadFileTask(){
      description = "Download file from '$url' to '$filePath'."
   }

   /**
    * The path to download the ODI API zip file to.
    */
   @Input
   @Option(option = "file-path",
           description = "The path to download the ODI API zip file to."
   )
   String filePath

   @Input
   String url

   @OutputFile
   File getFile() {
      return project.file(filePath)
   }

   @TaskAction
   void download() {
      ant.get(src: url, dest: getFile())
   }
}
