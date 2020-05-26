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
   def encodingOptions = new EncodingOptions(EncodingOptions.DEFAULT_XML_VERSION,
                                             EncodingOptions.DEFAULT_JAVA_CHARSET,
                                             EncodingOptions.DEFAULT_XML_CHARSET)

   @Internal
   def cipherKey = 'checkmate-odi12c+' as char[]

   def exportObject(IExportable object, String path, Boolean parents = false, Boolean recursive = true, Boolean withoutCipherData = false, Boolean overwrite = true) {

      def result

      if(!parents) {

         result = exportService.exportToXml(
                 object,
                 path,
                 overwrite,
                 recursive,
                 encodingOptions,
                 cipherKey,
                 withoutCipherData)

      } else {

         result = exportService.exportToXmlWithParents(
                 object,
                 path,
                 overwrite,
                 recursive,
                 encodingOptions,
                 cipherKey,
                 withoutCipherData)

      }

      return result
   }

   def smartExportObject(ISmartExportable object, String path, String objectPrefix, String objectName, Boolean withoutCipherData = false, Boolean isZip = false, Boolean overwrite = true, Boolean materializeShortcut = false) {

      List<ISmartExportable> smartExportList = new LinkedList<ISmartExportable>()
      smartExportList.add(object)
      def result

      result = smartExportService.exportToXml(
              smartExportList,
              path,
              "${objectPrefix}_${objectName.replaceAll("[^a-zA-Z0-9]+","_")}",
              overwrite,
              isZip,
              encodingOptions,
              materializeShortcut,
              null,
              cipherKey,
              withoutCipherData)

      return result
   }

   def smartExportList(List<ISmartExportable> smartExportList, String path, String objectPrefix, String objectName, Boolean withoutCipherData = false, Boolean isZip = false, Boolean overwrite = true, Boolean materializeShortcut = false) {

      def result

      result = smartExportService.exportToXml(
              smartExportList,
              path,
              "${objectPrefix}_${objectName.replaceAll("[^a-zA-Z0-9]+","_")}",
              overwrite,
              isZip,
              encodingOptions,
              materializeShortcut,
              null,
              cipherKey,
              withoutCipherData)

      return result
   }

   def exportTopology(String folderPath, Boolean withoutCipherData = false) {

      def result

      result = exportService.exportTopologyInFolder(
              folderPath,
              encodingOptions,
              cipherKey,
              withoutCipherData)

      return result
   }
}
