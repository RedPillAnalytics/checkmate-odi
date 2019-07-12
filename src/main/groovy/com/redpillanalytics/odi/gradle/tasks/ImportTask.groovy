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
   importObject(File file) {
      importService.importObjectFromXml(
              ImportServiceImpl.IMPORT_MODE_SYNONYM_INSERT_UPDATE,
              file.canonicalPath,
              true,
              null,
              true
      )
   }

   @Internal
   smartImportObject(File file) {
      smartImportService.setMatchedFCODefaultImportAction(smartImportService.MATCH_BY_NAME,smartImportService.SMART_IMPORT_ACTION_OVERWRITE)
      smartImportService.importObjectsFromXml(
              file.canonicalPath,
              'checkmate-odi12c+' as char[],
              false,
      )
   }
}
