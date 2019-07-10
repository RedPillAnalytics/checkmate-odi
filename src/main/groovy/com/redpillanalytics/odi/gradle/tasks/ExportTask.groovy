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
                 'checkmate-odi12c+' as char[],
                 false,
         )
      } else {
         result = exportService.exportToXml(
                 object,
                 path,
                 overwrite,
                 recursive,
                 encodingOptions,
                 'checkmate-odi12c+' as char[],
                 false,
         )
      }
      return result
   }

//   @Internal
//   def exportObjectCipherData(IExportable object, String path, Boolean parents, char[] exportKey, Boolean overwrite = true, Boolean recursive = true) {
//
//      def encodingOptions = new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9")
//      def result
//
//      if (parents) {
//         result = exportService.exportToXmlWithParents(
//                 object,
//                 path,
//                 overwrite,
//                 recursive,
//                 encodingOptions,
//                 exportKey,
//                 false,
//         )
//      } else {
//         result = exportService.exportToXml(
//                 object,
//                 path,
//                 overwrite,
//                 recursive,
//                 encodingOptions,
//                 exportKey,
//                 false,
//         )
//      }
//      return result
//   }

   @Internal
   def smartExportObject(ISmartExportable object, String path, String objectName, Boolean isZip = false, Boolean overwrite = true, Boolean materializeShortcut = false, Boolean exportWithoutCipherData = true) {

      def encodingOptions = new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9")
      List<ISmartExportable> smartExportList = new LinkedList<ISmartExportable>()
      smartExportList.add(object)
      def result

      result = smartExportService.exportToXml(
              smartExportList,
              path,
              objectName.replaceAll("[^a-zA-Z0-9]+","_").toUpperCase(),
              overwrite,
              isZip,
              encodingOptions,
              materializeShortcut,
              null,
              null,
              exportWithoutCipherData)

      return result
   }
}
