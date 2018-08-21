package checkmate.odi.sdk;

import oracle.odi.core.OdiInstance;
import oracle.odi.core.config.MasterRepositoryDbInfo;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.config.PoolingAttributes;
import oracle.odi.core.config.WorkRepositoryDbInfo;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.core.security.Authentication;
import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.finder.IOdiProjectFinder;

import javax.swing.*;

public class OdiGetProjects {
	private static String projectNameOutput;

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
		// End Connection Configuration
		
		//ODI SDK CODE BEGIN
		
		// List all the projects
		 
		Object[] project = ((IOdiProjectFinder)odiInstance.getTransactionalEntityManager().
		getFinder(OdiProject.class)).findAll().toArray();
		//List of Projects to Output
		projectNameOutput = "EXISTING PROJECTS:\n";
		 
		 for ( int i =0 ;i <project.length ; i++ )  {
		    OdiProject pro1=(OdiProject) project[i];
		   //We need to cast the object project accordingly, for this example OdiProject
             projectNameOutput += String.format("-> %s\n", pro1.getName());
		}
			
		//ODI SDK CODE END
			// Close the Instance
			odiInstance.getTransactionManager().commit(trans);
			odiInstance.close();
			// Close the Instance
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(),null,JOptionPane.ERROR_MESSAGE);
		}
		JOptionPane.showMessageDialog(null, projectNameOutput,null,JOptionPane.INFORMATION_MESSAGE);
	}
}
