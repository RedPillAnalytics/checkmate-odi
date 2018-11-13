package com.redpillanalytics.odi

import groovy.util.logging.Slf4j
import oracle.odi.core.security.PasswordStorageConfiguration
import oracle.odi.setup.AuthenticationConfiguration
import oracle.odi.setup.JdbcProperties
import oracle.odi.setup.TechnologyName
import oracle.odi.setup.support.MasterRepositorySetupImpl

@Slf4j
class OdiRepository {

   String url
   String driver
   String masterRepo
   String masterPassword
   String workRepo
   String odiUser
   String odiPassword

   String dbaUser
   String dbaPassword

   TechnologyName repositoryTechnology = TechnologyName.ORACLE

   def getJdbcProps() {

      return new JdbcProperties(url, driver, masterRepo, masterPassword)
   }

   def createMaster(Boolean overwrite = false) {

      new MasterRepositorySetupImpl().createMasterRepository(
              jdbcProps,
              dbaUser,
              dbaPassword.toCharArray(),
              repositoryTechnology,
              overwrite,
              AuthenticationConfiguration.createStandaloneAuthenticationConfiguration(odiPassword.toCharArray()),
              new PasswordStorageConfiguration.InternalPasswordStorageConfiguration()
      )
   }
}
