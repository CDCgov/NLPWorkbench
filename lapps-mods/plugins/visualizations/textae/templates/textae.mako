<!DOCTYPE HTML>

<%
    import os
    root = h.url_for( '/' )
    json = hda.get_raw_data()
%>

<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
    <title>${hda.name} | ${visualization_name}</title>
    ${h.stylesheet_link('http://textae.pubannotation.org/lib/css/textae.min.css')}
    ${h.javascript_link('http://textae.pubannotation.org/lib/textae.min.js')}

    <script>
        $(document).ready(function() {
            $('#save').on('click', function(e){
                var text = $('#textae').html()
                $('#output').text(text)
                $('#output_div').show()
            })
            $('#hide').on('click', function (e) {
                $('#output_div').hide()
            })
        })
    </script>
</head>


<body>
    <h1>TextAE</h1>
    <p>The annotation editor from <a targt="_blank" href="http://textae.pubannotation.org">PubAnnotation</a></p>
    <table>
        <tr>
            <th>Name</th>
            <td>${hda.name}</td>
        </tr>
        <tr>
            <th>Type</th>
            <td>${hda.datatype}</td>
        </tr>
        <tr>
            <th>Dataset</th>
            <td>${hda.dataset}</td>
        </tr>
    </table>
    <div id="textae" class="textae-editor" mode="edit">${json}</div>
    <button id="save">Save</button>

    <div id="output_div" style="display: none">
        <p id="output">Yay! I'm visible.</p>
        <button id="hide">Hide</button>
    </div>
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
    <p style="text-align:center">Copyright &copy; 2018 The Language Applications Grid - All Rights Reserved</p>
</footer>
</body>
</html>
