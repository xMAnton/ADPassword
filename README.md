ADPassword
======================

A Zimbra server extension to change Active Directory passwords from the Zimbra web client.


The original project by Antonio Messina (antonio.messina@icar.cnr.it) https://github.com/xMAnton/ADPassword this version is tested on Zimbra 8.8.12 and Windows 2016.

I recommend the cli install from below. If you do not want the cli install, you can also try the GUI most steps are in the video: https://www.youtube.com/watch?v=AYmsdw3tHoU

## How does it work?

1. The user clicks change password in the Zimbra UI.
2. The extension will find the user's DN in external AD/LDAP based on zimbraAuthLdapSearchFilter in zimbraAuthLdapSearchBase. 
3. The extension will not search in case you have set `External LDAP account for Authentication`. The DN from  zimbraAuthLdapExternalDn will then be used.
4. Finally the extension will change the password using bind credentials over a secure connection.

## Add the certificate from your Active Directory to the Zimbra server trust
If you use the same SSL certificate on your AD as on Zimbra there is a good change you can skip this step. If you already use your AD server for external auth, you can probably skip this as well. If you are not sure, configure your domain to auth against AD first before installing this extension. 

On 8.6:
* /opt/zimbra/j2sdk-20140721/bin/keytool -import -alias cacertclass1ca -keystore /opt/zimbra/java/jre/lib/security/cacerts -import -trustcacerts -file your-exported-cert.cer 
* default password: changeit

On 8.7:
* /opt/zimbra/common/bin/keytool -import -alias win2012 -keystore /opt/zimbra/common/etc/java/cacerts -trustcacerts -file your-exported-cert.cer
* default password: changeit

You can get any ldaps ssl certificate by using OpenSSL: openssl s_client -connect servername:port, copy paste the cert from -----BEGIN CERTIFICATE----- to -----END CERTIFICATE----- and put it in a file on your server. Then import using above commands.

## Installation via the cli

Review your LDAP configuration in the commands below and then copy-paste them:

      mkdir -p /opt/zimbra/lib/ext/adpassword
      wget https://github.com/Zimbra-Community/ADPassword/releases/download/0.0.4/ADPassword.jar -O /opt/zimbra/lib/ext/adpassword/adPassword.jar 
      su zimbra
      zmprov md domain.ext zimbraAuthLdapBindDn "%u@domain.ext"
      zmprov md domain.ext zimbraAuthLdapSearchBase "CN=Users,DC=DOMAIN,DC=EXT"
      zmprov md domain.ext zimbraAuthLdapSearchBindDn "CN=serviceAccount,CN=Users,DC=DOMAIN,DC=EXT"
      zmprov md domain.ext zimbraAuthLdapSearchBindPassword "your-password-here"
      zmprov md domain.ext zimbraAuthLdapSearchFilter "(samaccountname=%u)"
      zmprov md domain.ext zimbraAuthLdapURL "ldaps://ad-server-ip-or-dns:636"
      zmprov md domain.ext zimbraExternalGroupLdapSearchBase "CN=Users,DC=DOMAIN,DC=EXT"
      zmprov md domain.ext zimbraExternalGroupLdapSearchFilter "(samaccountname=%u)"
      zmprov md domain.ext zimbraAuthMech "ad"
      zmprov md domain.ext zimbraAuthMechAdmin "ad"
      zmprov md domain.ext zimbraPasswordChangeListener ADPassword
      zmprov gd domain.ext | grep -i ldap | grep -v Gal
      zmprov gd domain.ext | grep -i zimbraPasswordChangeListener
      zmprov md domain.ext zimbraAuthFallbackToLocal FALSE
      zmcontrol restart

* If you want a custom password complexity rules, see: https://github.com/Zimbra-Community/ADPassword/wiki/Adding-a-password-policy-check
* Sometimes when the user clicks the change password option, Zimbra goes to a URL on port 8443. To fix: `zmprov mcf zimbraChangePasswordURL https://your-zimbra-server.com/h/changepass?skin=harmony`

## Support for Zentyal

ADPassword also supports Zentyal as directory server, please check the wiki:

[https://github.com/Zimbra-Community/ADPassword/wiki/Support-for-Zentyal] (https://github.com/Zimbra-Community/ADPassword/wiki/Support-for-Zentyal)

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
