# Enable LDAPS on Windows 2008 Active Directory Domain

_Are more up-to-date guide for newer Windows versions can be found at https://www.miniorange.com/guide-to-setup-ldaps-on-windows-server, while the Mini Orange guide also does some Kerberos set-up, it does show screenshots of most what is described below._

### Introduction

Zimbra can set user password stored in an Active Directory using an ldaps connection and a distinguished name with administrative privileges.
However Windows 2008 Domain Controllers don't have default LDAP over SSL (LDAPS) and so we need to activate it.
  
  
### Prerequisites

* Windows 2008 Active Directory Domain Controller
* Certification Authority role enabled (or different CA server)  
  
  
### Solution

Starting with your Certificate Authority (CA) we need to make sure that the Domain Controllers (DC's) can enroll with the CA in order to obtain the correct Certificates. There is a Certificate Template for this that exists by default.  

  * **Enable The Domain Controller Authentication Certificate Template on the Certificate Authority.**  
1.     Logon to the CA and open *Server Manager*
1.     Expand the tree till you see the Certificate Templates folder and look for the Domain Controller Authentication the default existing template
1.     Then expand the CA server and check if its listed under its Certificate Templates folder as well. If the Domain Controller Authentication is listed in both places then it exists and is enabled. If it isn't under the CA's Folder then we need to enable the Domain Controller Authentication Certificate Template
1.     Right click *Certificate Templates* under the CA, click *New*, then and click *Certificate Template to Issue*. Select the *Domain Controller Authentication* and then click *OK*  
    
  
  * **Obtain the "Domain Controller Authentication" Certificate on the Domain Controller**
1. Login into the Domain Controller you want to test the LDAP over SSL
1. Open a *command prompt* as an administrator
1. Start the Microsoft Management Console (MMC), typing *mmc*, and then press ENTER
1. Click *File*, click *Add/Remove Snap-in*, select *Certificates* from the available snap-ins, and then click *Add*
1. In the *Certificates snap-in*, click *Computer Account*, and then click *Next*
1. In the *Select Computer*, click *Local Computer*, and then click *Finish* and then *OK*
1. In the console tree, expand *Certificates - Local Computer*, expand *Personal*, and then expand *Certificates*
1. Right Click and choose *All Task*, then click *Request New Certificate*. A *Before You Begin* window will prompt you. Click *Next*
1. Select *Active Directory Enrollment Policy* and click *Next*
1. Check *Domain Controller* and *Domain Controller Authentication* and click *Next*
1. In the *Certificate Enrollment*, a status window should show the *Domain controller enrolling* and then *Status: Succeeded*. Click *Finish*  
  
  
### References
[http://www.christowles.com/2010/11/enable-ldap-over-ssl-ldaps-on-windows.html](http://www.christowles.com/2010/11/enable-ldap-over-ssl-ldaps-on-windows.html)  
[http://technet.microsoft.com/en-us/library/ee411009%28WS.10%29.aspx](http://technet.microsoft.com/en-us/library/ee411009%28WS.10%29.aspx)
