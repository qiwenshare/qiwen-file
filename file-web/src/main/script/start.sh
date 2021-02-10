#!/bin/bash
. /etc/profile
. ~/.bashrc
. ~/.bash_profile
#support jsch commons

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`

SPRING_PROFILES_ACTIVE="prod"
export SPRING_PROFILES_ACTIVE

echo "-------- SPRING_PROFILES_ACTIVE ${SPRING_PROFILES_ACTIVE}--------"

echo "--------start app ${DEPLOY_DIR}--------"
PARENT_DIR=$(dirname "$PWD")
CONF_DIR=$DEPLOY_DIR/conf
LIB_DIR=$DEPLOY_DIR/lib
SERVER_NAME=`cat $CONF_DIR/config/application.yml | grep -w "name:" | grep -v "#" | awk  'NR==1{print $2}' | tr -d '\r'`
SERVER_PORT=`cat $CONF_DIR/config/application.yml | grep -w "port:" | grep -v "#" | awk  'NR==1{print $2}' | tr -d '\r'`

#REM **********************************************************************************************
LOG_PATH="/export/log/qiwen-file"
GC_LOG_PATH="/export/log/qiwen-file/gc"
if [ "${LOG_PATH}" == "" ] ; then
	LOG_PATH=$PARENT_DIR/logs/${SERVER_NAME}
fi
if [ "${GC_LOG_PATH}" == "" ] ; then
	GC_LOG_PATH=$PARENT_DIR/logs/${SERVER_NAME}/gclog
fi

if [ ! -d ${LOG_PATH}  ];then
  mkdir -p ${LOG_PATH}
fi
if [ ! -d ${GC_LOG_PATH}  ];then
  mkdir -p ${GC_LOG_PATH}
fi

STDOUT_FILE=${LOG_PATH}/nohup.out

#if use self jdk,modify
#JAVA_HOME="/usr/lib/jvm/jdk1.8.0_191"
if [ "${JAVA_HOME}" != "" ] ; then
	export JAVA_HOME
	export PATH=$PATH:JAVA_HOME/bin
	echo JAVA_HOME:${JAVA_HOME}
else
  echo "JAVA_HOME not set!!!"
  exit 1
fi

USER_VMARGS="-D64 -server -Xmx1g -Xms1g -Xmn521m -Xss256k "

#GC_OPTS=""
GC_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$LOG_PATH/heapdump.$$.hprof -XX:ErrorFile=$LOG_PATH/hs_err_pid$$.log -Xloggc:$GC_LOG_PATH/gc.$$.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC "

JMX_PORT=""
JAVA_JMX_OPTS=""
if [ "${JMX_PORT}" != "" ] ; then
	JAVA_JMX_OPTS="-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=${JMX_PORT} "
fi

#JAVA_DEBUG=""
#if [ "$1" = "debug" ]; then
#    JAVA_DEBUG=" -Xdebug "
#fi

JAVA_OPTS=""
#if [ "${JAVA_OPTS}" != "" ]; then
#   JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
#fi
#REM **********************************************************************************************

#prevent repeated start
PIDS=`ps -ef | grep java | grep "$CONF_DIR" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME already started!"
    echo "PID: $PIDS"
    exit 1
fi

if [ -n "$SERVER_PORT" ]; then
    SERVER_PORT_COUNT=`netstat -tln | grep $SERVER_PORT | wc -l`
    if [ $SERVER_PORT_COUNT -gt 0 ]; then
        echo "ERROR: The $SERVER_NAME port $SERVER_PORT already used!"
        exit 1
    fi
fi
#=====================

######################cloud trace##########################
TRACE_CLOUD_TRACE_URL=http://localhost:9411
TRACE_CLOUD_TRACE_PROBABILITY=1.0
TRACE_CLOUD_TRACE_ENABLED=true
TRACE_LIB_JARS="/opt/cloud-trace-deps-1.1.0/*"
echo "Using TRACE_LIB_JARS: $TRACE_LIB_JARS"
if [ -n "$TRACE_CLOUD_TRACE_URL" ]; then
   TRACE_OPTS="-Dcloud.trace.url=$TRACE_CLOUD_TRACE_URL"
fi
if [ -n "$TRACE_CLOUD_TRACE_PROBABILITY" ]; then
   TRACE_OPTS="$TRACE_OPTS -Dcloud.trace.probability=$TRACE_CLOUD_TRACE_PROBABILITY"
fi
if [ -n "$TRACE_CLOUD_TRACE_ENABLED" ]; then
   TRACE_OPTS="$TRACE_OPTS -Dcloud.trace.enabled=$TRACE_CLOUD_TRACE_ENABLED"
fi
#echo "Using TRACE_OPTS: $TRACE_OPTS"
##########################################################


LIB_JARS=$DEPLOY_DIR/lib/*
echo "Using LIB_JARS: $LIB_JARS"
echo "Using CONF_DIR: $CONF_DIR"
echo "Using TRACE_LIB_JARS: $TRACE_LIB_JARS"

CLASSPATH=".:$CONF_DIR:$LIB_JARS:$TRACE_LIB_JARS"

EXEC_CMDLINE="${JAVA_HOME}/bin/java -classpath ${CLASSPATH} ${TRACE_OPTS} ${USER_VMARGS} ${GC_OPTS} ${JAVA_JMX_OPTS} ${JAVA_DEBUG} ${JAVA_OPTS} com.qiwenshare.file.FileApplication"

echo "Start app command line: ${EXEC_CMDLINE}" >> $STDOUT_FILE
echo "Starting $SERVER_NAME ..."

nohup ${EXEC_CMDLINE} >> $STDOUT_FILE 2>&1 &

###wait app start listener port, only wait 120s
COUNT=0
while [ $COUNT -lt 120 ]; do
    echo -e ".\c"
    sleep 1
    IS_LISTENED=`netstat -an | grep -w LISTEN | grep -w $SERVER_PORT`
    let COUNT++  
    if [ -n "$IS_LISTENED" ]; then
        COUNT=1000
    fi
done

echo "Console File: $STDOUT_FILE"
echo "--------start app $SERVER_NAME on $(uname -n) (pid=$$)--------"