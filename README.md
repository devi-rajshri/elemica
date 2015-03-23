# Elemica Login App with Play, Sorm

1. After downloading into your laptop, cd into elemica
2. Install sbt and open terminal to run sbt in Elemica with the following command
3. After all dependencies have been downloaded in the sbt console, type in:
start -Dhttp.port=disabled -Dhttps.port=9443 -Dhttps.keystore=./example.com.jks -Dhttps.keyStorePassword="123456"
4. Type in http://localhost:9443 in your browser and you should see the app.
5. I have pre-created a user "user@email.com" with password "password". If you would like to
change/ add / delete users, please add them in Global.scala file.
6. This has only been tested on Chrome/ Mac O/s.

#Notes on Security

Play's session is cookie based.

The session is meant to only hold the most basic of information - such as the unique username. Anything larger than that is supposed to be stored in memcached or in any other form of caching / db.


The top 10 security issues are listed in the owasp site below:
https://www.owasp.org/index.php/Top_10_2010-A1-Injection

1. A1: Injection : This is something the programmer will have to watch out for by using parameterized queries. In this case - The login page does not expose any get links that is vulnerable.
2. A2: XSS : Html is by default escaped in the templates . You need the special tag @Html to let Play know you trust the html and want to render it as such. [https://www.playframework.com/documentation/2.2.x/ScalaCustomTemplateFormat]( https://www.playframework.com/documentation/2.2.x/ScalaCustomTemplateFormat) . With the addition of the Security Headers filter in Global in the elemica app, you should get an extra strict layer of protection against xss. 
[https://www.playframework.com/documentation/2.3.3/SecurityHeaders](https://www.playframework.com/documentation/2.3.3/SecurityHeaders)
3. A3: Broken Authentication and Session Management: Session ids in play are cookie based and not url based. So session cannot be passed onto another user through url.I have also set a time out through play cache to be 5 mins. Other techniques can also be used for timeout - I used the most convenient one to me. XSS flaws have been taken care of by Play ( plus with some additional configurations). Password has been encrypted. I have also made the play session secure through a config on application.conf
4. A4: Insecure direct object references: The dashboard page cannot be accessed unless the user is logged in. As long as an action uses the withAuth method of Security - the resource provided by that action will be not be accessible unless logged in. In the login app instance , the values got from the form are escaped by play automatically - so it's not possible to hack those values.
5. A5: Cross-Site Request Forgery (CSRF) Play's POST requests allow for authenticity tokens to prevent this. I have added a CSRF token to all play requests per documentation below . Play has also an application secret token that is used for signing CSRF tokens and for built in encryption which is by default set to a value in application.conf. If this were a live production environment , it would reside in a separate production.conf. This per Play , makes it impossible for 3rd parties to hack into play's session.
 [https://www.playframework.com/documentation/1.2/security](https://www.playframework.com/documentation/1.2/security) ,
 [https://www.playframework.com/documentation/2.3.x/ApplicationSecret](https://www.playframework.com/documentation/2.3.x/ApplicationSecret)
[https://www.playframework.com/documentation/2.2.x/ScalaCsrf](https://www.playframework.com/documentation/2.2.x/ScalaCsrf)
6. A6: Security Misconfiguration The default error reporting process seems safe on production (no stack trace leaks). There is no catch all routes by default since Play 2.0 which would have left open a security vulnerability. We should make sure that ports/ pages etc that are unused is not left open - but this falls more on the operations side and cannot be effectively shown through a simple login app.
7. A7: Insecure Cryptographic Storage. Developer is responsible to encrypt sensible information in the database. I have demonstrated this by encrypting the password in the login app.
8. A8: Failure to Restrict URL Access. This is very well implemented through Play. Every page served through the withAuth action is challenged for its authorization prior to being shown.
9. A9: Insufficient Transport Layer Protection. Play supports SSL and I have added that to the login app.
10. A10: Unvalidated Redirects and Forwards. Play redirect is via 302, not hardcoded strings. I have used redirect in the login post action without any url parameters. I feel the fact that every authenticated page is wrapped with "withAuth" , implies that even if an unvalidated redirect causes the user to get to an authentication page - the withauth checks it and ensures the user is sent back to the initial page.

Some other security related issues:

Related Domain Attacks - Cookies stored by one site can be modified by another if the two sites happen to share a sufficiently long suffix. For example, two such sites are docs.google.com and www.google.com, having google.com as a suffix. Play's default configuration disallows that and I had used the default. ( application.defaultCookieDomain)

[https://www.playframework.com/documentation/1.2.3/configuration](https://www.playframework.com/documentation/1.2.3/configuration)