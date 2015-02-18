#!/bin/bash
function resetSEQ() {
  local sql="SELECT  'SELECT SETVAL(' ||quote_literal(quote_ident(S.relname))|| ', MAX(' ||quote_ident(C.attname)|| ') ) FROM ' ||quote_ident(T.relname)|| ';' FROM pg_class AS S, pg_depend AS D, pg_class AS T, pg_attribute AS C WHERE S.relkind = 'S' AND S.oid = D.objid AND D.refobjid = T.oid AND D.refobjid = C.attrelid AND D.refobjsubid = C.attnum ORDER BY S.relname;"
  local tempsql="/var/tmp/temp.sql"
  local temp="/var/tmp/temp.tmp"

  echo "--reseting sequences $dbName"
  sudo su postgres -c "psql -d $dbName -c \"$sql\" > $temp"
  sudo bash -c "echo '' > $tempsql"
  while read -r line; do
    if [[ $line == SELECT* ]]; then
      sudo bash -c "echo \"$line\" >> $tempsql"
    fi
  done < $temp
  [ -f $tempsql ] && sudo su postgres -c "psql -d $dbName -f $tempsql"
}

function initFlyway() {
  echo "--flywaying $dbName..."
  mvn flyway:init -DdbName=$dbName
  resetSEQ
}

function copyEnvDeployResources() {
  echo "--copying $appDeployDir/app.license => $appDir/tomcat/webapps/ROOT/WEB-INF"
  sudo cp $appDeployDir/app.license $appDir/tomcat/webapps/ROOT/WEB-INF

  echo "--copying company logo..."
  cp $appDeployDir/{*.png,*.gif,*.jpg} $appTargetDir/images/company_logo.gif
}

function deployAndStopTomcat() {
  echo "deploying to tomcat server"

  echo "--stopping tomcat..."
  sudo bash $appDir/tomcat/bin/shutdown.sh

  cd $workingDir

  #sudo su postgres -c "psql -d $dbname -f $sqlFolder/init/init-db.4.8.sql"
  local existCode=`sudo su postgres -c "psql -l" | grep $dbName | wc -l`
  echo "--checking database = $existCode"
  if [[ "$existCode" -ne "1" ]]; then
    echo "--creating $dbName..."
    sudo su postgres -c "createdb $dbName"
    echo "--initializing $dbName by $workingSqlDir/init-db.5.0.sql..."
    sudo su postgres -c "psql -d $dbName -f $workingSqlDir/init-db.5.0.sql"
    initFlyway
  fi

  sudo su postgres -c "psql -d $dbName -c 'select * from schema_version;'" || initFlyway

  echo "--compiling source code in $workingDir..."
  mvn clean install -DskipTests -DdbName=$dbName

  echo "--reset sequences.."
  resetSEQ

  echo "--remove target $appTargetDir"
  sudo rm -Rf $appTargetDir/

  echo "--removing link ROOT"
  sudo rm $appDir/tomcat/webapps/ROOT

  echo "--copying $workingDir/target/hiringboss code to $appTargetDir..."
  sudo cp -R $workingDir/target/hiringboss $appTargetDir

  echo "--creating link $appDir/tomcat/webapps/ROOT to $appTargetDir"
  sudo ln -s $appTargetDir $appDir/tomcat/webapps/ROOT

  copyEnvDeployResources

  echo "--change owner to $owner:$owner of $appDir"
  sudo chown -R $owner:$owner $appDir

  echo "--staring tomcat..."
  sudo bash $appDir/tomcat/bin/startup.sh
}

function updateSource(){
  local credential="--username $svnUsername --password $svnPassword --no-auth-cache"

  cd "$rootSourceDir"
  echo "updating source from svn url $svnURL => $rootSourceDir"
  if [ ! -d ".svn" ]; then
    sudo svn co $credential --non-recursive $svnURL .
  fi

  for name in $svnDirs; do
    if [[ $name == */* ]]; then
      local subname=""
      for n in ${name//// }; do
        if [[ ! "$subname$n" == "$name" ]]; then
          subname="$subname$n/"
          sudo svn up $credential --non-recursive "$subname"
        fi
      done
    fi
  done

  sudo svn up $credential $svnDirs $rootSourceDir
}

function updateDB() {
  echo "--update DB schema $dbName"

  #local hbversion=""
  #[ -f $changelogFile ] && hbversion=`getProperty $changelogFile "hiringboss.version"` || files=`find $workingSqlDir -type f -name *.sql | sort`

  local files=`find $workingSqlDir -type f -name *.sql | sort`
  for file in $files; do
    echo "----running sql file $file"
    sudo su postgres -c "psql -d $dbName -f $file"
  done

  #[ -f $changelogFile ] || hbversion=$configHbversion
}

#function logCompletedTasks() {
#  sudo bash -c "echo 'hiringboss.version=$hbversion' > $changelogFile"
#}

function initializeDatabase() {
  #[ -z "$hiringboss" ] && cropAndRecreateDB
  [ -f $changelogFile ] || cropAndRecreateDB
  updateDB
}

function cropAndRecreateDB() {
  echo "Drop and create DB=$dbName"

  echo "--dropping $dbName..."
  sudo su postgres -c "dropdb $dbName"

  echo "--creating $dbName..."
  sudo su postgres -c "createdb $dbName"

  sudo su postgres -c "psql -d $dbName -f $workingSqlDir/init-db.5.0.sql"
}

function installPackages() {
  sudo apt-get install postgresql-client-9.1
}

function getProperty() {
  local file=$1
  local key=$2
  local value=`cat $file | grep ${key} | cut -d'=' -f2`
  echo "$value" | tr -d '\r'
  #echo "$value" | dos2unix -ascii
}

function backupPreviousCode() {
  echo "Backup previous tasks"
  sudo chmod -R 777 $appBackupDir
  echo "--copying $appTargetDir => $appBackupDir..."
  sudo cp -R $appTargetDir $appBackupDir/
  sudo cp -R $appDeployDir $appBackupDir/
}

function backupDB() {
  local suffix=$1
  echo "Backup database $dbName"
  sudo chmod -R 777 $appBackupDir
  local dumpFile="$appBackupDir/$dbName-$suffix.sql"
  echo "--pg_dump $dbName => $dumpFile..."
  sudo su postgres -c "pg_dump $dbName -f $dumpFile"
}

function generateSetEnvSh() {
  echo "--generating setenv.sh"
  sudo rm $appDir/tomcat/bin/setenv.sh

  sudo cp $bashSourceDir/setenv.sh $appDir/tomcat/bin/setenv.sh
  sudo bash -c "echo '' >> $appDir/tomcat/bin/setenv.sh"
  sudo bash -c "echo '  JAVA_OPTS=\"-Djava.awt.headless=true -Xms512M -Xmx1024M -Dhiringboss.properties=file:$appDeployDir/custom.properties\"' >> $appDir/tomcat/bin/setenv.sh"
  sudo bash -c "echo '' >> $appDir/tomcat/bin/setenv.sh"
  sudo bash -c "echo 'fi' >> $appDir/tomcat/bin/setenv.sh"
}

function makeDirectories() {
  echo "Make directory"
  for dir in $1; do
    echo "--$dir"
    sudo mkdir $dir
    sudo chown -R ubuntu:ubuntu $dir
  done

  echo "--creating tomcat sever at port: $tomcatPort and control-port: $tomcatControlPort..."
  sudo tomcat6-instance-create -p $tomcatPort -c $tomcatControlPort $appDir/tomcat && generateSetEnvSh

  #generateSetEnvSh

  if [ ! -f $appDeployDir/$hbcustomPropertiesFilename ]; then
    echo "--generating $appDeployDir/$hbcustomPropertiesFilename"
    #[ -f $appDeployDir/$hbcustomPropertiesFilename ] || sudo bash -c "echo 'jdbc.url=jdbc:postgresql://localhost/$dbName' > $appDeployDir/$hbcustomPropertiesFilename"
    sudo bash -c "echo 'jdbc.url=jdbc:postgresql://localhost/$dbName' > $appDeployDir/$hbcustomPropertiesFilename"
  fi

  echo "--change owner to $owner:$owner of $appDir"
  sudo chown -R $owner:$owner $appDir

}

function loadPropertiesFile()  {
  echo "Loading properties from file $deployConfigFile"
  dbName=`getProperty $deployConfigFile "db.name"`
  tomcatPort=`getProperty $deployConfigFile "tomcat.port"`
  tomcatControlPort=`getProperty $deployConfigFile "tomcat.control.port"`
  owner=`getProperty $deployConfigFile "os.user"`
  hbcustomPropertiesFilename=`getProperty $deployConfigFile "custom.hiringboss.properties.filename"`
  svnUsername=`getProperty $deployConfigFile "svn.username"`
  svnPassword=`getProperty $deployConfigFile "svn.password"`
  svnURL=`getProperty $deployConfigFile "svn.url"`
  svnDirs=`getProperty $deployConfigFile "svn.name.uris"`
  rootSourceDir=`getProperty $deployConfigFile "source.root.dir"`
  workingDir=$rootSourceDir/`getProperty $deployConfigFile "source.working.dir.uri"`
  workingSqlDir=$workingDir/`getProperty $deployConfigFile "source.working.sql.dir.uri"`
  appsDir="/mnt/apps"
  appDir="$appsDir/$dbName"
  appDeployDir="$appDir/env.deploy"
  appTargetDir="$appDir/target"
  appBackupDir="$appDir/backup/$now"
}

function readPropertiesFromEnv() {
  echo "Under construction"
}

#####################################################################################################################
################################################### MAIN FUNCTION ###################################################
#####################################################################################################################

start_time=`date +%s`
deployConfigFile=$1
#"deploy-config.properties"
now=`date +"%Y%m%d"`-`date +"%H%M"`
bashSourceDir=`dirname $BASH_SOURCE`

[ -f $deployConfigFile ] && loadPropertiesFile || readPropertiesFromEnv
makeDirectories "$appsDir $appDir $appTargetDir $appDeployDir $appDir/backup $appBackupDir $rootSourceDir"
backupPreviousCode
backupDB `date +%s%N | cut -b1-13`
updateSource
deployAndStopTomcat "$workingDir" "$appTargetDir" "$appDeployDir" "$appDir"
backupDB "`date +%s%N | cut -b1-13`"

sudo chown -R ubuntu:ubuntu $appDir

echo "Run time is $(expr `date +%s` - $start_time) s"

#main > $dbName.log

