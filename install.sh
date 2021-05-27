#/*************************************************
#*  install.sh write by echo at Changsha. Hunan, 2021年 05月 24日 星期一 11:33:25 CST
#*************************************************/
#!/bin/sh
function echo_dbg_p(){
  echo "echo_dbg, $@"
}
function usage(){
echo -e "usages: $0 [H|h|help] [-h] [-s]
  [H|h|help]: check the usages\n
  []"
}

#main
#maven install check
cmd_package=yum
if ! mvn -v >/dev/null;then
  sudo $cmd_package install -y maven
fi
#java install check
if ! java -version &>/dev/null;then 
  sudo $cmd_package install -y java
fi
if ! mysql -V>/dev/null;then 
  sudo wget https://dev.mysql.com/get/mysql57-community-release-el7-9.noarch.rpm;
  sudo rpm -ivh mysql57-community-release-el7-9.noarch.rpm
  sudo yum install -y mysql-server
fi
#build path check
#build_root_path=./
settingDir=file-common/src/main/resources/conf/settings.xml

mvn clean install -s $settingDir
sed -i "s#D:/temp_db#/tmp/#g" release/conf/config/application-dev.properties
echo_dbg_p "warning, PLS create mysql with name file, and set the password follow the file qiwen-file/file-web/src/main/resources/config/application-prod.properties"

case $1 in
  H|h|help)
    usage
    ;;
  *)
# getopts :s:h表示这个命令接受2个带参数选项，分别是-h和-s
    while getopts :s:h opt
    do  
      case $opt in
        s)
          echo "-s=$OPTARG"
          ;;
        :)
          echo "-$OPTARG needs an argument"
          ;;
        h)
          echo "-h is set"
          ;;
        *)
          echo "-$opt not recognized"
          ;;
      esac
    done
    ;;
esac
