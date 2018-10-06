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

@Slf4j
class Instance {

   // basic connection information
   String url
   String driver
   String masterRepo
   String masterPassword
   String workRepo
   String odiUser
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
      this.odiUser = odiUser
      this.odiPassword = odiPassword

      // testing making the connection later, only when it's used
      // similar to what I did with MBeans in the Checkmate OBI plugin
      //connect()

   }

   static def getMasterRepo(String url, String driver, String user, String password, PoolingAttributes pooling = new PoolingAttributes()) {

      try {
         return new MasterRepositoryDbInfo(url, driver, user, password.toCharArray(), pooling)
      }
      catch (NullPointerException e) {

         throw new Exception("A portion of the Master Repository credentials are missing.")
      }
   }

   static def getWorkRepo(String user, PoolingAttributes pooling = new PoolingAttributes()) {

      return new WorkRepositoryDbInfo(user, pooling)
   }

   def connect() {

      this.masterDb = getMasterRepo(url, driver, masterRepo, masterPassword)
      this.workDb = getWorkRepo(workRepo)

      this.odi = OdiInstance.createInstance(new OdiInstanceConfig(masterDb, workDb))
      this.odiUser = odiUser
      this.odiPassword = odiPassword

      Authentication auth = odi.getSecurityManager().createAuthentication(odiUser, odiPassword as char[])
      odi.getSecurityManager().setCurrentThreadAuthentication(auth)

   }

   def getOdiProject(String projectCode ) {

      return findProjectName(projectCode)
   }

   def beginTxn() {

      this.transaction = odi.getTransactionManager()
              .getTransaction(new DefaultTransactionDefinition())

   }

   def endTxn(Boolean commit = true) {

      if (commit) {
         odi.getTransactionManager().commit(this.transaction)
      }

      odi.close()
   }

   def getProjectFinder() {
      return (IOdiProjectFinder) odi.getTransactionalEntityManager().getFinder(OdiProject.class)
   }

   def findProjectName(String code) {
      return getProjectFinder().findByCode(code)
   }

   def getFolderFinder() {
      return (IOdiFolderFinder) odi.getTransactionalEntityManager().getFinder(OdiFolder.class)
   }

   def findFolder(String folder, String project) {

      return getFolderFinder().findByName(folder, project)
   }

   def getMappingFinder() {

      return (IMappingFinder) odi.getTransactionalEntityManager().getFinder(Mapping.class)
   }

   def findMapping(String project, String folder) {

      return getMappingFinder().findByProject(project, folder)
   }

   def getPackageFinder() {

      return (IOdiPackageFinder) odi.getTransactionalEntityManager().getFinder(OdiPackage)
   }

   def findPackage(String project, String folder) {

      return getPackageFinder().findByProject(project, folder)
   }

   def getProcedureFinder() {

      return (IOdiUserProcedureFinder) odi.getTransactionalEntityManager().getFinder(OdiUserProcedure.class)
   }

   def findProcedure(String project, String folder) {

      return getProcedureFinder().findByProject(project, folder)
   }

   def getProjects() {

      return projectFinder.findAll().toArray()
   }
}
