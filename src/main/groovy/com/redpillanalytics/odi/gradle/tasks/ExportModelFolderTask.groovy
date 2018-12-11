package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import oracle.odi.domain.model.OdiModelFolder
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.support.ExportServiceImpl
import org.gradle.api.tasks.TaskAction

@Slf4j
class ExportModelFolderTask extends ExportDirectoryTask {

    @TaskAction
    def exportModelFolder() {

        instance.connect()

        log.debug "All Model Folders: ${instance.modelFolderFinder.findAll().toString()}"

        // create the export list
        List<IExportable> exportList = new LinkedList<IExportable>()

        if (!instance.findModelFolderbyName(folderName)) {

            log.warn "Not finded Model Folders containing the Name '${folderName}' ..."

        } else {
            //We have Model Folder(s)!
            Collection<OdiModelFolder> folders =  instance.findModelFolderbyName(folderName)

            folders.each {
                OdiModelFolder folder ->
                    log.info "Exporting Folder ${folder.name} ..."
                    exportList.add(folder)
                    }
            }

        // Validate if Export List have objects
        if (exportList.size() <= 0) {
            log.warn "Nothing to export..."
        }

        instance.beginTxn()
        exportList.each {
            IExportable object ->
                new ExportServiceImpl(this.instance.odi).exportToXmlWithParents(
                        object,
                        sourceBase.canonicalPath,
                        true,
                        true,
                        new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9"),
                        null,
                        true,
                )
        }

        instance.endTxn()
    }
}