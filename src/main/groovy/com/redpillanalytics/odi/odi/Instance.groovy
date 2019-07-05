package com.redpillanalytics.odi.odi


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
import oracle.odi.domain.mapping.ReusableMapping
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.mapping.finder.IReusableMappingFinder
import oracle.odi.domain.model.OdiModel
import oracle.odi.domain.model.OdiModelFolder
import oracle.odi.domain.model.finder.IOdiModelFinder
import oracle.odi.domain.model.finder.IOdiModelFolderFinder
import oracle.odi.domain.project.OdiFolder
import oracle.odi.domain.project.OdiKM
import oracle.odi.domain.project.OdiPackage
import oracle.odi.domain.project.OdiProject
import oracle.odi.domain.project.OdiSequence
import oracle.odi.domain.project.OdiUserFunction
import oracle.odi.domain.project.OdiUserProcedure
import oracle.odi.domain.project.OdiVariable
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiKMFinder
import oracle.odi.domain.project.finder.IOdiPackageFinder
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.project.finder.IOdiSequenceFinder
import oracle.odi.domain.project.finder.IOdiUserFunctionFinder
import oracle.odi.domain.project.finder.IOdiUserProcedureFinder
import oracle.odi.domain.project.finder.IOdiVariableFinder
import oracle.odi.domain.runtime.loadplan.OdiLoadPlan
import oracle.odi.domain.runtime.loadplan.finder.IOdiLoadPlanFinder
import oracle.odi.domain.runtime.scenario.OdiScenario
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder
import oracle.odi.domain.topology.OdiContext
import oracle.odi.domain.topology.OdiDataServer
import oracle.odi.domain.topology.OdiLogicalAgent
import oracle.odi.domain.topology.OdiLogicalSchema
import oracle.odi.domain.topology.OdiPhysicalAgent
import oracle.odi.domain.topology.OdiPhysicalSchema
import oracle.odi.domain.topology.OdiTechnology
import oracle.odi.domain.topology.finder.IOdiContextFinder
import oracle.odi.domain.topology.finder.IOdiDataServerFinder
import oracle.odi.domain.topology.finder.IOdiLogicalAgentFinder
import oracle.odi.domain.topology.finder.IOdiLogicalSchemaFinder
import oracle.odi.domain.topology.finder.IOdiPhysicalAgentFinder
import oracle.odi.domain.topology.finder.IOdiPhysicalSchemaFinder
import oracle.odi.domain.topology.finder.IOdiTechnologyFinder

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

      log.debug "Existing projects: ${projectFinder.findAll().toString()}"

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

   def flush() {
      odi.getTransactionalEntityManager().flush()
   }

   // IFinder Objects

   // Project Finders

   def getProjectFinder() {
      return (IOdiProjectFinder) odi.getTransactionalEntityManager().getFinder(OdiProject.class)
   }

   def getOdiProject(String projectCode) {

      return findProject(projectCode)
   }

   def findProject(String code, Boolean ignore = true) {
      def project = getProjectFinder().findByCode(code)
      if (!project && !ignore) throw new Exception("Project code '${code}' does not exist.")
      return project
   }

   def getProjects() {

      return projectFinder.findAll().toArray()
   }

   // Folder Finders

   def getFolderFinder() {
      return (IOdiFolderFinder) odi.getTransactionalEntityManager().getFinder(OdiFolder.class)
   }

   def findFolder(String folder, String project, Boolean ignore = true) {
      def odiFolders = getFolderFinder().findByName(folder, project)
      if (!odiFolders && !ignore) throw new Exception("Folder '${folder}' does not exist in project '${project}'.")
      return odiFolders
   }

   def getFoldersProjectFinder() {
      return (IOdiFolderFinder) odi.getTransactionalEntityManager().getFinder(OdiFolder.class)
   }

   def findFoldersProject(String project, Boolean ignore = true) {
      def odiProjects = getFoldersProjectFinder().findByProject(project)
      if (!odiProjects[0] && !ignore) throw new Exception("No folders exist in project '${project}'.")
      return getFoldersProjectFinder().findByProject(project)
   }

   // Mapping Finders

   def getMappingFinder() {

      return (IMappingFinder) odi.getTransactionalEntityManager().getFinder(Mapping.class)
   }

   def findMapping(String project, String folder) {

      return getMappingFinder().findByProject(project, folder)
   }

   // Package Finders

   def getPackageFinder() {

      return (IOdiPackageFinder) odi.getTransactionalEntityManager().getFinder(OdiPackage.class)
   }

   def findPackage(String project, String folder) {

      return getPackageFinder().findByProject(project, folder)
   }

   // Procedure Finders

   def getProcedureFinder() {

      return (IOdiUserProcedureFinder) odi.getTransactionalEntityManager().getFinder(OdiUserProcedure.class)
   }

   def findProcedure(String project, String folder) {

      return getProcedureFinder().findByProject(project, folder)
   }

   // Reusable-Mapping Finders

   def getReusableMappingFinder() {

      return (IReusableMappingFinder) odi.getTransactionalEntityManager().getFinder(ReusableMapping.class)
   }

   def findReusableMapping(String project, String folder) {

      return getReusableMappingFinder().findByProject(project, folder)
   }

   // Model Finders
   def getModelFinder() {
      return (IOdiModelFinder) odi.getTransactionalEntityManager().getFinder(OdiModel.class)
   }

   def findAllModels() {
      return getModelFinder().findAll().toArray()
   }

   def findModelbyCode(String modelCode, Boolean ignore = true) {
      def model = getModelFinder().findByCode(modelCode)
      if (!model && !ignore) throw new Exception("Model code '${modelCode}' does not exist.")
      return model
   }

   // Model Folder Finders
   def getModelFolderFinder() {
      return (IOdiModelFolderFinder) odi.getTransactionalEntityManager().getFinder(OdiModelFolder.class)
   }

   def findAllModelFolders() {
      return getModelFolderFinder().findAll().toArray()
   }

   def findModelFolderbyName(String name) {
      def folder = getModelFolderFinder().findByName(name)
      if (!folder) throw new Exception("Model '${folder}' does not exist.")
      return folder
   }

   // Scenario Finders
   def getScenarioFinder() {
      return (IOdiScenarioFinder) odi.getTransactionalEntityManager().getFinder(OdiScenario.class)
   }

   def findAllScenarios() {
      return getScenarioFinder().findAll()
   }

   def findScenarioBySourceMapping(Number mappingInternalID, boolean useTimestamp) {
      return getScenarioFinder().findLatestBySourceMapping(mappingInternalID, useTimestamp)
   }

   def findScenarioBySourcePackage(Number packageInternalID, boolean useTimestamp) {
      return getScenarioFinder().findLatestBySourcePackage(packageInternalID, useTimestamp)
   }

   def findScenarioBySourceUserProcedure(Number userProcedureInternalID, boolean useTimestamp) {
      return getScenarioFinder().findLatestBySourceUserProcedure(userProcedureInternalID, useTimestamp)
   }

   // Load Plans Finder
   def getLoadPlanFinder() {
      def finder = (IOdiLoadPlanFinder) odi.getTransactionalEntityManager().getFinder(OdiLoadPlan.class)
      return finder
   }

   def findAllLoadPlans() {
      def list = getLoadPlanFinder().findAll()
      log.info "Load plan list: $list"
      return list
   }

   // Variable Finder
   def getVariableFinder() {
      def finder = (IOdiVariableFinder) odi.getTransactionalEntityManager().getFinder(OdiVariable.class)
      return finder
   }

   def findAllGlobalVariables() {
      def list = getVariableFinder().findAllGlobals()
      log.info "Global Variables list: $list"
      return list
   }

   def findVariable(String projectCode) {
      def list = getVariableFinder().findByProject(projectCode)
      log.info "Project Variables list: $list"
      return list
   }

   // KM Finder
   def getKMFinder() {
      def finder = (IOdiKMFinder) odi.getTransactionalEntityManager().getFinder(OdiKM.class)
      return finder
   }

   def findAllGlobalKMs() {
      def list = getKMFinder().findAllGlobals()
      log.info "Global KMs list: $list"
      return list
   }

   def findKM(String projectCode) {
      def list = getKMFinder().findByProject(projectCode)
      log.info "KM list: $list"
      return list
   }

   // Sequence Finder
   def getSequenceFinder() {
      def finder = (IOdiSequenceFinder) odi.getTransactionalEntityManager().getFinder(OdiSequence.class)
      return finder
   }

   def findAllGlobalSequences() {
      def list = getSequenceFinder().findAllGlobals()
      log.info "Global Sequences list: $list"
      return list
   }

   def findSequence(String projectCode) {
      def list = getSequenceFinder().findByProject(projectCode)
      log.info "Sequence list: $list"
      return list
   }

   // UserFunction Finder
   def getUserFunctionFinder() {
      def finder = (IOdiUserFunctionFinder) odi.getTransactionalEntityManager().getFinder(OdiUserFunction.class)
      return finder
   }

   def findAllGlobalUserFunctions() {
      def list = getUserFunctionFinder().findAllGlobals()
      log.info "UserFunction Sequences list: $list"
      return list
   }

   def findUserFunction(String projectCode) {
      def list = getUserFunctionFinder().findByProject(projectCode)
      log.info "UserFunctionFinder list: $list"
      return list
   }

   // Context Finder
   def getContextFinder() {
      def finder = (IOdiContextFinder) odi.getTransactionalEntityManager().getFinder(OdiContext.class)
      return finder
   }

   def findAllContext() {
      def list = getContextFinder().findAll()
      log.info "Context list: $list"
      return list
   }

   // Technology Finder
   def getTechnologyFinder() {
      def finder = (IOdiTechnologyFinder) odi.getTransactionalEntityManager().getFinder(OdiTechnology.class)
      return finder
   }

   def findAllTechnology() {
      def list = getTechnologyFinder().findAll()
      log.info "Technology list: $list"
      return list
   }

   def findUsedTechnologies() {
      def list = getTechnologyFinder().findUsedTechnologies()
      log.info "Technology list: $list"
      return list
   }

   // DataServer Finder
   def getDataServerFinder() {
      def finder = (IOdiDataServerFinder) odi.getTransactionalEntityManager().getFinder(OdiDataServer.class)
      return finder
   }

   def findAllDataServer() {
      def list = getDataServerFinder().findAll()
      log.info "DataServer list: $list"
      return list
   }

   // PhysicalSchema Finder
   def getPhysicalSchemaFinder() {
      def finder = (IOdiPhysicalSchemaFinder) odi.getTransactionalEntityManager().getFinder(OdiPhysicalSchema.class)
      return finder
   }

   def findAllPhysicalSchema() {
      def list = getPhysicalSchemaFinder().findAll()
      log.info "PhysicalSchema list: $list"
      return list
   }

   // PhysicalAgent Finder
   def getPhysicalAgentFinder() {
      def finder = (IOdiPhysicalAgentFinder) odi.getTransactionalEntityManager().getFinder(OdiPhysicalAgent.class)
      return finder
   }

   def findAllPhysicalAgent() {
      def list = getPhysicalAgentFinder().findAll()
      log.info "PhysicalAgent list: $list"
      return list
   }

   // LogicalAgent Finder
   def getLogicalAgentFinder() {
      def finder = (IOdiLogicalAgentFinder) odi.getTransactionalEntityManager().getFinder(OdiLogicalAgent.class)
      return finder
   }

   def findAllLogicalAgent() {
      def list = getLogicalAgentFinder().findAll()
      log.info "LogicalAgent list: $list"
      return list
   }

   // LogicalSchema Finder
   def getLogicalSchemaFinder() {
      def finder = (IOdiLogicalSchemaFinder) odi.getTransactionalEntityManager().getFinder(OdiLogicalSchema.class)
      return finder
   }

   def findAllLogicalSchema() {
      def list = getLogicalSchemaFinder().findAll()
      log.info "LogicalSchema list: $list"
      return list
   }

}
