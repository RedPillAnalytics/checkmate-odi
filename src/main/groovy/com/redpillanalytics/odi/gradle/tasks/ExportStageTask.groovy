package com.redpillanalytics.odi.gradle.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.Diff

@Slf4j
class ExportStageTask extends ExportDirectoryTask {

    @Internal
    def xmlDiff(File controlXml, File testXml) {

        Diff result = DiffBuilder.compare(Input.fromFile(controlXml))
                .withTest(Input.fromFile(testXml))
                .withNodeFilter({ node -> node.getNodeName() != "Encryption" })
                .build()

        return result.hasDifferences()

    }

    @Internal
    def exportStageDir() {

        def buildDir = project.file(project.extensions.odi.buildDir)
        log.info("Build Directory: ${buildDir}")
        def sourceBase = project.file(project.extensions.odi.sourceBase)
        log.info("Source Base Directory: ${sourceBase}")

        // Find and remove the deleted objects in the source base comparing with the build objects
        project.fileTree(dir: sourceBase, include: "**/*.xml").toList().each { File srcFile ->
            if (!project.fileTree(dir: buildDir, include: "**/*.xml").collect {it.name}.contains(srcFile.name)) {
                ant.delete(file: srcFile)
            }
        }

        // Create buildDir and sourceBase xml files list to compare
        def buildList = project.fileTree(dir: buildDir, include: "**/*.xml").toList()
        log.debug("Build Dir List: ${buildList}")
        def sourceList = project.fileTree(dir: sourceBase, include: "**/*.xml").toList()
        log.debug("Source base List: ${sourceList}")

        buildList.each { buildFile ->
            // Compare the XML files and if the file change copy from Build to Source Base
            sourceList.each { sourceFile ->
                if(buildFile.name == sourceFile.name) {
                    if(xmlDiff(buildFile, sourceFile)) {
                        ant.copy(file: buildFile.canonicalPath,
                                tofile: sourceFile.canonicalPath,
                                overwrite: true)
                        println("Copying file: ${buildFile.name} ...")
                    }

                }
            }
            // If the XML file does not exist in Source Base copy from Build to Source Base
            ant.copy(file: buildFile.canonicalPath,
                    tofile: "${sourceBase}/${buildFile.canonicalPath.minus(buildDir.canonicalPath)}",
                    overwrite: true)
        }

        // Delete buildDir when all the files are processed
        ant.delete(dir: buildDir)
    }

    @TaskAction
    def taskAction() {
        exportStageDir()
    }

}
