# SIRS_Project_A33
A33 Smart Home System Security project by some awesome guys

###Web Application Module:
- [Install apache server](https://tomcat.apache.org/download-90.cgi) 
- I got this one: 32-bit/64-bit Windows Service Installer
- After install add it to Intellij: Settings > Application Servers (choose path where installed)
- Run > Edit configurations > (+)Add new configuration > Tomcat Server > Local
- Fix artifacts: In the same window go to Deployment > (+)Press Add > Artifact... 
- In Server window in On "Update" action > Update classes and resources

###Authentication:
- Check authentication servlet class: <b>AuthServlet</b>
- Configure\Copy <b>tomcat-users.xml</b> in: <br />
C:\Program Files\Apache Software Foundation\Tomcat 9.0\tomcat-users.xml
- Configure <b>web.xml</b> to invoke servlet (Already in project, no need to copy in tomcat path)
- Run the server: You will be prompted for user and password (use one in <b>tomcat-users.xml</b>)

###SSL/TLS with Self-Signed Certificate:
- Generated a self-sign certificate using java keytool (generated <b>.keystore</b>)
- Copy <b>.keystore</b> in your user home path: <br />
C:\Users\YourUserName
- Configure\Copy <b>server.xml</b> in: <br />
C:\Program Files\Apache Software Foundation\Tomcat 9.0\server.xml
- Configure the server before running: <br />
- Run > Edit configurations > Tomcat Server > Tomcat Server > Server: <br />
Open browser: https://localhost:8443/ <br />
HTTPs port: 8443
- Run the server: Because it is a self-signed certificate it will say that is not secure. 
Problem will be solved if the certificate is signed by a certified authority.