package com.redpillanalytics.odi

import com.redpillanalytics.common.Utils
import groovy.util.logging.Slf4j
import org.gradle.api.GradleException

/**
 * Class for normalizing ODI export files
 */
@Slf4j
class ExportFile {

   File export

   ExportFile(File file) {

      this.export = file
      log.debug "export file: ${export.path}"

      verify()
   }

   def verify() {

      if (!export.exists()) throw new GradleException("$export.path does not exist.")

      if (export.isDirectory()) throw new GradleException("'${export.path}' is a directory.")

      if (Utils.getFileExt(export) != 'xml') {
         throw new GradleException("'${export.path}' is not an XML file.")
      }
   }
}
