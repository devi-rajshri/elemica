# Elemica Login App with Play, Sorm

1. After downloading into your laptop, cd into elemica
2. Install sbt and open terminal to run sbt in Elemica with the following command
3. After all dependencies have been downloaded in the sbt console, type in:
start -Dhttp.port=disabled -Dhttps.port=9443 -Dhttps.keystore=./example.com.jks -Dhttps.keyStorePassword="123456"
4. Type in http://localhost:9443 in your browser and you should see the app.
5. I have pre-created a user "user@email.com" with password "password". If you would like to
change/ add / delete users, please add them in Global.scala file.
6. This has only been tested on Chrome/ Mac O/s.

