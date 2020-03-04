# Example that shows you how to check your AD contents

         First check the SearchBase or BaseDN or Users DN, here you should see your users. Over port 636 as per 
         https://github.com/Zimbra-Community/ADPassword/wiki/Enable-LDAPS-on-Windows-2008-Active-Directory-Domain  (also for Windows 2016)
         LDAPTLS_REQCERT=never ldapsearch -D "CN=Administrator,CN=Users,DC=BARRYDEGRAAFF,DC=TK" -w password-here  -H ldaps://192.168.1.22:636 -b "ou=Users,dc=barrydegraaff,dc=tk"
         
         [bar@bartop ~]$ LDAPTLS_REQCERT=never ldapsearch -D "CN=Administrator,CN=Users,DC=BARRYDEGRAAFF,DC=TK" -w password-here  -H ldaps://192.168.1.22:636 -b "dc=barrydegraaff,dc=tk" | grep -i test
         # test, Users, barrydegraaff.tk
         dn: CN=test,CN=Users,DC=barrydegraaff,DC=tk
         cn: test
         givenName: test
         distinguishedName: CN=test,CN=Users,DC=barrydegraaff,DC=tk
         displayName: test
         name: test
         sAMAccountName: test
         userPrincipalName: test@barrydegraaff.tk
         
         The user `test` can change the password as the sAMAccountName == CN
         
         
         
         Now we create a user test2 that has CN <> sAMAccountName
         
         # thisistheCN, Users, barrydegraaff.tk
         dn: CN=thisistheCN,CN=Users,DC=barrydegraaff,DC=tk
         objectClass: top
         objectClass: person
         objectClass: organizationalPerson
         objectClass: user
         cn: thisistheCN
         sn: lastname
         givenName: firstname
         distinguishedName: CN=thisistheCN,CN=Users,DC=barrydegraaff,DC=tk
         instanceType: 4
         whenCreated: 20190612141748.0Z
         whenChanged: 20190612141748.0Z
         displayName: thisistheCN
         uSNCreated: 24610
         uSNChanged: 24615
         name: thisistheCN
         objectGUID:: MA6g4UTkB0iZz1HcPK2R1w==
         userAccountControl: 512
         badPwdCount: 0
         codePage: 0
         countryCode: 0
         badPasswordTime: 0
         lastLogoff: 0
         lastLogon: 0
         pwdLastSet: 132048226688681348
         primaryGroupID: 513
         objectSid:: AQUAAAAAAAUVAAAABvwsyukd4GLKMzHhUAQAAA==
         accountExpires: 9223372036854775807
         logonCount: 0
         sAMAccountName: test2
         sAMAccountType: 805306368
         userPrincipalName: test2@barrydegraaff.tk
         objectCategory: CN=Person,CN=Schema,CN=Configuration,DC=barrydegraaff,DC=tk
         dSCorePropagationData: 16010101000000.0Z
         
         
           mkdir -p /opt/zimbra/lib/ext/adpassword
           wget https://github.com/Zimbra-Community/ADPassword/releases/download/0.0.4/ADPassword.jar -O /opt/zimbra/lib/ext/adpassword/adPassword.jar 
           su zimbra
           zmprov md barrydegraaff.tk zimbraAuthLdapBindDn "%u@barrydegraaff.tk"
           zmprov md barrydegraaff.tk zimbraAuthLdapSearchBase "CN=Users,DC=BARRYDEGRAAFF,DC=TK"
           zmprov md barrydegraaff.tk zimbraAuthLdapSearchBindDn "CN=Administrator,CN=Users,DC=BARRYDEGRAAFF,DC=TK"
           zmprov md barrydegraaff.tk zimbraAuthLdapSearchBindPassword "your-password-here"
           zmprov md barrydegraaff.tk zimbraAuthLdapSearchFilter "(samaccountname=%u)"
           zmprov md barrydegraaff.tk zimbraAuthLdapURL "ldaps://WIN-M7MG5CONCRJ.barrydegraaff.tk:636"
           zmprov md barrydegraaff.tk zimbraExternalGroupLdapSearchBase "CN=Users,DC=BARRYDEGRAAFF,DC=TK"
           zmprov md barrydegraaff.tk zimbraExternalGroupLdapSearchFilter "(samaccountname=%u)"
           zmprov md barrydegraaff.tk zimbraAuthMech "ad"
           zmprov md barrydegraaff.tk zimbraAuthMechAdmin "ad"
           zmprov md barrydegraaff.tk zimbraPasswordChangeListener ADPassword
           zmprov gd barrydegraaff.tk | grep -i ldap | grep -v Gal
           zmprov gd barrydegraaff.tk | grep -i zimbraPasswordChangeListener
           zmprov md barrydegraaff.tk zimbraAuthFallbackToLocal FALSE
           zmcontrol restart
         
         Verified I can change the password from `test2` account
