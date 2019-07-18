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
            //exportObject(it,"${sourceBase.canonicalPath}/ckm", true)
            smartExportObject(it, "${sourceBase.canonicalPath}/ckm", it.name)
        }

        instance.findAllGlobalIKM().each {
            //exportObject(it, "${sourceBase.canonicalPath}/ikm", true)
            smartExportObject(it, "${sourceBase.canonicalPath}/ikm", it.name)
        }

        instance.findAllGlobalJKM().each {
            //exportObject(it, "${sourceBase.canonicalPath}/jkm", true)
            smartExportObject(it, "${sourceBase.canonicalPath}/jkm", it.name)
        }

        instance.findAllGlobalLKM().each {
            //exportObject(it, "${sourceBase.canonicalPath}/lkm", true)
            smartExportObject(it, "${sourceBase.canonicalPath}/lkm", it.name)
        }

        instance.findAllGlobalRKM().each {
            //exportObject(it, "${sourceBase.canonicalPath}/rkm", true)
            smartExportObject(it, "${sourceBase.canonicalPath}/rkm", it.name)
        }

        instance.findAllGlobalSKM().each {
            //exportObject(it, "${sourceBase.canonicalPath}/skm", true)
            smartExportObject(it, "${sourceBase.canonicalPath}/skm", it.name)
        }

        instance.findAllGlobalXKM().each {
            //exportObject(it, "${sourceBase.canonicalPath}/xkm", true)
            smartExportObject(it, "${sourceBase.canonicalPath}/xkm", it.name)
        }

        instance.findAllGlobalReusableMappings().each {
            //exportObject(it, "${sourceBase.canonicalPath}/reusable-mapping", true)
            smartExportObject(it, "${sourceBase.canonicalPath}/reusable-mapping", it.name)
        }

        instance.findAllGlobalUserFunctions().each {
            //exportObject(it, "${sourceBase.canonicalPath}/user-function", true)
            smartExportObject(it, "${sourceBase.canonicalPath}/user-function", it.name)
        }

        instance.findAllGlobalSequences().each {
            //exportObject(it, "${sourceBase.canonicalPath}/sequence", true)
            smartExportObject(it, "${sourceBase.canonicalPath}/sequence", it.name)
        }

        instance.findAllGlobalVariables().each {
            //exportObject(it, "${sourceBase.canonicalPath}/variable", true)
            smartExportObject(it, "${sourceBase.canonicalPath}/variable", it.name)
        }

        instance.endTxn()
    }
}