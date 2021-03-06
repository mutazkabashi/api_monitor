# API_Monitor 

A Service Poller web application that tracks a list of Rest web services (API) , and 
 checks the services’ status.

**Functional Requirements**
- User could add/update/delete Rest-web services using user interface (web application)
- Each web service has Url,name,method,status, creationdate, and last modificationDate.

Note :- Webservice (GET) with one parameter is only supported

- User Management module , to add application’s users.
- Dash board to  display the apis(Rest-web services) status.

Note:- FreeAPI from the follwing web sites (https://apipheny.io/free-api/) has been used to test the functionality of the application

**Technology  Stack**
- Front-End (UI) Reactjs with hooks and redux.
- JAVA 11 

-Back-End Vert.x (java asynchronous event-driven application framework )

-ORM Hibernate
-Database Mysql (production),h2 for testing
- Testing (Jest,Junit,Vert.x test)

_________________________________________________________________________

**How to Strat and Build the application?** 

_Run the following commands:_

Step1: Execute the sql script file (Database_schema.sql) to create the database schema;  

NOTE:- database username=root and password=root or you should change the username and password 
in src/main/resources/META-INF/persistence.xml

Step2: mvn package

Note :- some Exception stack trace will appear on the console as part of running test to check Exception handling

A new file with the name of "api-monitor-1.0.0-fat.jar" will be created inside target folder.

Step3: java -jar -Dvertx.options.maxEventLoopExecuteTime=20000000000  target/api-monitor-1.0.0-fat.jar


_________________________________________________________________________
**Create User/signup**

1- type the following URL in web broswer (http://localhost:8080/signup)
  fill in all the fields of the signup form and click on signup button
  then you will be directed to login page (http://localhost:8080/login)
  
 2- Type your username and password on login screen and click on login button.
 
 3- click on webservices -> Display-as-list or type the following url on the browser
    (http://localhost:8080/display-as-list)
 
 4- add web service(s).
 
   Note You should select Http or Https from select service menu, and website field should conatin the url without protocol (wihtout http or https)
 
 5- click on dashboard on the top menu on the screen or type the following URL
  on the browser (http://localhost:8080/dashboard)    


