package odi;

import oracle.odi.core.OdiInstance;
import oracle.odi.core.config.MasterRepositoryDbInfo;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.config.PoolingAttributes;
import oracle.odi.core.config.WorkRepositoryDbInfo;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.core.security.Authentication;
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl;

import javax.swing.*;
import java.io.File;

public class OdiImportProjectXML {
    private static String ImportFolderPath;

    public static void main(String[] args) {
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

            // Execution Repository
            // In case if your Execution rep is linked to a different Master repository please appropriately create new variables
            // The present codes assumes that Development and Execution are linked to the same Master Repository.

            String WorkRep_Execution="WORKREP";

            WorkRepositoryDbInfo workInfo_exec = new WorkRepositoryDbInfo(WorkRep_Execution, new PoolingAttributes());
            OdiInstance odiInstance_exec=OdiInstance.createInstance(new OdiInstanceConfig(masterInfo,workInfo_exec));
            Authentication auth_exec = odiInstance_exec.getSecurityManager().createAuthentication(Odi_User,Odi_Pass.toCharArray());
            odiInstance_exec.getSecurityManager().setCurrentThreadAuthentication(auth_exec);
            ITransactionStatus trans_exec = odiInstance_exec.getTransactionManager().getTransaction(new DefaultTransactionDefinition());


            // End Connection Configuration

            //ODI SDK CODE BEGIN

            //Exporting Options

            ImportFolderPath="/Users/josecarlos/Desktop/WORK/ODI-PROJECTS";

            Boolean DeclareMissingRepository    = true;
            Boolean ImportWithoutCipherData     = true;
            char[] ExportKey                    = null;


            // Provides the implementation for performing a smart import of a smart export file.
            SmartImportServiceImpl importsrvc = new SmartImportServiceImpl(odiInstance_exec);
            String[] XMLFiles=getXMLFiles(ImportFolderPath).split("n");
            for (String xmlfile : XMLFiles) {
                System.out.println(" Importing Object from XML File "+xmlfile);
                importsrvc.importObjectsFromXml(xmlfile, ExportKey, ImportWithoutCipherData);
                System.out.println(" Imported Object from XML File "+xmlfile);

            }

            //ODI SDK CODE END

            // Close the Instance
            odiInstance.close();
            odiInstance_exec.getTransactionManager().commit(trans_exec);
            odiInstance_exec.close();

            // Close the Instance
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString(),null,JOptionPane.ERROR_MESSAGE);
            System.out.println(e.toString());
            System.exit(0);
        }
        JOptionPane.showMessageDialog(null,
                "Project Imported Succesfully!\nFrom Folder-Path: "+ ImportFolderPath ,
                null,JOptionPane.INFORMATION_MESSAGE);
    }

    //Reading all the XML Files from the Project Folder
    public static String getXMLFiles(String DirectoryName){

        String xmlfiles="";
        String files;
        File folder = new File(DirectoryName);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++)
        {
            if (listOfFiles[i].isFile())
            {
                files = listOfFiles[i].getName();
                if (files.endsWith(".xml") || files.endsWith(".XML"))
                {
                    xmlfiles+=DirectoryName+"/"+files+"n";
                }
            }}
        return xmlfiles;
    }
}
