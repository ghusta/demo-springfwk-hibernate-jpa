FROM tomcat:10.1-jre17

COPY target/*.war $CATALINA_HOME/webapps/ROOT.war
