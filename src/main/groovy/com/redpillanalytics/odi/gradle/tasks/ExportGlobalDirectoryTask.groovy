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
            exportObject(it, sourceBase.canonicalPath, true)
            //smartExportObject(it, sourceBase.canonicalPath, it.name)
        }

        instance.findAllGlobalIKM().each {
            exportObject(it, sourceBase.canonicalPath, true)
            //smartExportObject(it, sourceBase.canonicalPath, it.name)
        }

        instance.findAllGlobalJKM().each {
            exportObject(it, sourceBase.canonicalPath, true)
            //smartExportObject(it, sourceBase.canonicalPath, it.name)
        }

        instance.findAllGlobalLKM().each {
            exportObject(it, sourceBase.canonicalPath, true)
            //smartExportObject(it, sourceBase.canonicalPath, it.name)
        }

        instance.findAllGlobalRKM().each {
            exportObject(it, sourceBase.canonicalPath, true)
            //smartExportObject(it, sourceBase.canonicalPath, it.name)
        }

        instance.findAllGlobalReusableMappings().each {
            exportObject(it, sourceBase.canonicalPath, true)
            //smartExportObject(it, sourceBase.canonicalPath, it.name)
        }

        instance.findAllGlobalUserFunctions().each {
            exportObject(it, sourceBase.canonicalPath, true)
            //smartExportObject(it, sourceBase.canonicalPath, it.name)
        }

        instance.findAllGlobalSequences().each {
            exportObject(it, sourceBase.canonicalPath, true)
            //smartExportObject(it, sourceBase.canonicalPath, it.name)
        }

        instance.findAllGlobalVariables().each {
            exportObject(it, sourceBase.canonicalPath, true)
            //smartExportObject(it, sourceBase.canonicalPath, it.name)
        }

        instance.endTxn()
    }
}