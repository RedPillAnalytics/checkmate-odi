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

        // find the global km
        List globalKmList = new LinkedList<ISmartExportable>()

        globalKmList.addAll(instance.findAllGlobalCKM())
        globalKmList.addAll(instance.findAllGlobalIKM())
        globalKmList.addAll(instance.findAllGlobalJKM())
        globalKmList.addAll(instance.findAllGlobalLKM())
        globalKmList.addAll(instance.findAllGlobalRKM())
        globalKmList.addAll(instance.findAllGlobalSKM())
        globalKmList.addAll(instance.findAllGlobalXKM())

        // export the global km
        smartExportObject(globalKmList, "${exportDir.canonicalPath}/knowledge-module", 'KM','Global')

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