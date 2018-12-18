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
}
