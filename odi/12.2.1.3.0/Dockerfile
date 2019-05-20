FROM oraclelinux:7-slim AS builder

ENV JAVA_HOME=/usr/java/jdk1.8.0_211-amd64 \
    ODI_JAR=fmw_12.2.1.3.0_odi.jar \
    ODI_JAR2=fmw_12.2.1.3.0_odi2.jar \
    ORACLE_HOME=/u01/oracle/odi1 \
    PATH=$PATH:/u01/oracle/oracle_common/common/bin:/u01/oracle/container-scripts \
    ODI_AGENT_PORT=20910 \
    DOMAIN_NAME="${DOMAIN_NAME:-base_domain}" \
    DOMAIN_ROOT="${DOMAIN_ROOT:-/u01/oracle/user_projects/domains}"

RUN yum -y install wget which unzip make gcc && \
    wget -q -O jdk-8u211-linux-x64.rpm https://s3.amazonaws.com/rpa-checkmate-resources/java/jdk-8u211-linux-x64.rpm && \
    yum -y localinstall jdk-8u211-linux-x64.rpm && \
    java -version

RUN mkdir su-exec-tmp && \
    wget --quiet -O su-exec-tmp/su-exec.zip https://t.co/hsr02to11V && \
    unzip su-exec-tmp/su-exec.zip -d su-exec-tmp && \
    cd su-exec-tmp/su-exec-0.2 && \
    make && \
    mv su-exec /usr/bin/ && \
    chmod u+s /usr/bin/su-exec && \
    cd /

RUN wget -q -O fmw_12.2.1.3.0_odi.jar https://s3.amazonaws.com/rpa-checkmate-resources/odi/12.2.1.3.0/software/fmw_12.2.1.3.0_odi.jar && \
    wget -q -O fmw_12.2.1.3.0_odi2.jar https://s3.amazonaws.com/rpa-checkmate-resources/odi/12.2.1.3.0/software/fmw_12.2.1.3.0_odi2.jar

RUN mkdir /u01 && \
    useradd -b /u01 -d /u01/oracle -m -s /bin/bash oracle && \
    mkdir -p /u01/oracle/container-scripts /u01/oracle/logs && \
    chmod a+xr /u01

COPY oraInst.loc /u01/
COPY container-scripts/* /u01/oracle/container-scripts/

RUN chown oracle:oracle -R /u01 && \
    chmod a+xr /u01/oracle/container-scripts/*.*

RUN su-exec oracle $JAVA_HOME/bin/java -jar $ODI_JAR -silent -invPtrLoc /u01/oraInst.loc -jreLoc $JAVA_HOME -ignoreSysPrereqs -force -novalidation ORACLE_HOME=$ORACLE_HOME INSTALL_TYPE="Standalone Installation"

ENTRYPOINT [ "bash" ]
