package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiProject
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.smartie.ISmartExportable
import oracle.odi.impexp.smartie.impl.SmartExportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class SmartExportAllTask extends DefaultTask {

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
    @Option(option = "sourcePath",
            description = "The path to the export location. Defaults to the 'sourceBase' parameter value.")
    String sourcePath

    // setSourceBase is not used, but I added it to support Gradle Incremental Build support
    @OutputDirectory
    def getSourceBase() {

        return project.file(sourcePath)
    }

    @TaskAction
    def exportAllProjects() {

        def instance = new Instance(url, driver, master, work, masterPass, odi, odiPass)
        def exportService = new SmartExportServiceImpl(instance.odi)
        def encdOption = new EncodingOptions("1.0", "ISO8859_9", "ISO-8859-9")

        //Finding all Projects and save into a smartExportList
        List<OdiProject> projects = ((IOdiProjectFinder) instance.odi.getTransactionalEntityManager().getFinder(OdiProject.class)).findAll().toList()

        instance.beginTxn()

        projects.each {
            OdiProject project ->
                List<ISmartExportable> smartExportList = new LinkedList<ISmartExportable> ()
                smartExportList.add(project)
                exportService.exportToXml(smartExportList, sourcePath,project.getName(), true, false, encdOption, false, null, null, true)
        }

        instance.endTxn()
    }
}