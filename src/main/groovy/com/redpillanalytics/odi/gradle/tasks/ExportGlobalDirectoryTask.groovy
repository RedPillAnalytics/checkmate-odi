package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.odi.Instance
import groovy.util.logging.Slf4j
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

        instance.findAllGlobalCKM().each {
            exportObject(it,"${exportDir.canonicalPath}/ckm", true)
            //smartExportObject(it, "${exportDir.canonicalPath}/ckm", it.name)
        }

        instance.findAllGlobalIKM().each {
            exportObject(it, "${exportDir.canonicalPath}/ikm", true)
            //smartExportObject(it, "${exportDir.canonicalPath}/ikm", it.name)
        }

        instance.findAllGlobalJKM().each {
            exportObject(it, "${exportDir.canonicalPath}/jkm", true)
            //smartExportObject(it, "${exportDir.canonicalPath}/jkm", it.name)
        }

        instance.findAllGlobalLKM().each {
            exportObject(it, "${exportDir.canonicalPath}/lkm", true)
            //smartExportObject(it, "${exportDir.canonicalPath}/lkm", it.name)
        }

        instance.findAllGlobalRKM().each {
            exportObject(it, "${exportDir.canonicalPath}/rkm", true)
            //smartExportObject(it, "${exportDir.canonicalPath}/rkm", it.name)
        }

        instance.findAllGlobalSKM().each {
            exportObject(it, "${exportDir.canonicalPath}/skm", true)
            //smartExportObject(it, "${exportDir.canonicalPath}/skm", it.name)
        }

        instance.findAllGlobalXKM().each {
            exportObject(it, "${exportDir.canonicalPath}/xkm", true)
            //smartExportObject(it, "${exportDir.canonicalPath}/xkm", it.name)
        }

        instance.findAllGlobalReusableMappings().each {
            exportObject(it, "${exportDir.canonicalPath}/reusable-mapping", true)
            //smartExportObject(it, "${exportDir.canonicalPath}/reusable-mapping", it.name)
        }

        instance.findAllGlobalUserFunctions().each {
            exportObject(it, "${exportDir.canonicalPath}/user-function", true)
            //smartExportObject(it, "${exportDir.canonicalPath}/user-function", it.name)
        }

        instance.findAllGlobalSequences().each {
            exportObject(it, "${exportDir.canonicalPath}/sequence", true)
            //smartExportObject(it, "${exportDir.canonicalPath}/sequence", it.name)
        }

        instance.findAllGlobalVariables().each {
            exportObject(it, "${exportDir.canonicalPath}/variable", true)
            //smartExportObject(it, "${exportDir.canonicalPath}/variable", it.name)
        }

        instance.endTxn()

        // execute the export stage process
        exportStageDir()

    }
}