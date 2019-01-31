package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.smartie.impl.SmartExportServiceImpl
import oracle.odi.impexp.support.ExportServiceImpl
import org.gradle.api.tasks.Internal

@Slf4j
class ExportTask extends InstanceTask {

   @Internal
   ExportServiceImpl getExportService() {
      return new ExportServiceImpl(instance.odi)
   }

   @Internal
   SmartExportServiceImpl getSmartExportService() {
      return new SmartExportServiceImpl(instance.odi)
   }

   @Internal
   def exportObject(IExportable object, String path, Boolean parents, Boolean overwrite = true, Boolean recursive = true) {

      def encodingOptions = new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9")
      def result

      if (parents) {
         result = exportService.exportToXmlWithParents(
                 object,
                 path,
                 overwrite,
                 recursive,
                 encodingOptions,
                 null,
                 true,
         )
      } else {
         result = exportService.exportToXml(
                 object,
                 path,
                 overwrite,
                 recursive,
                 encodingOptions,
                 null,
                 true,
         )
      }
      return result
   }
}
