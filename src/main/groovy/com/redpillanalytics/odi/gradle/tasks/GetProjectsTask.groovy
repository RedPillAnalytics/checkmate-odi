package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiProject
import oracle.odi.domain.project.finder.IOdiProjectFinder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option


@Slf4j
class GetProjectsTask extends DefaultTask {

    @Input
    @Option(option = "url",
            description = "The JDBC URL of the Master Repository.")
    String url

    @Input
    @Option(option = "driver",
            description = "The JDBC driver class of the Master Repository.")
    String driver

    @Input
    @Option(option = "master",
            description = "The schema name of the Master repository.")
    String master

    @Input
    @Option(option = "work",
            description = "The schema name of the Work repository.")
    String work

    @Input
    @Option(option = "masterPass",
            description = "The password for the Master repository.")
    String masterPass

    @Input
    @Option(option = "odi",
            description = "The name of the ODI user.")
    String odi

    @Input
    @Option(option = "odiPass",
            description = "The password of the ODI user.")
    String odiPass

    @TaskAction
    def getProjects() {

        def instance = new Instance(url, driver, master, work, masterPass, odi, odiPass)

        //Get all projects and save into "projects"
        def projects = ((IOdiProjectFinder)instance.odi.getTransactionalEntityManager().getFinder(OdiProject.class)).findAll().toArray()

        instance.beginTxn()

        projects.each { OdiProject project ->
            //Action to do to the projects retrieved
            println(project.name)
        }
            instance.endTxn()

        }
}