package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.support.ExportServiceImpl
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportTask extends InstanceTask {

   @Internal
   def getExportService() {
      return new ExportServiceImpl(instance.odi)
   }

   @Internal
   def exportObject(IExportable object, String path, Boolean overwrite, Boolean recursive) {

      def result = exportService.exportToXmlWithParents(
              object,
              path,
              overwrite,
              recursive,
              new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9"),
              null,
              true,
      )

      return result
   }
}
