package com.redpillanalytics.odi.gradle

import com.redpillanalytics.common.Utils
import groovy.util.logging.Slf4j

@Slf4j
class OdiPluginExtension {

   /**
    * The name of the ODI project being built. Defaults to the directory name in SCM.
    */
   String projectName = 'TEST_PROJECT'

   /**
    * The target folder name containing the objects to export from the ODI Repository for the SmartExport (ExportProjectFolderTask)
    */
   String folderName = 'TEST_FOLDER'
   /**
    * The code of the ODI project being built. Defaults to a normalized version of the project name.
    */
   String projectCode

   /**
    * The base source directory.
    */
   String sourceBase = 'src/main/odi'

   /**
    * The base ODI build directory, which exists inside of the project 'buildDir' directoruy.
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

//   String masterUrl = "jdbc:oracle:thin:@${Utils.getHostname()}:1521/ORCL"
//   String masterDriver = "oracle.jdbc.OracleDriver"
//   String masterRepo = "DEV_ODI_REPO"
//   String masterPassword = 'test'
//   String workRepo = "WORKREP"
//   String odiUser = "SUPERVISOR"
//   String odiPassword = 'test'

   String masterUrl = "jdbc:oracle:thin:@odi-repo.csagf46svk9g.us-east-2.rds.amazonaws.com:1521/ORCL"
   String masterDriver = "oracle.jdbc.OracleDriver"
   String masterRepo = "DEV_ODI_REPO"
   String masterPassword = 'Welcome1'
   String workRepo = "WORKREP"
   String odiUser = "SUPERVISOR"
   String odiPassword = 'Welcome1'

   /**
    * Returns a normalized version of the ODI Project Name for use as the Project Code.
    *
    * @return Normalized version of the Project Name for use as the Project Code.
    */
   def getProjectCode(String name) {

      return projectCode ?: name.toUpperCase().replace(' ', '_')
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
