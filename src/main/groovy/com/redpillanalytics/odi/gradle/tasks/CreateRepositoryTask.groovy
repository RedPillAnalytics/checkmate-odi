package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.OdiRepository
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class CreateRepositoryTask extends DefaultTask {

   @Input
   @Option(option = "no-overwrite",
           description = "When enabled, do not overwrite existing Master and Work repositories when creating new ones.")
   boolean overwrite

   @Internal
   OdiRepository repository

   @TaskAction
   def createRepository() {

      repository.createMaster(overwrite)

   }
}