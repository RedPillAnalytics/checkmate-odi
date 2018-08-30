package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class SmartImportTask extends DefaultTask {

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
    @Option(option = "importFilePath",
            description = "The Smart Exported XML File Path to Import to ODI Repository")
    String importFilePath


    @TaskAction
    def importProject() {

        def instance = new Instance(url, driver, master, work, masterPass, odi, odiPass)
        def importsrvc = new SmartImportServiceImpl(instance.odi)
        def fileXML = new File(importFilePath)
        def filePath

        if(fileXML.file && fileXML.name =~/\.xml/){
            filePath = fileXML.absolutePath
        } else
            throw new Exception("XML File not found or damaged")

        instance.beginTxn()

        importsrvc.importObjectsFromXml(filePath, null, true)

        instance.endTxn()

    }
}
