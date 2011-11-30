cd parentpom
call setenv.bat
call mvn clean
rem Add the parameter -Dmaven.test.skip=true or -DskipTests=true in the command line, depending on whether you want to skip test compilation and execution or only execution
rem http://maven.apache.org/plugins/maven-surefire-plugin/examples/skipping-test.html
call mvn package -Pweb,delivery -DskipTests=true -Dmaven.test.skip=true
cd ..
