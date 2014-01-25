call setenv.bat
rem mvn org.apache.maven.plugins:maven-eclipse-plugin:2.8:eclipse  -DdownloadSources=true -DdownloadJavadocs=true
rem mvn install eclipse:eclipse
mvn eclipse:clean eclipse:eclipse