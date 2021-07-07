### Steps

* In the admin console, on the left select domains, then click the domain you wish to edit. Click the Configure GAL button and enter the following details:
    1. Enter the address of your AD server under External Server Name and set the port to 389
    2. Under Search Filter, enter (&(|(cn=*%s*)(sn=*%s*)))
    3. Under Autocomplete Filter, enter (|(uid=%s*)(givenname=%s*)(mail=%s*))
    4. Under LDAP Search base, enter dc=example,dc=com
    5. Click Next
* Place a tick in the box Use DN/Password to bind to external server and enter the same credentials used in authentication
* By default, there will be a tick alongside Use GAL search settings for GAL sync, leave this as is and click Next
* Enter the username or other name for your test user, and click the Test button
* If your search is successful, click Finish

Your AD GAL is now implemented.

You can verify the Global Address list by creating a new email and clicking the To: button which will allow you to search for your AD user accounts
