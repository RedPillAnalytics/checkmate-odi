package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl
import oracle.odi.impexp.support.ImportServiceImpl
import org.gradle.api.tasks.Internal

@Slf4j
class ImportTask extends InstanceTask {

   @Internal
   ImportServiceImpl getImportService() {
      return new ImportServiceImpl(instance.odi)
   }

   @Internal
   SmartImportServiceImpl getSmartImportService() {
      return new SmartImportServiceImpl(instance.odi)
   }

   @Internal
   def cipherKey = 'checkmate-odi12c+' as char[]

   def importObject(File file, int importMode = ImportServiceImpl.IMPORT_MODE_SYNONYM_INSERT_UPDATE, Boolean cipherData = false) {

      if (!cipherData) {

         importService.importObjectFromXml(
                 importMode,
                 file.canonicalPath,
                 true,
                 null,
                 true
         )

      } else {

         importService.importObjectFromXml(
                 importMode,
                 file.canonicalPath,
                 true,
                 cipherKey,
                 false
         )

      }

   }

   def smartImportObject(File file, Boolean cipherData = false) {

      smartImportService.setMatchedFCODefaultImportAction(smartImportService.MATCH_BY_ID, smartImportService.SMART_IMPORT_ACTION_OVERWRITE)

      if (!cipherData) {

         smartImportService.importObjectsFromXml(
                 file.canonicalPath,
                 null,
                 true,
         )

      } else {

         smartImportService.importObjectsFromXml(
                 file.canonicalPath,
                 cipherKey,
                 false,
         )

      }

   }

   def importTopology(String folderPath, int importMode = ImportServiceImpl.IMPORT_MODE_SYNONYM_INSERT_UPDATE, Boolean cipherData = true)  {

      if (!cipherData) {

         importService.importTopologyFromFolder(
                 importMode,
                 folderPath,
                 true,
                 null,
                 true)

      } else {

         importService.importTopologyFromFolder(
                 importMode,
                 folderPath,
                 true,
                 cipherKey,
                 false)

      }

   }

}
