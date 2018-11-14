package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.runtime.loadplan.OdiLoadPlan
import oracle.odi.domain.runtime.scenario.OdiScenario
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.smartie.ISmartExportable
import oracle.odi.impexp.smartie.impl.SmartExportServiceImpl
import oracle.odi.impexp.support.ExportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportLoadPlansAndScenariosTask extends DefaultTask {

    @Input
    @Option(option = "source-path",
            description = "The path to the export location. Defaults to the 'sourceBase' parameter value.")
    String sourcePath

    @Input
    @Option(option = "project-code",
            description = "The code of the project to create.")
    String projectCode

    @Input
    @Option(option = "folder-name",
            description = "The target folder name containing the objects to export from the ODI Repository for the SmartExport.")
    String folder

    @Internal
    Instance instance

    // setSourceBase is not used, but I added it to support Gradle Incremental Build support
    @OutputDirectory
    def getSourceBase() {

        return project.file(sourcePath)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @TaskAction
    def exportLoadPlansAndScenarios() {

        log.debug "sourcePath: ${sourcePath}"
        log.debug "sourceBase: ${sourceBase}"

        instance.connect()

        log.debug "All projects: ${instance.projectFinder.findAll().toString()}"

        // Validate project and folder
        if (!instance.findProjectName(projectCode)) {

            log.warn "Project Code '${projectCode}' does not exist."

        } else if (!instance.findFolder(folder, projectCode)[0]) {

            log.warn "Folder name '${folder}' does not exist."

        } else {
            //Create Holder for the Scenario
            OdiScenario scenario = new OdiScenario()

            //Set up the export
            ExportServiceImpl export = new ExportServiceImpl(instance.odi)
            EncodingOptions encdOption = new EncodingOptions("1.0", "ISO8859_9",  "ISO-8859-9")
            boolean useTimeStamp = true

            instance.beginTxn()

            // list the mappings
            instance.findMapping(projectCode, folder).each {
                scenario = instance.findScenarioBySourceMapping(it.getInternalId(),useTimeStamp)
                if(scenario != null){
                    export.exportToXml(scenario, sourceBase.canonicalPath,true,
                            true,
                            encdOption,
                            null,
                            true)
                    log.info "Scenario Mapping ${it.name} exported ..."
                }

            }

            // list the packages
            instance.findPackage(projectCode, folder).each {
                scenario = instance.findScenarioBySourcePackage(it.getInternalId(),useTimeStamp)
                if(scenario != null){
                    export.exportToXml(scenario, sourceBase.canonicalPath,true,
                            true,
                            encdOption,
                            null,
                            true)
                    log.info "Scenario Package ${it.name} exported ..."
                }

            }

            // list the procedures
            instance.findProcedure(projectCode, folder).each {
                scenario = instance.findScenarioBySourceUserProcedure(it.getInternalId(),useTimeStamp)
                if(scenario != null){
                    export.exportToXml(scenario, sourceBase.canonicalPath,true,
                            true,
                            encdOption,
                            null,
                            true)
                    log.info "Scenario Procedure ${it.name} exported ..."
                }

            }

            // list the load plans
            instance.findAllLoadPlans().each { OdiLoadPlan loadPlan ->
                if (loadPlan != null) {
                    export.exportToXml(loadPlan, sourceBase.canonicalPath, true,
                            true,
                            encdOption,
                            null,
                            true)
                    log.info "Load Plan ${loadPlan.name} exported ..."
                }
            }

            instance.endTxn()
        }
    }
}