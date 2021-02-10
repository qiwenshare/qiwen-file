#!/bin/bash
. /etc/profile
. ~/.bashrc
. ~/.bash_profile
#support jsch commons

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`

echo "========stop app ${DEPLOY_DIR}========"
PARENT_DIR=$(dirname "$PWD")
CONF_DIR=$DEPLOY_DIR/conf
LIB_DIR=$DEPLOY_DIR/lib
SERVER_NAME=`cat $CONF_DIR/config/application.yml | grep -w "name:" | grep -v "#" | awk  'NR==1{print $2}' | tr -d '\r'`
SERVER_PORT=`cat $CONF_DIR/config/application.yml | grep -w "port:" | grep -v "#" | awk  'NR==1{print $2}' | tr -d '\r'`

#REM **********************************************************************************************
LOG_PATH="/export/log/qiwen-file"
if [ "${LOG_PATH}" == "" ] ; then
    LOG_PATH=$PARENT_DIR/logs/${SERVER_NAME}
fi

STDOUT_FILE=$LOG_PATH/nohup.out

if [ -z "$SERVER_NAME" ]; then
    SERVER_NAME=`hostname`
fi

PIDS=`ps -ef | grep java | grep "$CONF_DIR" |awk '{print $2}'`
if [ -z "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME does not started!"
    exit 1
fi
#REM **********************************************************************************************

echo  "Stopping the $SERVER_NAME ..."
echo "Stopping the $SERVER_NAME" >> $STDOUT_FILE
date>>$STDOUT_FILE
id>>$STDOUT_FILE
echo $SERVER_NAME shutdown , pids $PIDS  >> $STDOUT_FILE

for PID in $PIDS ; do
    kill $PID
done

COUNT=0
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1
    COUNT=1
    for PID in $PIDS ; do
        PID_EXIST=`ps -f -p $PID | grep java`
        if [ -n "$PID_EXIST" ]; then
            COUNT=0
            break
        fi
    done
done

echo "PID: $PIDS  has bean kill" >> $STDOUT_FILE
echo "========stop app $SERVER_NAME shutdown========"