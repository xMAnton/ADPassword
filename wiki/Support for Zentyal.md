# Support for Zentyal

## This page is deprecated, the latest release jar, should support Zentyal out of the box.

Support for Zentyal is provided via an alternative binary.

The alternative binary is here:

[https://github.com/Zimbra-Community/ADPassword/releases/download/0.0.2/ADPassword-for-Zentyal.zip](https://github.com/Zimbra-Community/ADPassword/releases/download/0.0.2/ADPassword-for-Zentyal.zip)

You must unzip it before use. Then you can follow the normal README.md for installation.


# Documentation for developers

      In the file: ADPassword/src/it/iknowconsulting/adpassword/ADConnection.java
      ldapContext.modifyAttributes("cn=" + username + "," + authLdapSearchBase, mods);
      is changed to:
      ldapContext.modifyAttributes("cn=" + username + " " + username + "," + authLdapSearchBase, mods);  
      then a build is done with Netbeans IDE
