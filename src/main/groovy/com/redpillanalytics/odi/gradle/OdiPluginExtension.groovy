package com.redpillanalytics.odi.gradle

import com.redpillanalytics.common.Utils
import groovy.util.logging.Slf4j

@Slf4j
class OdiPluginExtension {

   /**
    * The group name to use for all tasks. Default: 'Checkmate'.
    */
   String taskGroup = 'Checkmate ODI'

   /**
    * The base directory for all source objects.
    */
   String sourceBase = 'src/main'

   /**
    * When enabled, support ODI Design Projects in this Gradle project directory. Default is 'true'.
    */
   Boolean enableProjects = true

   /**
    * The name of the ODI project being built. Defaults to the directory name in source control. Required parameter.
    */
   String projectName

   /**
    * When specified, a Gradle subproject folder can be associated with a particular ODI repository folder. Default: NULL, or support all folders in project 'odi.projectName'.
    */
   String projectFolder

   /**
    * The code of the ODI project being built. Defaults to a normalized version of the project name.
    */
   String projectCode

   /**
    * When enabled, support ODI Design Models in this Gradle project directory. Default is 'true'.
    */
   Boolean enableModels = true

   /**
    * When specified, a Gradle subproject folder can be associated with a particular ODI work repository folder. Default: NULL, or support all folders in the ODI repository.
    */
   String modelFolder

   /**
    * The base ODI build directory, which exists inside of the project 'buildDir' directory.
    */
   String buildBase = 'odi'

   /**
    * The version of ODI that Checkmate will maintain compatibility with.
    */
   String compatibility = '12.2.1.3'

   /**
    * The type of Work Repository: 'development' or 'execution'.
    */
   String workType = 'development'

   /**
    * What method of content are we using in source control: 'directory' or 'file'.
    */
   String contentPolicy = 'directory'

   String masterUrl = "jdbc:oracle:thin:@${Utils.getHostname()}:1521/ORCL"
   String masterDriver = "oracle.jdbc.OracleDriver"
   String masterRepo = "DEV_ODI_REPO"
   String masterPassword
   String workRepo = "WORKREP"
   String odiUser = "SUPERVISOR"
   String odiPassword

   /**
    * Returns a normalized version of the ODI Project Name for use as the Project Code.
    *
    * @return Normalized version of the Project Name for use as the Project Code.
    */
   def getProjectCode(String name) {

      return projectCode ?: name.toUpperCase()
              .replace(' ', '_')
   }

   /**
    * Returns a Boolean: true if this is a development work repository; false if it is not.
    *
    * @return a Boolean specifying whether this is a development work repository.
    */
   def isDevelopment() {

      return (workType.toLowerCase() == 'development')
   }
}
