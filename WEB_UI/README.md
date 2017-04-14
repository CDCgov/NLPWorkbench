<h1>Intro</h1>
This project folder holds the static web site for the front end part of the NLP Workbench.

As of now (4/14/2017), it is only a prototype for discussion of ideas. This prototype 
shows what type of content we want to include, what kind of functionality can be offered,
how it will interact with Web Services implementations provided by this project, and some
documentation as to the use of both the Workbench and the web Services.

<h1>Running the site</h1>
In order to run this site locally on your environment, simply deploy the files on your 
web server of choice. 

If you want to quickly test it, you can follow the steps below:

<h2>Pre requisites:</h2>
<ul>
  <li>Node.js</li>
  <li>If testing the implementations, have the NLP_WS services running</li>
</ul>

<h2>Steps:</h2>
<ol>
  <li>Install http-server module: <code>npm install http-server -g</code></li>
  <li>run the http server: <code>http-server <path-to-files></code><br>
     <code>ex.: http-server . </code></li>
</ol>

This should start a node js web server on port 8081. Simply open a browser and go to
http://localhost:8081. You should see the home page (Index.html)

   

