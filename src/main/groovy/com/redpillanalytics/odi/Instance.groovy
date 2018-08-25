package com.redpillanalytics.odi

import com.redpillanalytics.common.Utils
import groovy.util.logging.Slf4j
import oracle.odi.core.OdiInstance
import oracle.odi.core.config.MasterRepositoryDbInfo
import oracle.odi.core.config.OdiInstanceConfig
import oracle.odi.core.config.PoolingAttributes
import oracle.odi.core.config.WorkRepositoryDbInfo
import oracle.odi.core.persistence.transaction.ITransactionStatus
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition
import oracle.odi.core.security.Authentication

@Slf4j
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
   ITransactionStatus transaction

   Instance(String url, String driver, String masterRepo, String workRepo, String masterPassword, String odiUser, String odiPassword) {

      this.url = url
      this.driver = driver
      this.masterRepo = masterRepo
      this.masterPassword = masterPassword
      this.workRepo = workRepo

      this.masterDb = getMasterRepo(url, driver, masterRepo, masterPassword)
      this.workDb = getWorkRepo(workRepo)

      this.odi = OdiInstance.createInstance(new OdiInstanceConfig(masterDb, workDb))

      Authentication auth = odi.getSecurityManager().createAuthentication(odiUser, odiPassword.toCharArray())
      odi.getSecurityManager().setCurrentThreadAuthentication(auth)

   }

   def getMasterRepo(String url, String driver, String user, String password, PoolingAttributes pooling = new PoolingAttributes()) {

      return new MasterRepositoryDbInfo(url, driver, user, password.toCharArray(), pooling)
   }

   def getWorkRepo(String user, PoolingAttributes pooling = new PoolingAttributes()) {

      return new WorkRepositoryDbInfo(user, pooling)
   }

   def beginTxn() {

      this.transaction = odi.getTransactionManager()
              .getTransaction(new DefaultTransactionDefinition())

   }

   def endTxn() {

      odi.getTransactionManager().commit(this.transaction)
      odi.close(this.transaction)
   }
}
