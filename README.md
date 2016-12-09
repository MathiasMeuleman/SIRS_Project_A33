# SIRS_Project_A33
##A33 Smart Home System Security Project <br />
## Institute Superior Tecnico
## Network and Computer Security
### Students:
- <b>85720 - Robert-Octavian Popescu</b>
- <b>85884 - Torben Lammers</b>
- <b>85885 - Mathias Meuleman</b>

###Acronyms:
- SHS - Smart Home System
- SHD - Smart Home Device
- GW - Gateway (contains a web server)

###Platform and tools used:
- Java 8
- Intellij IDEA Ultimate Edition
- Apache Tomcat 9
- Oracle Virtual Box
- Windows
- CentOS
- fwbuilder

###Security components (src/pt.ulisboa.ist.sirs.project.securesmarthome/): Check + Explain better ! ! !
- <b>AESSecretKeyFactory</b> - provides AES-128b key
- <b>Cryptography</b> - encrypt/decrypt with AES-128b in CBC/ECB mode
- <b>DHKeyAgreement</b> - Diffie-Hellman key exchange algorithm
- <b>SecurityManager</b> - check timestamp for freshness 
- <b>GatewaySecurity</b> - authenticate SHD and send IV (encrypted with AES-128b in ECB mode) for CBC encrypt/decrypt
- <b>SHDSecurity</b> - authenticate GW
- <b>Web Application + Authentication + SSL\TLS with Self-signed Certificate</b> (in webserver/)

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
Set -> Program arguments: lightBulb <br />
Rename it -> Name: SHD

###TEST GW <-> SHD in Intellij IDEA (having done the above setup for both the GW and SHD):
- Run GW application (this will wait for a client - SHD to connect to it)
- Run SHD application (will connect to the socket open by the gateway) as a light bulb
(configure arguments properly)
- The key agreement and authentication should be done before data is transmitted over the channel

###TEST GW <-> SHD using the jars (CMD - Windows machine):
- Run GW.jar with apriori key of the light bulb: <br />
> java -jar GW.jar ABCDEFGHIJKLMNOP
- Run SHD.jar simulating a light bulb: <br />
> java -jar SHD.jar lightBulb

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
-> User inputs and adds on web interface SHD's name (lightBulb) and apriori key ('ABCDEFGHIJKLMNOP') <br />
-> JavaScript will invoke a method from the Gateway jar with SHD's apriori key <br />
-> JavaScript will invoke a method from the SHD jar with SHD's name <br />
-> The incoming data from SHD to the GW will be displayed on the web interface
-> User can interact with the SHD through the GW

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

###Conclusion:
The security components developed in this project cover several aspects of the course. Some components specified in 
the proposal (e.g. NIDS) may not have been implemented. Additionally, some of the components have been implemented 
and tested on virtual machines in Oracle Virtual Box, but not included in this submission.