#!/usr/bin/env groovy

/*
 * A Groovy script that scans a Galaxy tool directory, parses all the
 * tool XML files found, and generates and HTML file showing the input
 * and output data formats accepted and produced by the tools.
 */
import groovy.xml.*

if (args.size() != 1) {
	usage()
	return
}

/*
boolean writeStylesheet = false
String stylesheet = 'style.css'
int filenameIndex = 0
if (args[0] == '-s' || args[0] == '--style') {
	if (args.size() != 3) {
		usage()
		return
	}
	writeStylesheet = true
	stylesheet = args[1]
	filenameIndex = 2
}
*/
File toolDir = new File(args[0])
if (!toolDir.exists()) {
	println "${toolDir.path} does not exist."
	return
}

if (!toolDir.isDirectory()) {
	println "${toolDir.path} is not a directory."
	return
}

String css = '''
	table {
		border-collapse: collapse;
	}

	table, th, td {
		border: 1px solid black;
	}

	th {
		color: white;
		background-color: #222;
	}
	th, td {
		padding: 5px;
		text-align: left;
	}
	tr.even {
		background-color: #f2f2f2
	}
	tr.odd { 
		backgroud-color: white; 
	}
	.heading {
		background-color: #808080;
		color: white;
	}
'''

List<Section> sections = []
XmlParser parser = new XmlParser()
toolDir.eachDirRecurse { dir ->
	Section section = new Section(name:dir.name)
	dir.eachFileMatch(~/.*\.xml$/) { file ->
		StringWriter writer = new StringWriter()
		Node xml = parser.parse(file)
		Tool tool = new Tool()
		tool.name = extractName(file)
		tool.input = extractInputs(xml)
		tool.output = xml.outputs.data.@format[0] ?: '-none-'
		section.tools << tool
	}
	// Only add the section if it contains more than one tool.
	if (section.tools.size() > 0) {
		sections << section
	}
}

StringWriter htmlWriter = new StringWriter()
MarkupBuilder html = new MarkupBuilder(htmlWriter)
html.html {
	head {
		title 'LAPPS Tool I/O Requirements'
		//link(rel:'stylesheet', type:'text/css', href:stylesheet)
		style {
			mkp.yield css
		}
	}
	body {
		table(align:'center') {
			tr {
				th 'Name' 
				th 'Input'
				th 'Output'
			}
			sections.sort{ it.name }.each { section ->
				tr {
					td(class:'heading', colspan:3, section.name)
				}
				int i = 0
				section.tools.sort{ it.name }.each { tool ->
					String style = ((++i%2)==0) ? 'even' : 'odd'
					tr(class:style)  {
						td tool.name
						td tool.input
						td tool.output
					}
				}
			}
		}
	}
}

//if (writeStylesheet) {
//	new File(stylesheet).text = css
//}
println htmlWriter.toString()
return

void usage() {
	println "Invalid parameters"
	println "Usage:"
	println "    groovy ListOutputs.groovy [-s <stylesheet>] <tool_dir>"
	println()
	println "If the -s option is provided the CSS stylesheet will be generated."
	println "By default the generated HTML expects a stylesheet named style.css"
	println "to be present in the same directory as the HTML file."
}

String extractName(File file) {
	//file.parentFile.name + '/' + file.name
	file.name
}

// Returns a comma delimited string of all the data formats accepted
// as input by this tool.
String extractInputs(Node tool) {
	List list = []
	// For all input <param> elements of @type 'data' add the @format to the list.
	tool.inputs.param.findAll { it.@type == 'data' }.each { list << it.@format }
	if (list.size() == 0) {
		return '-none-'
	}
	return list.join(",")	
}

// The data model (sections and tools) parsed from the tool XML files.
class Section {
	String name
	List tools = []
}

class Tool {
	String name
	String input
	String output
}