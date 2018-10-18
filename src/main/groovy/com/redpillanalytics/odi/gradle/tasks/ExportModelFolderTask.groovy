package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
import oracle.odi.domain.model.OdiModelFolder
import oracle.odi.domain.project.OdiFolder
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.support.ExportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportModelFolderTask extends DefaultTask {

    @Input
    @Option(option = "source-path",
            description = "The path to the export location. Defaults to the 'sourceBase' parameter value.")
    String sourcePath

    @Input
    @Option(option = "folder-name",
            description = "The name of the model folder(s) to find.")
    String modelFolderName

    @Internal
    Instance instance

    // setSourceBase is not used, but I added it to support Gradle Incremental Build support
    @OutputDirectory
    def getSourceBase() {

        return project.file(sourcePath)
    }

    @TaskAction
    def exportModelFolder() {

        log.debug "sourcePath: ${sourcePath}"
        log.debug "sourceBase: ${sourceBase}"

        instance.connect()

        log.debug "All Model Folders: ${instance.modelFolderFinder.findAll().toString()}"

        // create the export list
        List<IExportable> exportList = new LinkedList<IExportable>()

        if (!instance.findModelFolderbyName(modelFolderName)) {

            log.warn "Not finded Model Folders containing the Name '${modelFolderName}' ..."

        } else {
            //We have Model Folder(s)!
            Collection<OdiModelFolder> folders =  instance.findModelFolderbyName(modelFolderName)

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