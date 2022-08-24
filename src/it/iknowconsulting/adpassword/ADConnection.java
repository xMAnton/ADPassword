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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class ADConnection {

    DirContext ldapContext;
    String authLdapSearchBase;

    //authLdapSearchFilter see readme: zmprov md domain.ext zimbraAuthLdapSearchFilter "(samaccountname=%u)"
    String authLdapSearchFilter;

    public ADConnection(Domain domain) throws NamingException {
        String authLdapURL = domain.getAuthLdapURL()[0];
        String authLdapSearchBindDn = domain.getAuthLdapSearchBindDn();
        String authLdapSearchBindPassword = domain.getAuthLdapSearchBindPassword();
        authLdapSearchBase = domain.getAuthLdapSearchBase();
        authLdapSearchFilter = domain.getAuthLdapSearchFilter();

        Hashtable ldapEnv = new Hashtable(11);
        ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapEnv.put(Context.PROVIDER_URL, authLdapURL);
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        ldapEnv.put(Context.SECURITY_PRINCIPAL, authLdapSearchBindDn);
        ldapEnv.put(Context.SECURITY_CREDENTIALS, authLdapSearchBindPassword);
        ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
        ldapContext = new InitialDirContext(ldapEnv);
    }

    public void updatePassword(Account acct, String password) throws NamingException, ServiceException {
        String username = acct.getUid();
        String userTest;
        String quotedPassword = "\"" + password + "\"";
        char unicodePwd[] = quotedPassword.toCharArray();
        byte pwdArray[] = new byte[unicodePwd.length * 2];
        for (int i=0; i<unicodePwd.length; i++) {
            pwdArray[i*2 + 1] = (byte) (unicodePwd[i] >>> 8);
            pwdArray[i*2 + 0] = (byte) (unicodePwd[i] & 0xff);
        }
        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("UnicodePwd", pwdArray));

        //if ExternalDN is set for the user in Zimbra, use that, otherwise fetch the DN
        if ( (acct.getAuthLdapExternalDn() != null) && (!acct.getAuthLdapExternalDn().isEmpty()))
        {
            ldapContext.modifyAttributes(acct.getAuthLdapExternalDn(), mods);
        }
        else
        {
            System.out.println("ADPassword->ADConnection->updatePassword->username: "+ username);
            userTest = fetchUser(username);
            if (userTest == null) {
                Provisioning prov = Provisioning.getInstance();
                Domain domain = prov.getDomain(acct);
                if (!domain.isAuthFallbackToLocal()) {
                    throw AccountServiceException.PERM_DENIED("User not found while updating password to AD! Please check your connection settings");
                } else {
                    System.out.println("ADPassword->ADConnection->updatePassword->fetchUser: "+ username+" not found in AD, skipping");
                    return;
                }
            }
            System.out.println("ADPassword->ADConnection->updatePassword->fetchUser(username): "+ userTest);
            System.out.println("ADPassword->ADConnection->updatePassword->mods: "+ mods);
            ldapContext.modifyAttributes(userTest, mods);
        }
    }

    String fetchUser(String username) throws NamingException {
        String returnedAttrs[]={"dn"};
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(returnedAttrs);
        String searchFilter = authLdapSearchFilter.replace("%u",username);
        System.out.println("ADPassword->ADConnection->fetchUser->searchFilter: "+ searchFilter);
        NamingEnumeration results = ldapContext.search(authLdapSearchBase, searchFilter, searchControls);

        if (!results.hasMore()) {
            return null;
        }
        SearchResult sr = (SearchResult) results.next();
        System.out.println("ADPassword->ADConnection->fetchUser->getNameInNamespace: "+ sr.getNameInNamespace());
        return sr.getNameInNamespace();
    }
}
