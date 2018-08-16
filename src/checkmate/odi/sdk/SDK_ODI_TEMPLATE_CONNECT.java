package checkmate.odi.sdk;

import oracle.odi.core.OdiInstance;
import oracle.odi.core.config.MasterRepositoryDbInfo;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.config.PoolingAttributes;
import oracle.odi.core.config.WorkRepositoryDbInfo;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.core.security.Authentication;

import javax.swing.*;

public class SDK_ODI_TEMPLATE_CONNECT {

	public static void main(String[] args) {
		try {
		/* CONNECTION PARAMETERS */
		String Url = "jdbc:oracle:thin:@52.14.228.220:1521/HR927";
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
		JOptionPane.showMessageDialog(null, "Task Completed!",null,JOptionPane.INFORMATION_MESSAGE);
	}
}
