package com.redpillanalytics.odi.gradle

import groovy.util.logging.Slf4j

@Slf4j
class OdiPluginExtension {

   /**
    * The base sorurce directory.
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

}
