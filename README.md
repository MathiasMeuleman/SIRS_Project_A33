# SIRS_Project_A33
##A33 Smart Home System Security Project <br />
###Acronyms:
- SHS - Smart Home System
- SHD - Smart Home Device
- GW - Gateway (contains a web server)

###Platform and tools used:
- Intellij IDEA Ultimate Edition


###Security components (src/pt.ulisboa.ist.sirs.project.securesmarthome/):
- <b>AESSecretKeyFactory</b> - provides AES-128b key
- <b>Cryptography</b> - encrypt/decrypt with AES-128b in CBC/ECB mode
- <b>DHKeyAgreement</b> - Diffie-Hellman key exchange algorithm
- <b>SecurityManager</b> - check timestamp for freshness 
- <b>GatewaySecurity</b> - authenticate SHD and send IV (encrypted with AES-128b in ECB mode) for CBC encrypt/decrypt
- <b>SHDSecurity</b> - authenticate GW 

###GW (src/pt.ulisboa.ist.sirs.project.securesmarthome.gateway/):
- <b>GW</b> will act as <b>server</b>, which will open a socket and wait for a connection
- Setup: Run > Edit configurations > (+)Add new configuration > Application > Main class: <br />
pt.ulisboa.ist.sirs.project.securesmarthome.gateway.GatewayMain <br />
Set -> Program arguments: ABCDEFGHIJKLMNOP <br />
Rename it -> Name: GW

###SHD (src/pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice/):
- <b>SHD</b> will act as <b>client</b>, which will connect to the GW socket start communication
- Setup: Run > Edit configurations > (+)Add new configuration > Application > Main class: <br />
pt.ulisboa.ist.sirs.project.securesmarthome.gateway.SHDMain <br />
Set -> Program arguments: temperatureSensor <br />
Rename it -> Name: SHD

###TEST GW <-> SHD in Intellij IDEA (having done the above setup for both the GW and SHD):
- Run GW application (this will wait for a client - SHD to connect to it)
- Run SHD application (will connect to the socket open by the gateway)
- The key agreement and authentication should be done before data is transmitted over the channel

###Web Application Module (webserver/):
- [Install apache server](https://tomcat.apache.org/download-90.cgi) 
- I got this one: 32-bit/64-bit Windows Service Installer
- After install add it to Intellij: Settings > Application Servers (choose path where installed)
- Run > Edit configurations > (+)Add new configuration > Tomcat Server > Local
- Fix artifacts: In the same window go to Deployment > (+)Press Add > Artifact... 
- In Server window in On "Update" action > Update classes and resources

###TEST User <-> GW <-> SHD:
- At the moment this feature is not implemented (time consuming to implement the connection)
- Main idea of the implementation would be that: <br />
-> User inputs on web interface SHD's name (temperatureSensor) and apriori key ('ABCDEFGHIJKLMNOP') <br />
-> JavaScript will invoke a method from the Gateway jar with SHD's apriori key <br />
-> JavaSript will invoke a method from the SHD jar with SHD's name <br />
-> The incoming data from SHD to the GW will be displayed on the web interface

###Authentication on Web Application:
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
Run > Edit configurations > Tomcat Server > Tomcat Server > Server: <br />
-> Open browser: https://localhost:8443/ <br />
-> HTTPs port: 8443
- Run the server: Because it is a self-signed certificate it will say that is not secure. 
Problem will be solved if the certificate is signed by a certified authority.


###Firewall (firewall/):
- <b>group33-firewall.fwb</b> has been created using <b>fwbuilder</b> in CentOS.
- Contains rules for resolving <b>DoS attacks</b> such as: TCP SYN flooding, TCP "Christmass Tree" packets, IP Fragments packets.
- Moreover, it has rules regarding remote access to the <b>GW</b> and <b>SHDs</b>.
- The firewall is intended to be installed on the router in our <b>SHS</b>, meaning that it has not been tested.