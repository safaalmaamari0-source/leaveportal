**Leave Portal Application**

This is a Spring Boot leave request application deployed on an OCI Compute instance.

**Main Features**

*Google OAuth login
*Upload .txt leave request files
*Store files in OCI Object Storage
*Store file metadata in Oracle Database
*Categorize leave requests using Gemini
*List and download uploaded files
*Run the application using systemd

**Deployment**

*Public URL: http://leaveapp.ddns.net:8084
*Application port: 8084
*Application folder: /home/opc/safa_leaveapp
*systemd service: safa-leave.service

**Check the service:
sudo systemctl status safa-leave.service

**View logs:
sudo journalctl -u safa-leave.service -f

**Database**

The application uses Oracle Database Free.
*Port: 1522
*Service: FREEPDB1
*Schema/User: APP2
*The database stores the file name, user email, leave reason, category, upload time, and OCI object information.

**OCI Object Storage**

*Bucket: leave-bucket-pair-01
*My prefix: safa/
*Files uploaded from my application are stored under my prefix.

**Gemini Categorization**

*A background scheduler checks leave requests with no category, sends the leave reason to Gemini, and saves the returned leave category in Oracle Database.
*Example categories:
Sick Leave
Annual Leave
Emergency Leave
Maternity Leave
Unpaid Leave
Other

**Google OAuth**

*Users sign in with Google before using the application.
*Redirect URI used for the deployed application:
http://leaveapp.ddns.net:8084/login/oauth2/code/google


**Build**

*Build the project with Maven and generate the JAR:
*target/leave_portal_app-0.0.1-SNAPSHOT.jar
*The JAR is then copied to the OCI VM and run using systemd.

