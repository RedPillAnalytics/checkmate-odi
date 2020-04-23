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

        // Export the Reusable Mappings
        instance.findAllGlobalReusableMappings().each {
            exportObject(it, "${exportDir.canonicalPath}/reusable-mapping")
        }

        // Export the user Functions
        instance.findAllGlobalUserFunctions().each {
            exportObject(it, "${exportDir.canonicalPath}/user-function")
        }

        // Export the Sequences
        instance.findAllGlobalSequences().each {
            exportObject(it, "${exportDir.canonicalPath}/sequence")
        }

        // Export the Variables
        instance.findAllGlobalVariables().each {
            exportObject(it, "${exportDir.canonicalPath}/variable")
        }

        // Export the KM Templates
//        instance.findAllGlobalKnowledgeModuleTemplate().each {
//            exportObject(it, "${exportDir.canonicalPath}/knowledge-module-template", true)
//        }

        // Export the KM
        List<ISmartExportable> exportList = new LinkedList<ISmartExportable>()

        instance.findAllGlobalCKM().each {
            //exportObject(it, "${exportDir.canonicalPath}/knowledge-module", true)
            exportList.add(it)
        }

        instance.findAllGlobalIKM().each {
            //exportObject(it, "${exportDir.canonicalPath}/knowledge-module", true)
            exportList.add(it)
        }

        instance.findAllGlobalJKM().each {
            //exportObject(it, "${exportDir.canonicalPath}/knowledge-module", true)
            exportList.add(it)
        }

        instance.findAllGlobalLKM().each {
            //exportObject(it, "${exportDir.canonicalPath}/knowledge-module", true)
            exportList.add(it)
        }

        instance.findAllGlobalRKM().each {
            //exportObject(it, "${exportDir.canonicalPath}/knowledge-module", true)
            exportList.add(it)
        }

        instance.findAllGlobalSKM().each {
            //exportObject(it, "${exportDir.canonicalPath}/knowledge-module", true)
            exportList.add(it)
        }

        instance.findAllGlobalXKM().each {
            //exportObject(it, "${exportDir.canonicalPath}/knowledge-module", true)
            exportList.add(it)
        }

        smartExportList(exportList, "${exportDir.canonicalPath}/knowledge-module", "KM", "Global_Knowledge_Modules")

        instance.endTxn()

        instance.close()

        // Execute the export stage process
        exportStageDir()

    }
}