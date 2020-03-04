Welcome to the ADPassword wiki!

To run an ldap search from your Zimbra machine to your AD do something like this:

      ldapsearch -x -H ldaps://192.168.1.19:636 -D 'CN=Administrator,CN=Users,DC=barrydegraaff,DC=tk' -w 'password here' -s sub -b "CN=Users,DC=barrydegraaff,DC=tk"

      and add this for testing purpose:
      CentOS: nano /etc/openldap/ldap.conf
      Ubuntu: nano /etc/ldap/ldap.conf
      TLS_REQCERT NEVER
