# Abercrombie AEM Developer Skill Assessment

## Pre Steps (Important)

1. Ensure `mvn -version` is using Java 8
2. Create a new branch in your repo.
3. Complete exercises below by creating/modifying code. You can architect the project how you like re: folder structure, how you name your files, etc. Please add comments at the start and end of the code (i.e. `***Begin Code - Candidate Name***` and `***END Code*****`). Use your best judgement as a developer.
4. Push the code onto the new branch to your own public Git repository, and send the link to your recruiter / rep.
5. Pretend your code is going into a `PRODUCTION` environment, or that you are writing a pull request for an established open source project. Do not rush these exercises or cut corners in the name of speed. We aren't interested in the code you can write under pressure; no one writes amazing code when they are rushing. This is your chance to show off. Write your best code.
6. This exercise is to be completed without coaching or other outside assistance. Obviously, you may feel free to use whatever online resources you like -- `StackOverflow` etc. -- but it is _not acceptable_ to utilize other developers to help you finish this task.


## Exercise 1

1.	Create a service that will run once every 2 minutes and only on the author environment.
2.	This service will find all pages that have been published and will set a property named `processedDate` to the current time.
3.	Provide unit tests with at least 80% test coverage.


## Exercise 2

1.	Create a servlet that will output the first and last name of the author who last modified the targeted page.
2.	It will also contain a list of any child pages that were also modified by this user.
3.	Based on the extension, the servlet should return the output in either XML or JSON format
4.	Provide unit tests with at least 80% test coverage.


## Exercise 3

1.	Create a component that contains a form with a text input and a submit button.
2.	The label for the input and the text of the submit button must be authorable.
3.	On desktop, the component should have a light grey background and black text.
4.	On mobile, the component should have a black background with white text.
5.	When the submit button is click, the component should display the title, description, image, and last modified date for each component whose title or description contain the text from the input field.
6.	If no pages are returned, it should instead display text alerting the user that their term returned zero results.
7.	Provide unit tests with at least 80% test coverage.


## Starting AEM

1.	To download the AEM 6.5.0 Jar file, go to the following page and login using your Adobe ID:
    a.	https://experience.adobe.com/#/downloads/content/software-distribution/en/aem.html?package=%2Fcontent%2Fsoftware-distribution%2Fen%2Fdetails.html%2Fcontent%2Fdam%2Faem%2Fpublic%2Fadobe%2Fpackages%2Fcq650%2Fquickstart%2Fcq-quickstart-6.5.0.jar
2.	For the license file, please use the license.properties file provided within the project repository.
