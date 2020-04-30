package com.redpillanalytics.odi.gradle.tasks

import com.sunopsis.core.SnpsNamespaceException
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

   def importObject(File file, int importMode = ImportServiceImpl.IMPORT_MODE_SYNONYM_INSERT_UPDATE) {

      try {

         importService.importObjectFromXml(
                 importMode,
                 file.canonicalPath,
                 true,
                 'checkmate-odi12c+' as char[],
                 false
         )

      } catch(Exception e ) {
         // Ignore SnpsNamespaceException for duplicated space names
         if(!e.toString().contains('ODI-17591')) {
            throw e
         }
      }

   }

   def smartImportObject(File file) {

      smartImportService.setMatchedFCODefaultImportAction(smartImportService.MATCH_BY_ID, smartImportService.SMART_IMPORT_ACTION_OVERWRITE)

      smartImportService.importObjectsFromXml(
              file.canonicalPath,
              'checkmate-odi12c+' as char[],
              false,
      )

   }

   def importTopology(String folderPath, int importMode = ImportServiceImpl.IMPORT_MODE_SYNONYM_INSERT_UPDATE)  {

      importService.importTopologyFromFolder(
              importMode,
              folderPath,
              true,
              'checkmate-odi12c+' as char[],
              false)

   }

}
