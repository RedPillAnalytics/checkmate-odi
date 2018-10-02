package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.OdiFolder
import oracle.odi.domain.project.OdiPackage
import oracle.odi.domain.project.OdiProject
import oracle.odi.domain.project.OdiUserProcedure
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiPackageFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.project.finder.IOdiUserProcedureFinder
import oracle.odi.impexp.EncodingOptions
import oracle.odi.impexp.smartie.ISmartExportService
import oracle.odi.impexp.smartie.ISmartExportable
import oracle.odi.impexp.smartie.impl.SmartExportServiceImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

import static oracle.odi.domain.project.OdiFolder.*

@Slf4j
class ExportProjectFolderTask extends DefaultTask {

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

    @Input
    @Option(option = "sourcePath",
            description = "The path to the export location. Defaults to the 'sourceBase' parameter value.")
    String sourcePath

    @Input
    @Option(option = "pname",
            description = "The project name to export. Defaults to either 'projectName' or the subdirectory name in SCM.")
    String pname

    @Input
    @Option(option = "fname",
            description = "The target folder name containing the objects to export from the ODI Repository for the SmartExport.")
    String fname

    // setSourceBase is not used, but I added it to support Gradle Incremental Build support
    @OutputDirectory
    def getSourceBase() {

        return project.file(sourcePath)
    }

    @TaskAction
    def exportProjectFolder() {

        //create ODI Instance
        def instance = new Instance(url, driver, master, work, masterPass, odi, odiPass)

        // create the export list
        List<ISmartExportable> smartExportList = new LinkedList<ISmartExportable> ()

        // create transaction items
        def tme = instance.odi.getTransactionalEntityManager()

        // create finders
        def pf = (IOdiProjectFinder)tme.getFinder(OdiProject.class)  // project
        def ff = (IOdiFolderFinder)tme.getFinder(OdiFolder.class)   // project code folders
        def mf = (IMappingFinder)tme.getFinder(Mapping.class)
        def pkf= (IOdiPackageFinder)tme.getFinder(OdiPackage.class)
        def prf= (IOdiUserProcedureFinder)tme.getFinder(OdiUserProcedure.class)

        // Validate project and folder

        def project = pf.findByCode(pname)
        if (project == null) {
            println("Project "+ pname +" not found")
        }
        else
        {
            def folderColl = ff.findByName(fname, pname)
            if (folderColl.size() == 1) {
                def folder = folderColl.iterator().next()
                folder
            }
            // list the mappings
            def mappingColl = mf.findByProject(pname,fname)
            for (Mapping mapping : mappingColl) {
                smartExportList.add( (ISmartExportable) mapping) // add the item to the list
                println(mapping.getName())
            }
             // list the packages
            def packageColl = pkf.findByProject(pname,fname)
            for (OdiPackage thePackage : packageColl) {
                smartExportList.add( (ISmartExportable) thePackage)
                println(thePackage.getName())
            }
             // list the procedures
            def procedureColl = prf.findByProject(pname,fname)
            for (OdiUserProcedure theProcedure : procedureColl) {
                smartExportList.add( (ISmartExportable) theProcedure)
                println(theProcedure.getName())
            }
        }

        //create Smart Export Service Object
        ISmartExportService smartExport = new SmartExportServiceImpl(instance.odi)
        EncodingOptions encdOption = new EncodingOptions("1.0", "ISO8859_9",  "ISO-8859-9")

        instance.beginTxn()

        smartExport.exportToXml (smartExportList, sourcePath, pname, true, false, encdOption, false, null, null, true)

        instance.endTxn()

    }
}