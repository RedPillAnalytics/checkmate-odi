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

   def exportObject(IExportable object, String path, Boolean parents = false, Boolean recursive = true, Boolean overwrite = true) {

      def encodingOptions = new EncodingOptions(EncodingOptions.DEFAULT_XML_VERSION, EncodingOptions.DEFAULT_JAVA_CHARSET, EncodingOptions.DEFAULT_XML_CHARSET)
      def result

      try {

         if(!parents) {

            result = exportService.exportToXml(
                    object,
                    path,
                    overwrite,
                    recursive,
                    encodingOptions,
                    'checkmate-odi12c+' as char[],
                    false)

         } else {

            result = exportService.exportToXmlWithParents(
                    object,
                    path,
                    overwrite,
                    recursive,
                    encodingOptions,
                    'checkmate-odi12c+' as char[],
                    false)

         }

      } catch(Exception e) {log.info("Error exporting object: ${object.name} error message: ${e.toString()}")}

      return result
   }

   def smartExportObject(ISmartExportable object, String path, String objectPrefix, String objectName, Boolean isZip = false, Boolean overwrite = true, Boolean materializeShortcut = false, Boolean exportWithoutCipherData = false) {

      def encodingOptions = new EncodingOptions(EncodingOptions.DEFAULT_XML_VERSION, EncodingOptions.DEFAULT_JAVA_CHARSET, EncodingOptions.DEFAULT_XML_CHARSET)
      List<ISmartExportable> smartExportList = new LinkedList<ISmartExportable>()
      smartExportList.add(object)
      def result

      try {

         result = smartExportService.exportToXml(
                 smartExportList,
                 path,
                 "${objectPrefix}_${objectName.replaceAll("[^a-zA-Z0-9]+","_")}",
                 overwrite,
                 isZip,
                 encodingOptions,
                 materializeShortcut,
                 null,
                 'checkmate-odi12c+' as char[],
                 exportWithoutCipherData)

      } catch(Exception e) {log.info("Error exporting object: ${object.name} error message: ${e.toString()}")}

      return result
   }

   def exportTopology(String folderPath) {

      def encodingOptions = new EncodingOptions(EncodingOptions.DEFAULT_XML_VERSION, EncodingOptions.DEFAULT_JAVA_CHARSET, EncodingOptions.DEFAULT_XML_CHARSET)
      def result

      try {

         result = exportService.exportTopologyInFolder(
                 folderPath,
                 encodingOptions,
                 'checkmate-odi12c+' as char[],
                 false)

      } catch(Exception e) {log.info("Error exporting: Topology error message: ${e.toString()}")}

      return result
   }
}
