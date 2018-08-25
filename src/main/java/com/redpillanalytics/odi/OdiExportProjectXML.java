package com.redpillanalytics.odi;

import oracle.odi.core.OdiInstance;
import oracle.odi.core.config.MasterRepositoryDbInfo;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.config.PoolingAttributes;
import oracle.odi.core.config.WorkRepositoryDbInfo;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.core.security.Authentication;
import oracle.odi.domain.project.OdiFolder;
import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.impexp.EncodingOptions;
import oracle.odi.impexp.support.ExportServiceImpl;

import javax.swing.*;
import java.io.IOException;
import java.text.ParseException;


public class OdiExportProjectXML {
    private static String Project_Code;
    private static OdiProject project;
    private static String   Folder_Name;
    private static OdiFolder folder;
    private static String ExportFolderPath;

    public static void main(String[] args){
        try {
            /* CONNECTION PARAMETERS */
            String Url = "jdbc:oracle:thin:@odi-repo.csagf46svk9g.us-east-2.rds.amazonaws.com:1521/ORCL";
            String Driver = "oracle.jdbc.OracleDriver";
            String Master_User = "DEV_ODI_REPO";
            String Master_Pass = "Welcome1";
            String WorkRep = "WORKREP";
            String Odi_User = "SUPERVISOR";
            String Odi_Pass = "Welcome1";

            // Connection
            MasterRepositoryDbInfo masterInfo = new MasterRepositoryDbInfo(Url, Driver, Master_User,
                    Master_Pass.toCharArray(), new PoolingAttributes());
            WorkRepositoryDbInfo workInfo = new WorkRepositoryDbInfo(WorkRep, new PoolingAttributes());
            OdiInstance odiInstance = OdiInstance.createInstance(new OdiInstanceConfig(masterInfo, workInfo));
            Authentication auth = odiInstance.getSecurityManager().createAuthentication(Odi_User, Odi_Pass.toCharArray());
            odiInstance.getSecurityManager().setCurrentThreadAuthentication(auth);
            ITransactionStatus trans = odiInstance.getTransactionManager()
                    .getTransaction(new DefaultTransactionDefinition());
            // End Connection Configuration

            //ODI SDK CODE BEGIN

            //Exporting Options

            ExportFolderPath="/Users/josecarlos/Desktop/WORK/ODI-PROJECTS";

            Boolean ExportPackageScen       = true;
            Boolean ExportInterfaceScen     = true;
            Boolean ExportProcedureScen     = true;
            Boolean ExportVariableScen      = false;
            Boolean RecursiveExport         = true;
            Boolean OverWriteFile           = true;
            Boolean ExportWithoutCipherData = true;
            char[] ExportKey                = null;

            Project_Code   ="TEST-PROJECT";
            Folder_Name    ="TEST-FOLDER";

            //Exports the given object to a XML file along with its parent objects. The resulting file can be imported by Smart Import
            // Get Project
            project = ((IOdiProjectFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiProject.class)).findByCode(Project_Code);
            ExportServiceImpl export=new ExportServiceImpl(odiInstance);
            EncodingOptions EncdOption = new EncodingOptions();
            System.out.println( " Exporting Project: " +Project_Code);
            //export.exportAllScenarii(project, ExportFolderPath, ExportPackageScen, ExportInterfaceScen, ExportProcedureScen, ExportVariableScen, EncdOption, RecursiveExport, OverWriteFile);

            export.exportToXmlWithParents(project, ExportFolderPath, OverWriteFile, RecursiveExport, EncdOption, ExportKey, ExportWithoutCipherData);

            //ODI SDK CODE END
            // Close the Instance
            odiInstance.getTransactionManager().commit(trans);
            odiInstance.close();
            // Close the Instance
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString(),null,JOptionPane.ERROR_MESSAGE);
            System.out.println(e.toString());
            System.exit(0);
        }
        JOptionPane.showMessageDialog(null,
                "Project " + project.getName() + " exported Succesfully!\nFolder-Path: "+ ExportFolderPath ,
                null,JOptionPane.INFORMATION_MESSAGE);
    }
}
