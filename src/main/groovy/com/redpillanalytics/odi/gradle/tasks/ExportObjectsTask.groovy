package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.impexp.IExportable
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
class ExportObjectsTask extends ProjectCodeTask {

    @Input
    @Option(option = "source-path",
            description = "The path to the export location. Defaults to the 'sourceBase' parameter value.")
    String sourcePath

    // setSourceBase is not used, but I added it to support Gradle Incremental Build support
    @OutputDirectory
    def getSourceBase() {

        return project.file(sourcePath)
    }

    @TaskAction
    def exportProjectObjects() {

        log.debug "sourcePath: ${sourcePath}"
        log.debug "sourceBase: ${sourceBase}"

        instance.connect()

        log.debug "All projects: ${instance.projectFinder.findAll().toString()}"

        // create the export list
        List<IExportable> exportList = new LinkedList<IExportable>()

        // Validate project and folder
        if (!instance.findProjectName(projectCode)) {

            log.warn "Project Code '${projectCode}' does not exist."

        } else if (!instance.findFoldersProject(projectCode)[0]) {

            log.warn "No Folders founded in the Project '${projectCode}'..."

        } else {
            //We have Folders! Let's go ahead and collect all the existing objects folder by folder
            Collection<OdiFolder> folders =  instance.findFoldersProject(projectCode)

            folders.each {
                OdiFolder folder ->
                    log.info "Exporting Objects from  ${folder.name} ..."

                    // list the mappings
                    instance.findMapping(projectCode, folder.name).each {
                        exportList.add((IExportable) it)
                        //log.info "Mapping ${it.name} added to export list..."
                    }

                    // list the reusable mappings
                    instance.findReusableMapping(projectCode, folder.name).each {
                        exportList.add((IExportable) it)
                        //log.info "Reusable Mapping ${it.name} added to export list..."
                    }

                    // list the packages
                    instance.findPackage(projectCode, folder.name).each {
                        exportList.add((IExportable) it)
                        //log.info "Package ${it.name} added to export list..."
                    }

                    // list the procedures
                    instance.findProcedure(projectCode, folder.name).each {
                        exportList.add((IExportable) it)
                        //log.info "Procedure ${it.name} added to export list..."
                    }
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