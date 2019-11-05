package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

@Slf4j
class ExportTopologyDirectoryTask extends ExportDirectoryTask {

    @Internal
    String category = 'topology'

//    @Internal
//    List objectList = ['technology', 'context', 'data-server', 'physical-schema', 'physical-agent', 'logical-agent', 'logical-schema']


    @TaskAction
    def exportObjects() {

//        objectList.each { object ->
//            assert "'object' must be one of '${objectList.toString()}'." && objectList.contains(object)
//        }
//
//        instance.connect()
//
//        Integer count = 0
//
//        objectList.each { objectType ->
//
//            log.info "Exporting ${objectType}s..."
//
//            // capture the class name to use
//            // fancy regex... but all of this is to make 'data-server' == 'DataServer'
//            def finder = objectType.replaceAll(~/([^-]+)(?:-)?(\w)?(.+)/) { String all, String first, String capital, String rest ->
//                "findAll${first.capitalize()}${capital ? capital.toUpperCase() : ''}$rest"
//            }
//
//            // begin the transaction
//            instance.beginTxn()
//            // export the topology objects
//            instance."$finder"().each { object ->
//                count++
//                logger.debug "object name: ${object.name}"
//                exportObject(object, "${exportDir.canonicalPath}/${objectType}")
//            }
//        }
//        instance.endTxn()
//        if (count == 0) throw new Exception("No topology objects match provided filters; object types: ${objectList}")

        instance.connect()

        instance.beginTxn()

        // export the topology in export directory
        exportTopology(exportDir.canonicalPath)

        instance.endTxn()

        instance.close()

        // execute the export stage process
        exportStageDir()

    }

}
