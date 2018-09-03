package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class SmartImportAllTask extends DefaultTask {

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
    @Option(option = "path",
            description = "The path to the source file to import. Defaults to the 'sourceBase' parameter value.")
    String path

    @TaskAction
    def importAllXML() {

        def instance = new Instance(url, driver, master, work, masterPass, odi, odiPass)
        def importService = new SmartImportServiceImpl(instance.odi)

        //Reading all the XML Files from the Source Directory
        def getXMLFiles = { path ->

            def xmlFiles = ""
            def files
            def folder = new File(path as String)
            def listOfFiles = folder.listFiles()
            listOfFiles.each { def file ->
                if (file.isFile()) {
                    files = file.name
                    if (files.endsWith(".xml") || files.endsWith(".XML")) {
                        xmlFiles += path + "/" + files + "n"
                    }
                }
            }
            xmlFiles
        }

        //Taking all the XML Files from the Source Directory and splitting to loop into each XML File to Import
        def xmlFiles = getXMLFiles(path).split("n")

        instance.beginTxn()

        //Importing each XML File to the ODI Repository
        xmlFiles.each { def xmlfile ->
            importService.importObjectsFromXml(xmlfile, null, false)
        }

        instance.endTxn()

    }
}
