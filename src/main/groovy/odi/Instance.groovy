package odi

import common.Utils
import oracle.odi.core.OdiInstance
import oracle.odi.core.config.MasterRepositoryDbInfo
import oracle.odi.core.config.OdiInstanceConfig
import oracle.odi.core.config.PoolingAttributes
import oracle.odi.core.config.WorkRepositoryDbInfo

class Instance {

   // basic connection information
   String url = "jdbc:oracle:thin:@${Utils.getHostname()}:1521/ORCL"
   String driver = "oracle.jdbc.OracleDriver";
   String masterRepo = "DEV_ODI_REPO"
   String masterPassword
   String workRepo = "WORKREP"
   String odiUser = "SUPERVISOR"
   String odiPassword

   // master repository connection
   MasterRepositoryDbInfo masterDb
   WorkRepositoryDbInfo workDb
   OdiInstance odi

   Instance(String url, String driver, String masterRepo, String workRepo, String masterPassword, String odiUser, String odiPassword) {

      this.url = url
      this.driver = driver
      this.masterRepo = masterRepo
      this.masterPassword = masterPassword
      this.workRepo = workRepo

      this.masterDb = getMasterRepo(url, driver, masterRepo, masterPassword)
      this.workDb = getWorkRepo(workRepo)

      this.odi = OdiInstance.createInstance(new OdiInstanceConfig(masterDb, workDb))
   }

   def getMasterRepo(String url, String driver, String user, String password, PoolingAttributes pooling = new PoolingAttributes()) {

      return new MasterRepositoryDbInfo(url, driver, user, password.toCharArray(), pooling)
   }

   def getWorkRepo(String user, PoolingAttributes pooling = new PoolingAttributes()) {

      return new WorkRepositoryDbInfo(user, pooling)
   }
}
