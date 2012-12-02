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

package it.iknowconsulting.adpassword;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ldap.ChangePasswordListener;
import java.security.Security;
import java.util.Map;
import javax.naming.NamingException;

public class ADChangePasswordListener extends ChangePasswordListener {
            
    @Override
    public void preModify(Account acct, String newPassword, Map context, Map<String, Object> attrsToModify) throws ServiceException {
        try {
            Provisioning prov = Provisioning.getInstance();
            Domain domain = prov.getDomain(acct);
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            // the keystore that holds trusted root certificates
            System.setProperty("javax.net.ssl.trustStore", "/opt/zimbra/java/jre/lib/security/cacerts");
            System.setProperty("javax.net.debug", "all");
            ADConnection adc = new ADConnection(domain);
            adc.updatePassword(acct.getDisplayName(), newPassword);
        } catch (NamingException ex) {
            throw AccountServiceException.PERM_DENIED(ex.toString());
        }
    }
            
    @Override
    public void postModify(Account acct, String newPassword, Map context) {
        // do nothing
    }
}