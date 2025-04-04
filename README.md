# Project Repository for COSC 310: Software Engineering (Project A) 

--- 

This is a Spring Boot Application. All files are found in the 'Projecta-Backend' folder. 

## Basic Information: 

- The project skeleton is generated from the Spring Initializer at https://start.spring.io/.
- The 6 folders (Model, Repository, Service, Controller, Config, Util) defined in **src\main\java\com\hackademics** ... Will contain the implementation for most necessasry components of the application.

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
- Render

## Group Members 
- Tyler Schaus 
- Misha Gavura
- Nadeen Hamdona
- Lexi Loudiadis
- Aditi Bajaj

## March 2 (Milestone 4) Update 

100% of Backend complete. 

All core functional requirements including the bonus are complete, tested, and functioning. 

The Backend is now hosted publically on Render.com at:
https://team-hackademics-projecta-backend.onrender.com

Bug Report: 

- Issue #80 - Student Records
- Issue #81 - 401 Responses
- Issue #82 - Over enrollment possibility 
- Issue #83 - Schedule conflict verification 

### Bug coverage: 
Once we began connecting with the front end we realized the bugs that were more important to fix were actually #80 and #81. CSV formats are not consistently as expected, and **most** errors codes now accurately reflect what went wrong. We realized that the code was simply throwing runtime exceptions from some services layers when issues occured (i.e., When trying to create a course, the subject id provided did not exist... now properly throws a 404 not found). 

### Coverage testing: 

The following documents demonstrate the system overall coverage from testing. 

You will have to download the file from my Google Drive to view its contents. Our coverage testing reached 71% for service layers, 84% for utility classes, and 74% for model. I am happy with the coverage we recieved given the majority of non-covered would infrequently be hit, and was very similar to other thoroughly tested code. Furthermore, via Postman testing we were able to prove that all endpoints funcitoned as expected for all expectable use cases.

https://drive.google.com/file/d/1pBvJPjzdkEd7z-FY1ltk52hG9Z1004MT/view?usp=sharing

**Important Note:** 

I have already sent an email to the professor and explained the situation to our TA in our lab time but I wanted to restate it here. While implementing the email sending functionality, I forgot to omit this behaviour from tests as it was my first time implenting this kind of functionality. Therefore, when GitHub Actions Workflow tried to run my tests, it noticed I was attempting to send emails from it, and flagged my account as spam. As a result, my account got shadow banned, I cannot run a workflow, and my team members as well as anyone else completely lost access to my repositories. I believe my team coordinated the situation well to find a viable work around so that we could still complete the project in its entirety on time. 

Given this, Lexi created an empty repository on her GitHub account and uploaded the final version of the project there so that you can still view the repository. It has the full commit history and all files, however it does not contain the PR history or issue board. I explained this to the TA already and he seemed to think it would be okay, but I am more than happy to come in to office hours or any other time on campus so you can view the original repository on my computer as I believe that is the only way you would be able to see it. 





