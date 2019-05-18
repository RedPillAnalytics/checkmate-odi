FROM oraclelinux:7-slim

RUN yum -y install wget \
    && wget -O jdk-8u211-linux-x64.rpm https://s3.amazonaws.com/rpa-checkmate-resources/java/jdk-8u211-linux-x64.rpm \
    && yum -y localinstall jdk-8u211-linux-x64.rpm \
    && java -version

#
# Environment variables required for this build (do NOT change)
# -------------------------------------------------------------
ENV ODI_PKG1=fmw_12.2.1.3.0_odi_Disk1_1of2.zip \
    ODI_PKG2=fmw_12.2.1.3.0_odi_Disk1_2of2.zip \
    ODI_JAR=fmw_12.2.1.3.0_odi.jar \
    ODI_JAR2=fmw_12.2.1.3.0_odi2.jar \
    ORACLE_HOME=/u01/oracle \
    PATH=$PATH:/u01/oracle/oracle_common/common/bin:/u01/oracle/container-scripts \
    ODI_AGENT_PORT=20910 \
    DOMAIN_NAME="${DOMAIN_NAME:-base_domain}" \
    DOMAIN_ROOT="${DOMAIN_ROOT:-/u01/oracle/user_projects/domains}"

#
# Proxy needs to be set before running yum 
# ----------------------------------------
RUN mkdir /u01 && \
    useradd -b /u01 -d /u01/oracle -m -s /bin/bash oracle && \
    mkdir -p /u01/oracle/container-scripts /u01/oracle/logs && \
    chmod a+xr /u01 

#
# Copy required files to build this image
# ---------------------------------------
COPY $ODI_PKG1 $ODI_PKG2 oraInst.loc /u01/
COPY container-scripts/* /u01/oracle/container-scripts/

RUN chown oracle:oracle -R /u01 && \
    chmod a+xr /u01/oracle/container-scripts/*.*

#
# Install as oracle user
# ----------------------
USER oracle
RUN cd /u01 && \
    $JAVA_HOME/bin/jar xf /u01/$ODI_PKG1  && \
    $JAVA_HOME/bin/jar xf /u01/$ODI_PKG2  && \
    $JAVA_HOME/bin/java -jar /u01/$ODI_JAR -silent -invPtrLoc /u01/oraInst.loc -jreLoc $JAVA_HOME -ignoreSysPrereqs -force -novalidation ORACLE_HOME=$ORACLE_HOME INSTALL_TYPE="Standalone Installation" && \
     rm /u01/$ODI_PKG1 /u01/$ODI_PKG2 /u01/$ODI_JAR /u01/$ODI_JAR2 /u01/fmw_12213_readme.htm /u01/oraInst.loc

WORKDIR $ORACLE_HOME

#
# Expose all Ports
#-----------------
EXPOSE $ODI_AGENT_PORT

#
# Define default command to start 
# -------------------------------
CMD ["/u01/oracle/container-scripts/CreateODIDomainandRunAgent.sh"]

