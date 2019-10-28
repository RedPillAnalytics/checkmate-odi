package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.impexp.smartie.ISmartExportable
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class ExportGlobalDirectoryTask extends ExportDirectoryTask {

    // specify the global subdirectory
    String category = 'global'

    @Internal
    Instance instance

    @SuppressWarnings("GroovyAssignabilityCheck")
    @TaskAction
    def exportGlobals() {

        instance.connect()

        instance.beginTxn()

        instance.findAllGlobalReusableMappings().each {
            exportObject(it, "${exportDir.canonicalPath}/reusable-mapping")
        }

        instance.findAllGlobalUserFunctions().each {
            exportObject(it, "${exportDir.canonicalPath}/user-function")
        }

        instance.findAllGlobalSequences().each {
            exportObject(it, "${exportDir.canonicalPath}/sequence")
        }

        instance.findAllGlobalVariables().each {
            exportObject(it, "${exportDir.canonicalPath}/variable")
        }

        instance.findAllGlobalCKM().each {
            exportObject(it, "${exportDir.canonicalPath}/knowledge-module")
        }

        instance.findAllGlobalIKM().each {
            exportObject(it, "${exportDir.canonicalPath}/knowledge-module", true)
        }

        instance.findAllGlobalJKM().each {
            exportObject(it, "${exportDir.canonicalPath}/knowledge-module")
        }

        instance.findAllGlobalLKM().each {
            exportObject(it, "${exportDir.canonicalPath}/knowledge-module")
        }

        instance.findAllGlobalRKM().each {
            exportObject(it, "${exportDir.canonicalPath}/knowledge-module")
        }

        instance.findAllGlobalSKM().each {
            exportObject(it, "${exportDir.canonicalPath}/knowledge-module")
        }

        instance.findAllGlobalXKM().each {
            exportObject(it, "${exportDir.canonicalPath}/knowledge-module")
        }

        instance.endTxn()

        // execute the export stage process
        exportStageDir()

    }
}