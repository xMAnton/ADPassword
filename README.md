ADPassword
======================

A Zimbra server extension to change Active Directory passwords from the Zimbra web client.


The original project by Antonio Messina (a.messina@iknowconsulting.it) https://github.com/xMAnton/ADPassword currently tested on Zimbra 8.6 and Windows 2012 R2 Active Directory.

The below steps are also in a how-to video: https://www.youtube.com/watch?v=AYmsdw3tHoU

## Installation

- As root, `mkdir -p /opt/zimbra/lib/ext/adpassword`
- As root, `wget https://github.com/Zimbra-Community/ADPassword/releases/download/0.0.1/ADPassword.jar -O /opt/zimbra/lib/ext/adpassword/adPassword.jar`

## Add the certificate from your Active Directory to the Zimbra server trust
* /opt/zimbra/j2sdk-20140721/bin/keytool -import -alias cacertclass1ca -keystore /opt/zimbra/java/jre/lib/security/cacerts -import -trustcacerts -file your-exported-cert.cer 
* default password: changeit
* keytool binary may be on a different location if you are not running 8.6
* zmcontrol restart (after adding the certificate)

## Configure authentication settings for your domain

- Open the Zimbra Administration console
- Select External LDAP as authentication mechanism
- Type the LDAP URL and check Use SSL on port 636 (your certificate must be trusted, see below)
- Type `(samaccountname=%u)` in the LDAP filter field
- Specify `CN=Users, DC=DOMAIN, DC=EXT` in the LDAP search base field
- Check "Use DN/Password to bind to external server"
- Enter the Bind DN `CN=Administrator,CN=Users, DC=DOMAIN,DC=EXT` and its password
- If Test passed succesfully, click Finish
- Assign the new External change password listener: `ADPassword`
- From the cli run as Zimbra user:

         zmprov md domain.ext zimbraAuthLdapSearchBindDn "CN=Administrator,CN=Users, DC=DOMAIN,DC=EXT"
         zmprov md domain.ext zimbraAuthLdapSearchBindPassword "admin-password-here"
         zmprov md domain.ext zimbraAuthLdapBindDn "%u@domain.ext"
         zmprov md domain.ext zimbraAuthLdapURL "ldaps://ad-server-ip-or-dnsname:636" 
         zmprov md domain.ext zimbraAuthLdapSearchBase "CN=Users, DC=DOMAIN, DC=EXT"
         zmprov md domain.ext zimbraAuthLdapSearchFilter "(samaccountname=%u)"
         zmprov md domain.ext zimbraExternalGroupLdapSearchBase "CN=Users, DC=DOMAIN, DC=EXT"
         zmprov md domain.ext zimbraExternalGroupLdapSearchFilter "(samaccountname=%u)"
         zmprov md domain.ext zimbraPasswordChangeListener ADPassword
         zmcontrol restart


* This extension may require you to open port 8443
* Maybe you also want to `zmprov md domain.ext zimbraAuthFallbackToLocal FALSE` and disable locally set passwords.

## Debugging
Do a password change while you run the following command:

     tail -f /opt/zimbra/log/mailbox.log

Verify your configuration:     

     zmprov gd domain.ext | grep -i ldap | grep -v Gal

Example issues:

     Wrong bind DN:
     LDAP: error code 34 - 0000208F: NameErr: DSID-03100225, problem 2006 (BAD_NAME)
     
     Forgot to set zimbraAuthLdapSearchFilter or other required attribute:
     A network service error has occurred
     system failure: java.lang.NullPointerException

## License
* originally Copyright 2012 Antonio Messina (a.messina@iknowconsulting.it)
* packaging, fixes and adjustments for ZCS 8.5/8.6 Copyright 2016 VNC AG

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
