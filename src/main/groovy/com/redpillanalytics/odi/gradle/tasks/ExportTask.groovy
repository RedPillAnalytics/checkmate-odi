package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.smartie.ISmartExportable
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
   def exportObject(IExportable object, String path, Boolean overwrite = true, Boolean recursive = true) {

      def encodingOptions = new EncodingOptions(EncodingOptions.DEFAULT_XML_VERSION, EncodingOptions.DEFAULT_JAVA_CHARSET, EncodingOptions.DEFAULT_XML_CHARSET)
      def result

      try{
         result = exportService.exportToXml(
                 object,
                 path,
                 overwrite,
                 recursive,
                 encodingOptions,
                 'checkmate-odi12c+' as char[],
                 false
         )
      } catch(StringIndexOutOfBoundsException e) {log.debug(e.toString())}

      return result
   }

   @Internal
   def smartExportObject(ISmartExportable object, String path, String objectName, Boolean isZip = false, Boolean overwrite = true, Boolean materializeShortcut = false, Boolean exportWithoutCipherData = false) {

      def encodingOptions = new EncodingOptions(EncodingOptions.DEFAULT_XML_VERSION, EncodingOptions.DEFAULT_JAVA_CHARSET, EncodingOptions.DEFAULT_XML_CHARSET)
      List<ISmartExportable> smartExportList = new LinkedList<ISmartExportable>()
      smartExportList.add(object)
      def result

      try{
         result = smartExportService.exportToXml(
                 smartExportList,
                 path,
                 objectName.replaceAll("[^a-zA-Z0-9]+","_").toUpperCase(),
                 overwrite,
                 isZip,
                 encodingOptions,
                 materializeShortcut,
                 null,
                 'checkmate-odi12c+' as char[],
                 exportWithoutCipherData)
      } catch(StringIndexOutOfBoundsException e) {log.debug(e.toString())}

      return result
   }
}
