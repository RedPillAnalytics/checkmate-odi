package com.redpillanalytics.odi.gradle.containers

import groovy.util.logging.Slf4j

@Slf4j
class BuildGroupContainer {

   /**
    * The name of the container entity.
    */
   String name

   BuildGroupContainer(String name) {

      // set the name of the container
      this.name = name
   }

   // Build Group defaults
   private static final String CURRENT_BUILD_NAME = 'current'

   // capture the debug status
   Boolean isDebugEnabled = log.isDebugEnabled()

   def getDomainName() {

      return ((getClass() =~ /\w+$/)[0] - "Container")
   }

   def logTaskName(String task) {

      log.debug "${getDomainName()}: $name, TaskName: $task"

   }

   def isCurrentTaskName(String buildName) {

      return (buildName == CURRENT_BUILD_NAME) ? true : false

   }

   def getTaskName(String baseTaskName) {

      // return either the baseTaskName or prepend with a name
      String taskName = isCurrentTaskName(getName()) ? baseTaskName : getName() + baseTaskName.capitalize()

      logTaskName(taskName)

      return taskName


   }

}
