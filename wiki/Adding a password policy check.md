# Adding a password policy check

By default Zimbra does NOT enforce a password policy when configured to use External Authentication such as Active Directory.

This means that Zimbra will allow a user to choose a password, that is NOT allowed in the policy of the active directory server. The request with the violating password is sent to the AD server, that will return an LDAP error and Zimbra will show the user `A network service error has occurred.` message.


As this is not very intuitive, one can patch the `changepass` jsp file to include some form validation, and prevent the most common errors users make when choosing a password. So this patch is for user convenience not security. The patch will hide the `Change password` button as long as the password does not meet the complexity requirement.

Open the file:

    /opt/zimbra/jetty/webapps/zimbra/h/changepass

The below patch will verify the password is at least 8 characters long, and contains at least one lower, one upper, one number and one of `-_`. Change the code to suit your requirement. 

      diff -Naur changepass-old changepass
      --- changepass-old	2014-12-15 22:08:48.000000000 +0100
      +++ changepass	2017-02-27 15:24:39.739918001 +0100
      @@ -71,6 +71,83 @@
           <link rel="stylesheet" type="text/css" href="${cssurl}">
           <fmt:message key="favIconUrl" var="favIconUrl"/>
           <link rel="SHORTCUT ICON" href="<c:url value='${favIconUrl}'/>">
      +   <script type="text/javascript">
      +   function validateForm() {
      +      var password = document.getElementById('confirm').value;
      +      var newPassword = document.getElementById('newPassword').value;
      +      
      +      if (newPassword.length < 1)
      +      {
      +         document.getElementById('messageDiv').innerHTML = 'Submit button appears after you<br> have chosen a correct password.';
      +         document.getElementById('submitBtn').style.display = "none";
      +         document.getElementById('confirm').style.display = "none";
      +         return;
      +      }
      +
      +      if (newPassword.length < 10)
      +      {
      +       	 document.getElementById('messageDiv').innerHTML = 'Minimum length 10 characters';
      +         document.getElementById('submitBtn').style.display = "none";
      +         document.getElementById('confirm').style.display = "none";
      +         return;
      +      }
      +      
      +      var regexp = /^[a-zA-Z0-9-_]+$/;
      +      if (newPassword.search(regexp) == -1)
      +      {
      +         document.getElementById('messageDiv').innerHTML = 'Invalid character in password';
      +         document.getElementById('submitBtn').style.display = "none";
      +         document.getElementById('confirm').style.display = "none";
      +         return;
      +      }
      +
      +      var score = 0;
      +   
      +      if (newPassword.match(/[0-9]/))
      +         score++;
      +      if (newPassword.match(/[a-z]/))
      +         score++;
      +      if (newPassword.match(/[A-Z]/))
      +         score++;
      +      if (newPassword.match(/[-_]/))
      +         score++;
      +      if (newPassword.length < 8)
      +      {
      +         score = 0;   
      +      }
      +      
      +      if(score < 4)
      +      {
      +         
      +         document.getElementById('messageDiv').innerHTML = 'Password does not meet criteria<br>Should have: A-Z a-z 0-9 and - (dash) or _ (underscore)';
      +         document.getElementById('submitBtn').style.display = "none";
      +         document.getElementById('confirm').style.display = "none";
      +         return;
      +      }
      +
      +      if (password.length < 1)
      +      {
      +       	 document.getElementById('messageDiv').innerHTML = 'Please confirm new password';
      +         document.getElementById('submitBtn').style.display = "none";
      +         document.getElementById('confirm').style.display = "block";
      +         return;
      +      }
      +
      +
      +      if (password !== newPassword)
      +      {
      +       	 document.getElementById('messageDiv').innerHTML = 'New passwords do not match';
      +         document.getElementById('submitBtn').style.display = "none";
      +         document.getElementById('confirm').style.display = "block";
      +         return;
      +      }
      +
      +      document.getElementById('messageDiv').innerHTML = '';
      +      document.getElementById('confirm').style.display = "block";
      +      document.getElementById('submitBtn').style.display = "block";
      +   }
      +   </script>
      +
       </head>
       <body <c:if test="${successfullLogin ne 'true'}">
               onload="document.changePassForm.password.focus();"
      @@ -99,7 +176,7 @@
                       </div>
                   </c:if>
                   <c:if test="${successfullLogin ne 'true'}">
      -                <div id='ZLoginFormPanel' style="margin-left: 12%;">
      +                <div id='ZLoginFormPanel' style="margin-left: 12%;"><p style="color:white">&bull; Your password must NOT contain your username,<br> first name, last name and so on. <br>&bull; Do not re-use old passwords.</p>
                           <form method='post' name="changePassForm" action="" autocomplete="off" accept-charset="utf-8">
                               <input type="hidden" name="loginOp" value="login"/>
                               <table class="form">
      @@ -112,19 +189,19 @@
                                   <tr>
                                       <td class='zLoginLabelContainer'><label for="newPassword"><fmt:message key="newPassword"/>:</label></td>
                                       <td class='zLoginFieldContainer'>
      -                                    <input id="newPassword" autocomplete="off" class='zLoginField' name='loginNewPassword' type='password' value="" maxlength="${domainInfo.webClientMaxInputBufferLength}"/>
      +                                    <input id="newPassword" oninput="validateForm()" autocomplete="off" class='zLoginField' name='loginNewPassword' type='password' value="" maxlength="${domainInfo.webClientMaxInputBufferLength}"/>
                                       </td>
                                   </tr>
                                   <tr>
                                       <td class='zLoginLabelContainer'><label for="confirm"><fmt:message key="confirm"/>:</label></td>
                                       <td class='zLoginFieldContainer'>
      -                                    <input id="confirm" autocomplete="off" class='zLoginField' name='loginConfirmNewPassword' type='password' value="" maxlength="${domainInfo.webClientMaxInputBufferLength}"/>
      +                                    <input id="confirm" style="display:none" oninput="validateForm()" autocomplete="off" class='zLoginField' name='loginConfirmNewPassword' type='password' value="" maxlength="${domainInfo.webClientMaxInputBufferLength}"/>
                                       </td>
                                   </tr>
                                   <tr>
                                       <td></td>
      -                                <td><input type=submit class='zLoginButton'
      -                                                     value="<fmt:message key="changePassword"/>"/></td>
      +                                <td><input type=submit id="submitBtn" class='zLoginButton' style="display:none"
      +                                                     value="<fmt:message key="changePassword"/>"/><br><div style="width:300px; height:50px;font-weight:700" id="messageDiv">Submit button appears after you<br> have chosen a correct password.</div></td>
                                   </tr>
                               </table>
                           </form>
      
      

You can also download the patch here:

* https://raw.githubusercontent.com/Zimbra-Community/ADPassword/master/patches/changepass
