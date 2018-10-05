package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class SmartImportAllTask extends DefaultTask {

    @Internal
    Instance instance

    @Input
    @Option(option = "sourcePath",
            description = "The path to the source file to import. Defaults to the 'sourceBase' parameter value.")
    String sourcePath

    @TaskAction
    def importAllXML() {

        log.debug "sourcePath: ${sourcePath}"
        log.debug "sourceBase: ${sourceBase}"

        //Reading all the XML Files from the Source Directory
        def getXMLFiles = { String sourcePath ->

            def xmlFiles = ""
            def files
            def folder = new File(sourcePath)
            def listOfFiles = folder.listFiles()
            listOfFiles.each { file ->
                if (file.isFile()) {
                    files = file.name
                    if (files.endsWith(".xml") || files.endsWith(".XML")) {
                        xmlFiles += sourcePath + "/" + files + "#"
                    }
                }
            }
            xmlFiles
        }

        //Taking all the XML Files from the Source Directory and splitting to loop into each XML File to Import
        def xmlFiles = getXMLFiles(sourcePath).split("#")

        //Make the Connection
        instance.connect()

        instance.beginTxn()

        //Importing each XML File to the ODI Repository
        xmlFiles.each { xmlfile ->
            new SmartImportServiceImpl(instance.odi).importObjectsFromXml(
                    xmlfile,
                    null,
                    false,
            )
        }

        instance.endTxn()

    }
}
