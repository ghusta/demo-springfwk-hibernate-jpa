FROM tomcat:9.0-jre17

COPY target/*.war $CATALINA_HOME/webapps/ROOT.war
