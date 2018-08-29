package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.core.OdiInstance
import oracle.odi.domain.project.OdiProject
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.support.ExportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class SmartExportTask extends DefaultTask {

    @Input
    @Option(option = "url",
            description = "The JDBC URL of the Master Repository.")
    String url

    @Input
    @Option(option = "driver",
            description = "The JDBC driver class of the Master Repository.")
    String driver

    @Input
    @Option(option = "master",
            description = "The schema name of the Master repository.")
    String master

    @Input
    @Option(option = "work",
            description = "The schema name of the Work repository.")
    String work

    @Input
    @Option(option = "masterPass",
            description = "The password for the Master repository.")
    String masterPass

    @Input
    @Option(option = "odi",
            description = "The name of the ODI user.")
    String odi

    @Input
    @Option(option = "odiPass",
            description = "The password of the ODI user.")
    String odiPass

    @Input
    @Option(option = "exportPath",
            description = "The System Path to Export File")
    String exportPath

    @Input
    @Option(option = "projectName",
            description = "The project name to Export")
    String projectName

    @TaskAction
    def exportProject() {

        def instance = new Instance(url, driver, master, work, masterPass, odi, odiPass)
        OdiProject project = ((IOdiProjectFinder) instance.getTransactionalEntityManager().getFinder(OdiProject.class)).findByCode(projectName)
        def export=new ExportServiceImpl(instance.odi)
        def encdOption = new EncodingOptions()


        instance.beginTxn()

        export.exportToXmlWithParents(project, exportPath, true, true, encdOption, null, true)

        instance.odi.getTransactionManager().commit()

        instance.endTxn()

    }
}
