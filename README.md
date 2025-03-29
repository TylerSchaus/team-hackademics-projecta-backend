# Project Repository for COSC 310: Software Engineering (Project A) 

--- 

This is a Spring Boot Application. All files are found in the 'Projecta-Backend' folder. 

## Basic Information: 

- The project skeleton is generated from the Spring Initializer at https://start.spring.io/.
- The 5 folders (Model, Repository, Service, Controller, Config) defined in **src\main\java\com\hackademics** ... Will contain the implementation for most necessasry components of the application.
- The database is currently hosted on Aiven, for security purposes I limited access to certain IPv4 addresses, the University address is permitted so you may only be able modify the database when runnning the backend on the University network.

### Make sure it works: 
To make sure the project works, open your terminal in VSCode and navigate to the project directory. **By Default** it will open to the Repository Directory, make sure to navigate to the project directory first. Then in your terminal run: mvn **spring-boot:run** 

It should take 1-5 seconds to launch and then display the following line: 

**: Started ProjectaBackendApplication in 1.672 seconds (process running for 1.975)**

If you see this, the application is running. To terminate the application, in your terminal use the command **Ctrl + C**, and then if it asks you to terminate the batch job, press **'y'** and then **enter**. 

## Full Tech Stack: 
- Java
- Spring Boot Framework ( including Rest API capabilities)
- JWT Authentication
- MySQL Databases
- Aiven
- Fly.io

## Group Members 
- Tyler Schaus 
- Misha Gavura
- Nadeen Hamdona
- Lexi Loudiadis
- Aditi Bajaj

## March 28 (Milestone 3) Update 

Approx. 90% of Backend complete. 
Endpoints are defined for Users, Subjects, Courses, Enrollments, Lab Sections, Waitlists, Waitlist Enrollments, and Grades allowing all functionality related to these entities. 
Along with endpoints, their corresponding service, repository, and model implementations are complete. 
JWT Authetnication is set up, and tested. 

The only remaining core functionality is: 
- Email notifcations for enrollment and unenrollment.
- Improved filtering for students.

Bug Report: 

Issue #80 - Student Records
Issue #81 - 401 Responses
Issue #82 - Over enrollment possibility 
Issue #83 - Schedule conflict verification 

All bugs are detailed in their descriptions in the respective issues, including their description, reproducibility, affected components, and severity level. 

As I continue testing to the APIs, I intend to add more bugs to this list whenever I identify them. 

Coverage testing: 

The following documents demonstrate the system overall coverage from testing. 


[test-coverage.zip](https://github.com/user-attachments/files/19515951/test-coverage.zip)

I do want to clarify one thing about these reports.

When developing the backend for this system (early - middle stages) ... I was under the impression that allowing administrators to create courses, subjects, lab sections, and waitlists was a requirement. However, upon discussing with the TA, I understand this is NOT a requirement. Therefore upon realizing this, I opted not to write testing for the endpoints and logic that involves this functionality. I intend to thoroughly go over the codebase to remove this now unnecessary code, and replace it with a preloader class that preloads the database this kind of data. Since this code is still there, but not tested, coverage lies around ~60%. I am confident that once we remove the code we do not need, it will improve to ~80. 





