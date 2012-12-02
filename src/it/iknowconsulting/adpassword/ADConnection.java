/*
   Copyright 2012 Antonio Messina (a.messina@iknowconsulting.it)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. 
*/

// Based on ADConnection
// A Java class that encapsulates a JNDI connection to an Active Directory
// Written by Jeremy E. Mortis  mortis@ucalgary.ca  2002-07-03 
//
// References: 
// http://homepages.ucalgary.ca/~mortis/software/ADConnection.txt
// http://ldapwiki.willeke.com/wiki/Example%20-%20Active%20Directory%20Change%20Password%20JNDI

package it.iknowconsulting.adpassword;

import com.zimbra.cs.account.Domain;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

public class ADConnection {

    DirContext ldapContext;
    String authLdapSearchBase;

    public ADConnection(Domain domain) throws NamingException {
        String authLdapURL = domain.getAuthLdapURL()[0];
        String authLdapSearchBindDn = domain.getAuthLdapSearchBindDn();
        String authLdapSearchBindPassword = domain.getAuthLdapSearchBindPassword();
        authLdapSearchBase = domain.getAuthLdapSearchBase();

        Hashtable ldapEnv = new Hashtable(11);
        ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapEnv.put(Context.PROVIDER_URL, authLdapURL);
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        ldapEnv.put(Context.SECURITY_PRINCIPAL, authLdapSearchBindDn);
        ldapEnv.put(Context.SECURITY_CREDENTIALS, authLdapSearchBindPassword);
        ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
        ldapContext = new InitialDirContext(ldapEnv);
    }

    public void updatePassword(String username, String password) throws NamingException {
        String quotedPassword = "\"" + password + "\"";
        char unicodePwd[] = quotedPassword.toCharArray();
        byte pwdArray[] = new byte[unicodePwd.length * 2];
        for (int i=0; i<unicodePwd.length; i++) {
            pwdArray[i*2 + 1] = (byte) (unicodePwd[i] >>> 8);
            pwdArray[i*2 + 0] = (byte) (unicodePwd[i] & 0xff);
        }
        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("UnicodePwd", pwdArray));
        ldapContext.modifyAttributes("cn=" + username + "," + authLdapSearchBase, mods);
    }

    NamingEnumeration get(String searchFilter) throws NamingException {
        String returnedAttrs[]={"givenName","sn","name","sAMAccountName","userPrincipalName","mail","userAccountControl"};
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(returnedAttrs);
        NamingEnumeration results = ldapContext.search(authLdapSearchBase, searchFilter, searchControls);
        return results;        
    }
    
    public NamingEnumeration getUsers() throws NamingException {
        String searchFilter = "(userPrincipalName=*)";
        return get(searchFilter);
    }

    public NamingEnumeration fetchUser(String uid) throws NamingException {
        String searchFilter = "(sAMAccountName="+uid+")";
        return get(searchFilter);
    }
}