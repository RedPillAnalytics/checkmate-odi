package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.smartie.ISmartExportable
import oracle.odi.impexp.smartie.impl.SmartExportServiceImpl
import org.gradle.api.tasks.TaskAction

@Slf4j
class ExportProjectFileTask extends ExportFileTask {

   @TaskAction
   def smartExportFile() {

      instance.connect()

      // let's make sure the project exists
      log.debug "All projects: ${instance.projectFinder.findAll().toString()}"

      //Find The Target Project by the Project Code Value
      List<ISmartExportable> projectList = new LinkedList<ISmartExportable>()

      projectList.add(((IOdiProjectFinder) instance.getProjectFinder()).findByCode(projectCode))

      instance.beginTxn()

      new SmartExportServiceImpl(instance.odi).exportToXml(
              projectList,
              exportFile.canonicalPath,
              projectCode,
              true,
              false,
              new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9"),
              false,
              null,
              null,
              true
      )
      instance.endTxn()
   }
}
