<h1>Introduction</h1>
This project exposes a simple Web Service architecture that exposes some NLP pipeline tasks
from various frameworks as web services.

The architecture currently exposes web services as RESTful calls using Java and Spring framework.
Springboot is currently used to encapsulate a web server and make the service available without
any other external dependencies.

The focus here is to expose the architecture of the web services and not necessarily the fucntionality
that is being exposed. 
Currently, the services accept simple texts as input, i.e., the entire content is treated as content. 
Some services use GET request parameters, some use POST body content as examples of each verb. The 
output is straight forward json arrays with the target separation of each method - sentences and tokens.

This is just for simplicity and does not mean these types of inputs and outputs have to be used on
actual domain specific services.

Currently, there's no database dependencies.

<h1>Running the Web Services</h1>

In order to run the webservices locally on your environment, you can deploy it on your web applicaton 
server of choice, or run them as springboot applications with the embedded tomcat server.

<h2>Pre RequisitesL</h2>
<ul>
	<li>Have Java 1.8 or newer installed</li>
	<li>If you want to test some POST methods, have appropriate tools installed, such as 
		<a href="https://soapui.org" target="_blank">SoapUI</a> or
		<a href="https://www.getpostman.com" target="_blank">Postman</a>
	</li>
</ul>

<h2>Steps:</h2>
<ol>
	<li> Simply run the included gradle task: <code>gradlew bootRun</code></li>
</ol>
This should start the service on port 8080.

The service does not have any UI on its own, but you can use the tools above the exercise some of the
methods, or use the NLP Worbench WEB_UI as frontend to call some methods.

API documentation is available as a Swagger file in the project itself.
open the api.yaml file on <a href="http://editor.swagger.io/">swagger editor</a>
