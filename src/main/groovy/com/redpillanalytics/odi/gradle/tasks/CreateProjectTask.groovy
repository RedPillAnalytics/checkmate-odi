package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Lifecycle
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option


@Slf4j
class CreateProjectTask extends DefaultTask {

   @Input
   @Option(option = "pname",
           description = "The name of the project to create.")
   String pname

   @Input
   @Option(option = "pcode",
           description = "The code of the project to create.")
   String pcode

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

   @TaskAction
   def createProject() {

      new Lifecycle(url, driver, master, work, masterPass, odi, odiPass).createProject(pname, pcode)

   }
}