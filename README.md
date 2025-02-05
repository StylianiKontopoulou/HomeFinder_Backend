# Introduction
The sections below describe the steps that must be followed in order to setup the backend API service using WildFly as an application server, either manually or using Docker containers.

## Docker setup
- Navigate into the files `pom.xml` and `src\main\resources\META-INF\persistence.xml` and verify that the local setup option is commented-out.
- If you have a local instance of MySQL server running, this will need to be stopped.
- From the parent directory execute:
```
docker-compose up
```

The backend service and a MySQL server instance should be running. 

## Manual configuration

- Before starting into the files `pom.xml` and `src\main\resources\META-INF\persistence.xml` and verify that the Docker setup option is commented-out.

### Database setup

- Download and install MySQL from https://dev.mysql.com/downloads/installer/
- Download and install MySQL Workbench from https://dev.mysql.com/downloads/workbench/
- Open MySQL Workbench, connect to the local server (e.g. localhost:3306) and create a new database:
```
CREATE SCHEMA homefinder
```

### WildFly configuration

- Download WildFly from https://www.wildfly.org/ (version used for the project 33.0.1) and unzip the downloaded file (see more in https://docs.wildfly.org/35/Installation_Guide.html#Zipped_Installation)
- Navigate to `bin` in the extracted folder.
- Execute `standalone.bat` to start WildFly in "standalone" mode.
- Execute `add-user.bat` to create an administration user.
- Navigate to `localhost:9990` which is the default directory WildFLy Admin console.
- Login with the credentials used to create the administration user.
- Download the Platform Independent mysql-connector-j-9.0.0.zip from `https://downloads.mysql.com/archives/c-j/` (direct download link `https://downloads.mysql.com/archives/get/p/3/file/mysql-connector-j-9.0.0.zip`)
- Extract the zip and locate the `mysql-connector-j-9.0.0.jar` file.
- Navigate to Deployments. Click on the add icon and then Upload deployment. Upload the jar file and click on Finish.
- Navigate to Configuration > Subsystems > Datasources & Drivers > Datasources
- Click on the add icon and then "Add datasource". Select MySQL. Click on Next. Provide a name for the datasource that will be used from the API's persistence configuration.
The default values that were used for development are:
```
Name: HomeFinder
JNDI Name: java:/HomeFinder
```
Click on Next.
```
Driver Name: mysql-connector-j-9.0.0.jar
Driver Class Name:com.mysql.cj.jdbc.Driver
```
Click on Next. Provide the credentials for connecting to MySQL database that was previously created (see sample values below)
```
Connection URL: jdbc:mysql://localhost:3306/homefinder
User Name: root
Password: root
```
Click on Next. Test the connection to make sure it works and complete the wizard.

### Application deployment

- Download and install Apache NetBeans from https://netbeans.apache.org/front/main/download/
- Download apache-maven from https://maven.apache.org/download.cgi
- Setup mvn in system variables to allow execution of commands in terminal
- In Apache NetBeans click on File > Open Project and select the project folder
- Select Tools > Servers > Add Server. 
- Select WildFly Application Server and click on Next
- Select the extracted WildFly folder as the Server Location
- Select the file in `\configuration\standalone-full.xml` within the extracted WildFly folder as the Server Configuration
- Finish the Server setup
- Open a command window in the project folder and run:
```
mvn clean install
```
- Execute the following command to seed the database with initial data:
```
mvn flyway:migrate 
```
- Click on Run project to deploy the application to WildFly and start the backend service.

