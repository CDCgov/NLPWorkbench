<html>
<head>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <style type="text/css">

    .popup {
    position: relative;
    display: inline-block;
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
}

/* The actual popup */
.popup .popuptext {
    visibility: hidden;
    width: 160px;
    background-color: #555;
    color: #fff;
    text-align: center;
    border-radius: 6px;
    padding: 8px 0;
    position: absolute;
    z-index: 1;
    bottom: 125%;
    left: 50%;
    margin-left: -80px;
}

/* Popup arrow */
.popup .popuptext::after {
    content: "";
    position: absolute;
    top: 100%;
    left: 50%;
    margin-left: -5px;
    border-width: 5px;
    border-style: solid;
    border-color: #555 transparent transparent transparent;
}

/* Toggle this class - hide and show the popup */
.popup .show {
    visibility: visible;
    -webkit-animation: fadeIn 1s;
    animation: fadeIn 1s;
}

/* Add animation (fade in the popup) */
@-webkit-keyframes fadeIn {
    from {opacity: 0;} 
    to {opacity: 1;}
}

@keyframes fadeIn {
    from {opacity: 0;}
    to {opacity:1 ;}
}

        #load {
  position: absolute;
  left: 50%;
  top: 50%;
  z-index: 1;
  width: 150px;
  height: 150px;
  margin: -75px 0 0 -75px;
  border: 16px solid #f3f3f3;
  border-radius: 50%;
  border-top: 16px solid #3498db;
  width: 120px;
  height: 120px;
  -webkit-animation: spin 2s linear infinite;
  animation: spin 2s linear infinite;
}

@-webkit-keyframes spin {
  0% { -webkit-transform: rotate(0deg); }
  100% { -webkit-transform: rotate(360deg); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Add animation to "page content" */
.animate-bottom {
  position: relative;
  -webkit-animation-name: animatebottom;
  -webkit-animation-duration: 1s;
  animation-name: animatebottom;
  animation-duration: 1s
}

@-webkit-keyframes animatebottom {
  from { bottom:-100px; opacity:0 } 
  to { bottom:0px; opacity:1 }
}

@keyframes animatebottom { 
  from{ bottom:-100px; opacity:0 } 
  to{ bottom:0; opacity:1 }
}
  </style>
  <script type="text/javascript">
    document.onreadystatechange = function () {
        var state = document.readyState;
        console.log(state);
        if (state == "loading") {
          document.getElementById("result").style.visibility = "visible";
          document.getElementById("load").style.visibility = "visible";
        } else if (state == "complete") {
          document.getElementById("load").style.visibility = "hidden";
          document.getElementById("result").style.visibility = "visible";
        }
      }
    function preloader(){
            document.getElementById("result").style.visibility = "visible";
            document.getElementById("load").style.visibility = "visible";
    }
    function myFunction() {
      var popup = document.getElementById("myPopup");
      popup.classList.toggle("show");
    }
    function myFunction2() {
      var popup = document.getElementById("myPopup2");
      popup.classList.toggle("show");
    }
    function myFunction3() {
      var popup = document.getElementById("myPopup3");
      popup.classList.toggle("show");
    }
  </script>
</head>
<body>
<h1> Feature Library Mechanism</h1>
<p>Users can select the features to be included in the Language Model training and the source of the input as TXT or ANN files. The service will return a BIO file containing the specified features.</p>
<p> Users can upload multiple files. By clicking 'Execute Feature Library and Downlaod BIO File' button, the service will download a zipped file containing all the BIO files. However, if the users click 'Execute Feature Library', the service will  display the merged BIO file containing results for all the BIO files.</p>
%pipeline = features[0]
%token_type = features[1]
%time_feature = features[2]
%pos_tagger = features[3]
%noun_chunk = features[4]
%snomed_code = features[5]
%token_length = features[6]
%orth = features[7]
%left_size = features[8]
%right_size = features[9]
%left_feature_string = features[10]
%right_feature_string = features[11]
<p> Select the Pipeline to be executed: </p>
<form id="feature" action="/clew/feature_library/result" method="post" enctype="multipart/form-data">
  <p><strong>Choose one of the input file formats below</strong></p>
  <strong>Choose the input ANN File: </strong> <input type="file" name="myANNFile" multiple>
  <strong>Choose the input TXT File: </strong> <input type="file" name="myFile" multiple>
  <strong><p> Select the Pipeline to be executed: </p></strong>
  <select name="type">
    %if pipeline == "stanford":
      <option value="stanford" selected="selected">Stanford</option>
      <option value="opennlp">OpenNLP</option>
      <option value="gate">GATE</option>
    %elif pipeline == "opennlp":
      <option value="stanford">Stanford</option>
      <option value="opennlp" selected="selected">OpenNLP</option>
      <option value="gate">GATE</option>
    %elif pipeline == "gate":
      <option value="stanford">Stanford</option>
      <option value="opennlp">OpenNLP</option>
      <option value="gate" selected="selected">GATE</option>
    %end
  </select>
  <strong><p>Select the Features to be included for the current token:</p></strong>
  %if token_type == "yes":
    <input type="checkbox" name="token_type" value="token_type" checked> Token Type<br>
  %else:
    <input type="checkbox" name="token_type" value="token_type"> Token Type<br>
  %end
  %if time_feature == "yes":
    <input type="checkbox" name="time_feature" value="time_feature" checked> Time Feature<br>
  %else:
    <input type="checkbox" name="time_feature" value="time_feature"> Time Feature<br>
  %end
  %if pos_tagger == "yes":
    <input type="checkbox" name="pos_tagger" value="pos_tagger" checked> POS Tagger Result<br>
  %else:
    <input type="checkbox" name="pos_tagger" value="pos_tagger"> POS Tagger Result<br>
  %end
  %if noun_chunk == "yes":
    <input type="checkbox" name="noun_chunker" value="noun_chunker" checked> Noun Chunker Result<br>
  %else:
    <input type="checkbox" name="noun_chunker" value="noun_chunker"> Noun Chunker Result<br>
  %end
  %if snomed_code == "yes":
    <input type="checkbox" name="snomed_code" value="snomed_code" checked> SNOMED Code (MetaMap)<br>
  %else:
    <input type="checkbox" name="snomed_code" value="snomed_code"> SNOMED Code (MetaMap)<br>
  %end
  %if token_length == "yes":
    <input type="checkbox" name="token_length" value="token_length" checked> Token Length<br>
  %else:
    <input type="checkbox" name="token_length" value="token_length"> Token Length<br>
  %end
  %if orth == "yes":
    <input type="checkbox" name="orthography" value="orthography" checked> Orthography Information<br>
  %else:
    <input type="checkbox" name="orthography" value="orthography"> Orthography Information<br>
  %end
  <strong><p>Specify the window size for inclusion of the neighbouring tokens:</p></strong>
  Left Window Size: <input type="text" name="left_size" value="{{left_size}}"><br>
  Right Window Size: <input type="text" name="right_size" value="{{right_size}}"><br>
  <strong><p>Specify the features to be included for the neighbouring tokens:</p></strong>
  <p>Select the Features for tokens on the left to be included:</p>
  %if "word" in left_feature_string.split(","):
    <input type="checkbox" name="prev_word" value="word" checked> Token Name<br>
  %else:
    <input type="checkbox" name="prev_word" value="word"> Token Name<br>
  %end
  %if "type" in left_feature_string.split(","):
    <input type="checkbox" name="prev_token_type" value="token_type" checked> Token Type<br>
  %else:
    <input type="checkbox" name="prev_token_type" value="token_type"> Token Type<br>
  %end
  %if "time" in left_feature_string.split(","):
    <input type="checkbox" name="prev_time_feature" value="time_feature" checked> Time Feature<br>
  %else:
    <input type="checkbox" name="prev_time_feature" value="time_feature"> Time Feature<br>
  %end
  %if "pos" in left_feature_string.split(","):
    <input type="checkbox" name="prev_pos_tagger" value="pos_tagger" checked> POS Tagger Result<br>
  %else:
    <input type="checkbox" name="prev_pos_tagger" value="pos_tagger"> POS Tagger Result<br>
  %end 
  %if "chunk" in left_feature_string.split(","):
    <input type="checkbox" name="prev_noun_chunker" value="noun_chunker" checked> Noun Chunker Result<br>
  %else:
    <input type="checkbox" name="prev_noun_chunker" value="noun_chunker"> Noun Chunker Result<br>
  %end
  %if "code" in left_feature_string.split(","):
    <input type="checkbox" name="prev_snomed_code" value="snomed_code" checked> SNOMED Code (MetaMap)<br>
  %else:
    <input type="checkbox" name="prev_snomed_code" value="snomed_code"> SNOMED Code (MetaMap)<br>
  %end
  %if "length" in left_feature_string.split(","):
    <input type="checkbox" name="prev_token_length" value="token_length" checked> Token Length<br>
  %else:
    <input type="checkbox" name="prev_token_length" value="token_length"> Token Length<br>
  %end
  %if "orth" in left_feature_string.split(","):
    <input type="checkbox" name="prev_orthography" value="orthography" checked> Orthography Information<br>
  %else:
    <input type="checkbox" name="prev_orthography" value="orthography"> Orthography Information<br>
  %end
  <p>Select the Features for tokens on the right to be included:</p>
    %if "word" in right_feature_string.split(","):
      <input type="checkbox" name="next_word" value="word" checked> Token Name<br>
    %else:
      <input type="checkbox" name="next_word" value="word"> Token Name<br>
    %end
    %if "type" in right_feature_string.split(","):
      <input type="checkbox" name="next_token_type" value="token_type" checked> Token Type<br>
    %else:
      <input type="checkbox" name="next_token_type" value="token_type"> Token Type<br>
    %end
    %if "time" in right_feature_string.split(","):
      <input type="checkbox" name="next_time_feature" value="time_feature" checked> Time Feature<br>
    %else:
      <input type="checkbox" name="next_time_feature" value="time_feature"> Time Feature<br>
    %end
    %if "pos" in right_feature_string.split(","):
      <input type="checkbox" name="next_pos_tagger" value="pos_tagger" checked> POS Tagger Result<br>
    %else:
      <input type="checkbox" name="next_pos_tagger" value="pos_tagger"> POS Tagger Result<br>
    %end 
    %if "chunk" in right_feature_string.split(","):
      <input type="checkbox" name="next_noun_chunker" value="noun_chunker" checked> Noun Chunker Result<br>
    %else:
      <input type="checkbox" name="next_noun_chunker" value="noun_chunker"> Noun Chunker Result<br>
    %end
    %if "code" in right_feature_string.split(","):
      <input type="checkbox" name="next_snomed_code" value="snomed_code" checked> SNOMED Code (MetaMap)<br>
    %else:
      <input type="checkbox" name="next_snomed_code" value="snomed_code"> SNOMED Code (MetaMap)<br>
    %end
    %if "length" in right_feature_string.split(","):
      <input type="checkbox" name="next_token_length" value="token_length" checked> Token Length<br>
    %else:
      <input type="checkbox" name="next_token_length" value="token_length"> Token Length<br>
    %end
    %if "orth" in right_feature_string.split(","):
      <input type="checkbox" name="next_orthography" value="orthography" checked> Orthography Information<br>
    %else:
      <input type="checkbox" name="next_orthography" value="orthography"> Orthography Information<br>
    %end
  <br><br>
  <button type="submit" onclick="preloader();">Execute Feature Library</button> <div class="popup" onclick="myFunction()">Description
  <span class="popuptext" id="myPopup">The button 'Execute Feature Library' will run the Feature Library and display the feature result as well as the process plot on the page. If there are multiple input files, the merged BIO result will be displayed.</span>
</div> <br> <br>
  <button type="submit" formaction="/clew/feature_library/output/{{output}}" onclick="preloader();">Execute Feature Library and Download BIO File</button> <div class="popup" onclick="myFunction2()">Description
  <span class="popuptext" id="myPopup2">The button 'Execute Feature Library and Download BIO File' will run the Feature Library and download the feature result as a BIO file without displaying result on the page. It supports the users to upload multiple files and download a zipped file containing all BIO files.</span>
</div> <br> <br>
  <button type="submit" formaction="/clew/feature_library/main_plot" onclick="preloader();">Generate process plot</button> <div class="popup" onclick="myFunction3()">Description
  <span class="popuptext" id="myPopup3">The button 'Generate process plot' will display the process plot on the page. </span>
</div>
</form>
<div id="load"></div>
<div id="result">
<div>
  <p>The plot for the process selected is displayed below:</p>
  <img src="/clew/feature_library/static/{{plot}}">
</div>
</div>
</body>
</html>