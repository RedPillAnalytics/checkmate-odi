package com.redpillanalytics.odi.gradle.tasks

import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.Diff

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

        log.warn("Stage Directory: ${buildDir}")
        log.warn("Source Base Directory: ${sourceBase}")

        // Find and remove the deleted objects in the source base
        project.fileTree(dir: sourceBase, include: "**/*.xml").toList().each { File srcFile ->
            if (project.fileTree(dir: buildDir, include: srcFile.name).toList() == null) {
                ant.delete(file: srcFile)
            }
        }

        // Create buildDir and sourceBase xml files list to compare
        def buildList = project.fileTree(dir: buildDir, include: "**/*.xml").toList()
        def sourceList = project.fileTree(dir: sourceBase, include: "**/*.xml").toList()

        buildList.each { buildFile ->
            sourceList.each { sourceFile ->
                if(buildFile.name == sourceFile.name) {
                    ant.copy(from: buildFile.canonicalPath, into: sourceFile.canonicalPath)
                }
            }
        }
    }

    @TaskAction
    def taskAction() {
        exportStageDir()
    }

}
