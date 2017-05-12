#! /bin/sh

WorkingPath=`pwd`
echo "=====InteLAB Backend Setup Script====="

echo "Current Environment ${INTELAB_ENV}"
echo "[INFO] Pull Latest Code"
git pull

echo "[INFO] Check configs"
ConfigsFolderName=../intelab-configs/
if [ -d "$ConfigsFolderName" ]; then
  cd $ConfigsFolderName
  git pull
else
  echo "[INFO] configs not exist, clone from server"
  cd ..
  git clone git@github.com:ilabservice/intelab-configs.git
  OUT=$?
  if [ $OUT -eq 0 ]; then
    echo "[INFO] clone finish"
  else
    echo "[ERROR] clone failed, exit"
    exit -1
  fi
fi
cd $WorkingPath

LogConfigureFileName=../intelab-configs/${INTELAB_ENV}/log4j2.xml
if [ -e "$LogConfigureFileName" ]; then
  echo "[INFO] Copy Log Configuration File"
  cp $LogConfigureFileName ./src/main/resources/log4j2.xml
else
  echo "[ERROR] Log Configuration File ${LogConfigureFileName} not exist, exit"
  exit -1
fi

Hostname=`hostname --fqdn`
sed -i "s/NEEDTOREPLACEWITHHOSTNAME/${Hostname}/g" ./src/main/resources/log4j2.xml
echo "[INFO] init log configuration"

rm -rf ./target
mvn package
OUT=$?
if [ $OUT -eq 0 ]; then
  echo "[INFO] compile project finish"
else
  echo "[ERROR] compile failed, exit"
  exit -1
fi

nohup java -jar ./target/com.device.inspect-1.0-SNAPSHOT.jar --spring.config.location=../intelab-configs/${INTELAB_ENV}/application.properties 1 > /dev/null 2>&1 &
echo "[INFO] finish"
