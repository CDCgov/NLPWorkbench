<!DOCTYPE HTML>
<%
########################################################################################################################
#----------------------------------------------------------------------------------------------------------------------#
import commands, sys, os, json, subprocess
reload(sys);
sys.setdefaultencoding('utf8')
lappsjson = hda.get_raw_data()
lappsjsonfil = hda.dataset.file_name
lsdpath = os.path.join(os.getcwd(),'../mods/plugins/visualizations/nlp_brat/json2json.lsd')
bratjson = """{
    "text" : "Unknown Text ..."
}"""
output = subprocess.Popen([lsdpath, lappsjsonfil], stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
outputjson = output.stdout.read()
if ("payload" in outputjson) and ("targets" in outputjson) and (not "http://vocab.lappsgrid.org/ns/error" in outputjson):
    bratjson = json.loads(outputjson)["payload"]["targets"][0]
json2jsonexp = output.stderr
%>


<%
root = h.url_for( '/' )
%>

<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
    <title>${hda.name} | ${visualization_name}</title>
    ${h.stylesheet_link( root + 'plugins/visualizations/nlp_brat/static/css/style-vis.css' )}
    ${h.stylesheet_link( root + 'plugins/visualizations/nlp_brat/static/css/bootstrap-theme.min.css' )}
    ${h.stylesheet_link( root + 'plugins/visualizations/nlp_brat/static/css/bootstrap.min.css' )}
    ${h.stylesheet_link( root + 'plugins/visualizations/nlp_brat/static/css/theme.css' )}

    ${h.stylesheet_link( root + 'plugins/visualizations/nlp_brat/static/css/codemirror/codemirror.css' )}
    ${h.stylesheet_link( root + 'plugins/visualizations/nlp_brat/static/css/codemirror/simplescrollbars.css' )}
    ${h.stylesheet_link( root + 'plugins/visualizations/nlp_brat/static/css/codemirror/fullscreen.css' )}
    ${h.stylesheet_link( root + 'plugins/visualizations/nlp_brat/static/css/codemirror/dialog.css' )}
    ${h.stylesheet_link( root + 'plugins/visualizations/nlp_brat/static/css/codemirror/matchesonscrollbar.css' )}
    ${h.stylesheet_link( root + 'plugins/visualizations/nlp_brat/static/css/codemirror/foldgutter.css' )}


    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/codemirror.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/simplescrollbars.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/markdown.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/xml.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/placeholder.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/fullscreen.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/javascript.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/active-line.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/matchbrackets.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/overlay.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/loadmode.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/meta.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/htmlmixed.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/dialog.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/searchcursor.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/search.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/annotatescrollbar.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/foldcode.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/foldgutter.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/brace-fold.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/xml-fold.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/markdown-fold.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/comment-fold.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/matchesonscrollbar.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/codemirror/typo.js' )}

    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/jquery_1.11.3_jquery-1.11.3.min.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/head.load.min.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/bootstrap.min.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/lisp2dot.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/xslt.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/viz.js' )}
    ${h.javascript_link( root + 'plugins/visualizations/nlp_brat/static/js/clearnlp_demo.js' )}

    <script>
        head.js(
            '${root}plugins/visualizations/nlp_brat/static/js/jquery.min.js',
            '${root}plugins/visualizations/nlp_brat/static/js/jquery.svg.min.js',
            '${root}plugins/visualizations/nlp_brat/static/js/jquery.svgdom.min.js',
            '${root}plugins/visualizations/nlp_brat/static/js/jquery-ui.min.js',
            '${root}plugins/visualizations/nlp_brat/static/js/webfont.js',
            '${root}plugins/visualizations/nlp_brat/static/js/util.js',
            '${root}plugins/visualizations/nlp_brat/static/js/annotation_log.js',
            '${root}plugins/visualizations/nlp_brat/static/js/dispatcher.js',
            '${root}plugins/visualizations/nlp_brat/static/js/url_monitor.js',
            '${root}plugins/visualizations/nlp_brat/static/js/visualizer.js',
            '${root}plugins/visualizations/nlp_brat/static/js/configuration.js',
            '${root}plugins/visualizations/nlp_brat/static/js/lisp2dot.js',
            '${root}plugins/visualizations/nlp_brat/static/js/viz.js'
        );
        var webFontURLs = [
            '${root}plugins/visualizations/nlp_brat/static/fonts/Astloch-Bold.ttf',
            '${root}plugins/visualizations/nlp_brat/static/fonts/PT_Sans-Caption-Web-Regular.ttf',
            '${root}plugins/visualizations/nlp_brat/static/fonts/Liberation_Sans-Regular.ttf'
        ];

        var getTextAreaValue = function (textareaId) {
            return $("#"+textareaId).val();
        }

        var setTextAreaValue = function (textareaId, val) {
            $("#"+textareaId).val(val);
        }

        var codeMirrors = {};

        var coverCodeMirror = function(textareaId, changeHandler) {
            var textarea = document.getElementById(textareaId);
            var editor = CodeMirror.fromTextArea(textarea, {
                mode: "javascript",
                lineNumbers: true,
                lineWrapping: true,
                styleActiveLine: true,
                matchBrackets: true,
                scrollbarStyle: "overlay",
                foldGutter: true,
                gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
            });
            // editor.setOption("theme", "neat");
            editor.setSize(textarea.style.width, textarea.style.height);
            codeMirrors[textareaId] = editor;
            editor.on('dblclick', function() {
                editor.setOption("fullScreen", !editor.getOption("fullScreen"));
            });
            editor.on("viewportChange", function(){
                editor.refresh();
            });
            return editor;
        }

        var getCodeMirrorValue = function(textareaId) {
            return codeMirrors[textareaId].getValue();
        }

        var setCodeMirrorValue = function(textareaId, val) {
            codeMirrors[textareaId].setValue(val);
        }

        var prettyJSON = function(textareaId) {
            var json = getTextAreaValue(textareaId);
            if(json.length > 0 && json[0] == "{" && json.slice(-1) == "}") {
                setTextAreaValue(textareaId, JSON.stringify(jQuery.parseJSON(json),null,4));
            }
        }


        var renderDotDisplay = function(dotDisplayId, inputId) {
            var docData = getTextAreaValue(inputId);
            // When has parser result.
            if (docData.indexOf("~~~~") > 0) {
                var docJSON = jQuery.parseJSON(docData.trim());
                var lisp = docJSON.text.split("~~~~")[1];
                console.log(lisp);
                var dot = lisp2dot(lisp);
                var svg = Viz(dot, "svg");
                var dotDisplay = $('#' + dotDisplayId);
                dotDisplay.html("");
                dotDisplay.html(svg);
                dotDisplay.show();
                dotDisplay.dblclick(function(){
                    $(this).toggleClass("dotdisplayfullscreen").toggleClass("dotdisplaybox");
                });
            } else {
                var dotDisplay = $('#' + dotDisplayId);
                dotDisplay.html("");
                dotDisplay.hide();
            }
        }

        var docChangeHandler = function(liveDispatcher, textareaId){
            var docInput = getTextAreaValue(textareaId);
            var docJSON;
            try {
                docJSON = jQuery.parseJSON(docInput.trim());
            } catch (e) {
                console.error('invalid JSON Data:', e);
                return;
            }
            try {
                liveDispatcher.post('requestRenderData', [$.extend({}, docJSON)]);
                console.info("docChangeHandler");
            } catch(e) {
                console.error('requestRenderData went down with:', e);
            }
        }


        var confChangeHandler = function(liveDispatcher, textareaId) {
            var collInput = getTextAreaValue(textareaId);
            var collJSON;
            try {
                collJSON = jQuery.parseJSON(collInput.trim());
            } catch (e) {
                console.error('invalid JSON Data:', e);
                return;
            }
            try {
                liveDispatcher.post('collectionLoaded',
                        [$.extend({'collection': null}, collJSON)]);
                console.info("confChangeHandler");
            } catch(e) {
                console.error('collectionLoaded went down with:', e);
            }
        }

        var listenTo = 'propertychange keyup input paste change';
        var onchange = function(textareaId, handler) {
            $("#"+textareaId).bind(listenTo, handler);
        }

        var renderBratDisplay =  function (displayId, inputId, confInputId, dotDisplayId) {
            var docData = getTextAreaValue(inputId);
            var collData = getTextAreaValue(confInputId);
            // Time for some "real" brat coding, let's hook into the dispatcher
            var liveDispatcher;
            try{
                liveDispatcher = Util.embed(displayId,
                    $.extend({'collection': null}, jQuery.parseJSON(collData.trim())),
                    $.extend({}, jQuery.parseJSON(docData.trim())), webFontURLs);
                renderDotDisplay(dotDisplayId, inputId);
                console.info("Finished: liveDispatch");
                $("#preload").fadeOut(4000, function(){});
            }catch(e) {
                console.error("ERROR: "+e+" ;doc="+docData+"; coll="+collData);
            }
            var renderError = function() {
                // setting this blows the layout (error --> red)
                $('#' + displayId).css({'border': '2px solid red'});
            };
            liveDispatcher.on('renderError: Fatal', renderError);

            onchange(inputId, function() {
                docChangeHandler(liveDispatcher, inputId);
                renderDotDisplay(dotDisplayId, inputId);
            });

            onchange(confInputId, function() {
                confChangeHandler(liveDispatcher, confInputId);
            });

            // packJSON used to make json neat and pack.
            var packJSON = function(s) {
                // replace any space with ' ' in non-nested curly brackets
                s = s.replace(/(\{[^\{\}\[\]]*\})/g,
                              function(a, b) { return b.replace(/\s+/g, ' '); });
                // replace any space with ' ' in [] up to nesting depth 1
                s = s.replace(/(\[(?:[^\[\]\{\}]|\[[^\[\]\{\}]*\])*\])/g,
                              function(a, b) { return b.replace(/\s+/g, ' '); });
                return s
            }
            // when input doc data changed
            // liveDispatcher.post('requestRenderData', [$.extend({}, jQuery.parseJSON(docInput.val()))]);

            // when config input coll data changed
            // liveDispatcher.post('collectionLoaded',  [$.extend({'collection': null}, jQuery.parseJSON(collInput.val()))]);
        }
        head.ready(function() {
            prettyJSON("lappsjson");
            coverCodeMirror("lappsjson");
            renderBratDisplay("instantbratdisplay", "docjson", "colljson", "instantdotdisplay");
        });

    </script>
    <style type="text/css">
        #instantdotdisplay svg {
            width: auto;
            height: auto;
            border: 1px solid #7fa2ff;
            font-size: 15px;
        }

        .dotdisplaybox {
            width:800px;
            height:400px;
            overflow:scroll;
        }

        .dotdisplayfullscreen {
            width:100%;
            height:100%;
            z-index:1000;
        }
    </style>
</head>


## ----------------------------------------------------------------------------
<body>
<!--h2  align="center">Online Visualization of LappsGrid</h2>
<p style="text-align:center;font-size: 12pt;"-->
    LappsGrid, <var>Version 0.3.0</var>,  May 2015</p>


    <textarea style="display:none" id="docjson">${bratjson}</textarea>
    <textarea style="display:none" id="colljson">{"entity_types":[{"type":"PERSON","labels":["Person","Per"],"bgColor":"#ffccaa","borderColor":"darken"},{"type":"TIME","labels":["Time"],"bgColor":"#ffccaa","borderColor":"darken"},{"type":"NUMBER","labels":["Number","Num"],"bgColor":"#ffccaa","borderColor":"darken"},{"type":"DATE","labels":["Date"],"bgColor":"#ffccaa","borderColor":"darken"},{"type":"LOCATION","labels":["Location","Loc"],"bgColor":"#f1f447","borderColor":"darken"},{"type":"ORGANIZATION","labels":["Organization","Org"],"bgColor":"#8fb2ff","borderColor":"darken"},{"type":"GENE","labels":["Gene","Gen"],"bgColor":"#95dfff","borderColor":"darken"},{"type":"-LRB-","labels":["-LRB-"],"bgColor":"#e3e3e3","borderColor":"darken","fgColor":"black"},{"type":"-RRB-","labels":["-RRB-"],"bgColor":"#e3e3e3","borderColor":"darken","fgColor":"black"},{"type":"DT","labels":["DT"],"bgColor":"#ccadf6","borderColor":"darken","fgColor":"black"},{"type":"PDT","labels":["PDT"],"bgColor":"#ccadf6","borderColor":"darken","fgColor":"black"},{"type":"WDT","labels":["WDT"],"bgColor":"#ccadf6","borderColor":"darken","fgColor":"black"},{"type":"CC","labels":["CC"],"bgColor":"white","borderColor":"darken","fgColor":"black"},{"type":"CD","labels":["CD"],"bgColor":"#ccdaf6","borderColor":"darken","fgColor":"black"},{"type":"NP","labels":["NP"],"bgColor":"#7fa2ff","borderColor":"darken","fgColor":"black"},{"type":"NN","labels":["NN"],"bgColor":"#a4bced","borderColor":"darken","fgColor":"black"},{"type":"NNP","labels":["NNP"],"bgColor":"#a4bced","borderColor":"darken","fgColor":"black"},{"type":"NNPS","labels":["NNPS"],"bgColor":"#a4bced","borderColor":"darken","fgColor":"black"},{"type":"NNS","labels":["NNS"],"bgColor":"#a4bced","borderColor":"darken","fgColor":"black"},{"type":"VP","labels":["VP"],"bgColor":"lightgreen","borderColor":"darken","fgColor":"black"},{"type":"MD","labels":["MD"],"bgColor":"#adf6a2","borderColor":"darken","fgColor":"black"},{"type":"VB","labels":["VB"],"bgColor":"#adf6a2","borderColor":"darken","fgColor":"black"},{"type":"VBZ","labels":["VBZ"],"bgColor":"#adf6a2","borderColor":"darken","fgColor":"black"},{"type":"VBP","labels":["VBP"],"bgColor":"#adf6a2","borderColor":"darken","fgColor":"black"},{"type":"VBN","labels":["VBN"],"bgColor":"#adf6a2","borderColor":"darken","fgColor":"black"},{"type":"VBG","labels":["VBG"],"bgColor":"#adf6a2","borderColor":"darken","fgColor":"black"},{"type":"VBD","labels":["VBD"],"bgColor":"#adf6a2","borderColor":"darken","fgColor":"black"},{"type":"PP","labels":["PP"],"bgColor":"lightblue","borderColor":"darken","fgColor":"black"},{"type":"PRP","labels":["PRP"],"bgColor":"#ccdaf6","borderColor":"darken","fgColor":"black"},{"type":"RB","labels":["RB"],"bgColor":"#fffda8","borderColor":"darken","fgColor":"black"},{"type":"RBR","labels":["RBR"],"bgColor":"#fffda8","borderColor":"darken","fgColor":"black"},{"type":"RBS","labels":["RBS"],"bgColor":"#fffda8","borderColor":"darken","fgColor":"black"},{"type":"WRB","labels":["WRB"],"bgColor":"#fffda8","borderColor":"darken","fgColor":"black"},{"type":"WP","labels":["WP"],"bgColor":"#ccdaf6","borderColor":"darken","fgColor":"black"},{"type":"ADVP","labels":["ADVP"],"bgColor":"lightgray","borderColor":"darken","fgColor":"black"},{"type":"SBAR","labels":["SBAR"],"bgColor":"lightgray","borderColor":"darken","fgColor":"black"},{"type":"ADJP","labels":["ADJP"],"bgColor":"lightgray","borderColor":"darken","fgColor":"black"},{"type":"JJ","labels":["JJ"],"bgColor":"#fffda8","borderColor":"darken","fgColor":"black"},{"type":"JJS","labels":["JJS"],"bgColor":"#fffda8","borderColor":"darken","fgColor":"black"},{"type":"JJR","labels":["JJR"],"bgColor":"#fffda8","borderColor":"darken","fgColor":"black"},{"type":"PRT","labels":["PRT"],"bgColor":"lightgray","borderColor":"darken","fgColor":"black"},{"type":"CONJP","labels":["CONJP"],"bgColor":"lightgray","borderColor":"darken","fgColor":"black"},{"type":"TO","labels":["TO"],"bgColor":"#ffe8be","borderColor":"darken","fgColor":"black"},{"type":"IN","labels":["IN"],"bgColor":"#ffe8be","borderColor":"darken","fgColor":"black"},{"type":"INTJ","labels":["INTJ"],"bgColor":"lightgray","borderColor":"darken","fgColor":"black"},{"type":"EX","labels":["EX"],"bgColor":"#e4cbf6","borderColor":"darken","fgColor":"black"},{"type":"FW","labels":["FW"],"bgColor":"#e4cbf6","borderColor":"darken","fgColor":"black"},{"type":"LS","labels":["LS"],"bgColor":"#e4cbf6","borderColor":"darken","fgColor":"black"},{"type":"POS","labels":["POS"],"bgColor":"#e4cbf6","borderColor":"darken","fgColor":"black"},{"type":"RP","labels":["RP"],"bgColor":"#e4cbf6","borderColor":"darken","fgColor":"black"},{"type":"SYM","labels":["SYM"],"bgColor":"#e4cbf6","borderColor":"darken","fgColor":"black"},{"type":"UH","labels":["UH"],"bgColor":"#e4cbf6","borderColor":"darken","fgColor":"black"},{"type":"LST","labels":["LST"],"bgColor":"lightgray","borderColor":"darken","fgColor":"black"},{"type":"MENTION","labels":["Mention"],"bgColor":"lightgray","borderColor":"darken","fgColor":"black"},{"type":"B-DNA","labels":["B-DNA","BDNA"],"bgColor":"#a4bced","borderColor":"darken","fgColor":"black"},{"type":"I-DNA","labels":["I-DNA","IDNA"],"bgColor":"#a4bced","borderColor":"darken","fgColor":"black"},{"type":"B-PROTEIN","labels":["B-PROTEIN","B-PRO","BPRO"],"bgColor":"#ffe8be","borderColor":"darken","fgColor":"black"},{"type":"I-PROTEIN","labels":["I-PROTEIN","I-PRO","IPRO"],"bgColor":"#ffe8be","borderColor":"darken","fgColor":"black"},{"type":"B-CELL_TYPE","labels":["B-CELL_TYPE","B-CELL","BCELL"],"bgColor":"#fffda8","borderColor":"darken","fgColor":"black"},{"type":"I-CELL_TYPE","labels":["B-CELL_TYPE","I-CELL","ICELL"],"bgColor":"#fffda8","borderColor":"darken","fgColor":"black"},{"type":"DNA","labels":["DNA"],"bgColor":"#a4bced","borderColor":"darken","fgColor":"black"},{"type":"PROTEIN","labels":["PROTEIN","PROT"],"bgColor":"#ffe8be","borderColor":"darken","fgColor":"black"},{"type":"CELL_TYPE","labels":["CELL-TYPE","CELL-T"],"bgColor":"#fffda8","borderColor":"darken","fgColor":"black"},{"type":"CELL_LINE","labels":["CELL_LINE","CELL-L"],"bgColor":"#fffda8","borderColor":"darken","fgColor":"black"}],"entity_attribute_types":[],"relation_types":[{"args":[{"role":"Arg1","targets":["Mention"]},{"role":"Arg2","targets":["Mention"]}],"arrowHead":"none","name":"Coref","labels":["Coreference","Coref"],"children":[],"unused":false,"dashArray":"3,3","attributes":[],"type":"Coreference","properties":{"symmetric":true,"transitive":true}}],"event_types":[{"borderColor":"darken","normalizations":[],"name":"Mention","arcs":[{"arrowHead":"none","dashArray":"3,3","labels":["Coref"],"type":"Coreference","targets":["Mention"]}],"labels":["Mention","Ment","M"],"unused":false,"bgColor":"#ffe000","attributes":[],"type":"Mention","children":[]}]}
    </textarea>


    <table align="center" class="table table-bordered table-striped responsive-utilities" align="center" style="width:800px;">
        <tr><th> Display </th></tr>
        <tr><td height="100px"><div id="preload" style="width:100px;height:100px;position:fixed;top:50%;left:50%;" ondblclick="$('#preload').hide();">
            <img src="${root}plugins/visualizations/nlp_brat/static/img/KUJoe.gif" /></div>
            <div id="instantbratdisplay"></div></td></tr>
        <tr><td><div id="instantdotdisplay" style="display:none">Loading ...</div></td></tr>
    </table>


    <table align="center" class="table table-bordered table-striped responsive-utilities" align="center" style="width:800px">
        <tr><th>Tool Output</th></tr>
        <tr><td>
        <textarea id="lappsjson">${lappsjson}</textarea>
        </td></tr>
    </table>



<!--
    <textarea>${hda}</textarea>
    <textarea>${hda.datatype}</textarea>
    <textarea>${hda.get_raw_data()}</textarea>
    <textarea>${hda.name}</textarea>
    <textarea>${hda.dataset}</textarea>
    <textarea>${hda.dataset.file_name}</textarea>
    <textarea>${hda.dataset.object_store}</textarea>
    <textarea>${hda.dataset.object_store.config}</textarea>
    <textarea>${os.getcwd()}</textarea>
-->

<footer>
    <hr />
    <!--
    <p style="text-align:center">
        Contacts:
        <br/>&nbsp; &nbsp; <a target="_blank" class="nolink" href="http://www.cs.brandeis.edu/~jamesp/"> James Pustejovsky</a>
        (<nonsense>jame</nonsense>sp@<nonsense>cs.</nonsense>brandeis.<nonsense></nonsense>edu)
    </p>
    -->
</footer>
<p style="text-align:center">Copyright &copy; 2017 The Language Applications Grid - All Rights Reserved</p>
</body>
</html>
