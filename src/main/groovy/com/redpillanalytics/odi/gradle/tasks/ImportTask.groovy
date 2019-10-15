package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.impexp.OdiImportException
import oracle.odi.impexp.smartie.OdiSmartImportException
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
   importObject(File file) {
      try{

         importService.importObjectFromXml(
                 ImportServiceImpl.IMPORT_MODE_SYNONYM_INSERT_UPDATE,
                 file.canonicalPath,
                 true,
                 'checkmate-odi12c+' as char[],
                 false
         )

      } catch(OdiImportException e) {log.debug(e.toString())}

   }

   @Internal
   smartImportObject(File file) {

      smartImportService.setMatchedFCODefaultImportAction(smartImportService.MATCH_BY_ID, smartImportService.SMART_IMPORT_ACTION_OVERWRITE)

      try {

            smartImportService.importFromXml(
                    file.canonicalPath,
                    null,
                    'checkmate-odi12c+' as char[],
                    false,
         )

      } catch(OdiSmartImportException e) {log.debug(e.toString())}

   }

   @Internal
   importTopology(String path) {

      try {

         importService.importTopologyFromFolder(
                 ImportServiceImpl.IMPORT_MODE_SYNONYM_INSERT_UPDATE,
                 path,
                 true,
                 'checkmate-odi12c+' as char[],
                 false
         )

      } catch(OdiImportException e) {log.debug(e.toString())}

   }

}
