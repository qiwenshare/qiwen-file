set settingDir=file-common/src/main/resources/conf/settings.xml

call mvn install:install-file -s %settingDir% -DgroupId=com.aspose -DartifactId=aspose-words -Dversion=16.8.0 -Dpackaging=jar -Dfile=file-office/src/main/resources/lib/aspose-words-16.8.0-jdk16.jar
call mvn install:install-file -s %settingDir% -DgroupId=com.aspose -DartifactId=aspose-slides -Dversion=16.7.0 -Dpackaging=jar -Dfile=file-office/src/main/resources/lib/aspose.slides-16.7.0.jar
call mvn install:install-file -s %settingDir% -DgroupId=com.aspose -DartifactId=aspose-cells -Dversion=9.0.0 -Dpackaging=jar -Dfile=file-office/src/main/resources/lib/aspose-cells-9.0.0.jar
mvn install -s %settingDir%
pause