package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.ExportFile
import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ImportObjectTask extends DefaultTask {

    @Internal
    Instance instance

    @Input
    @Option(option = "import-path",
            description = "The path to the Smart Exported XML file to import.")
    String sourcePath

    @InputFile
    def getImportFile() {
        // normalize checking logic into a single class
        // DRY
        return new ExportFile(project.file(sourcePath)).export
    }

    @TaskAction
    def importObject() {

        //Make the Connection
        instance.connect()
        instance.beginTxn()

        new SmartImportServiceImpl(instance.odi).importObjectsFromXml(
                getImportFile().canonicalPath,
                null,
                false,
        )
        instance.endTxn()
    }
}

