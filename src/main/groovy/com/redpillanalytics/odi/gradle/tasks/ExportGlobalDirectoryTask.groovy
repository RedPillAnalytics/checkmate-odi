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

        try{

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

            // Export the KM
            List<ISmartExportable> exportList = new LinkedList<ISmartExportable>()

            instance.findAllGlobalCKM().each {
                exportList.add(it)
            }

            instance.findAllGlobalIKM().each {
                exportList.add(it)
            }

            instance.findAllGlobalJKM().each {
                exportList.add(it)
            }

            instance.findAllGlobalLKM().each {
                exportList.add(it)
            }

            instance.findAllGlobalRKM().each {
                exportList.add(it)
            }

            instance.findAllGlobalSKM().each {
                exportList.add(it)
            }

            instance.findAllGlobalXKM().each {
                exportList.add(it)
            }

            smartExportList(exportList, "${exportDir.canonicalPath}/knowledge-module", "KM", "Global_Knowledge_Modules")

            instance.endTxn()

            instance.close()

        } catch(Exception e ) {
            // Close the Connection
            instance.close()
            // Throw the Exception
            throw e
        }

        // Execute the export stage process
        exportStageDir()

    }
}