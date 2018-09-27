#!/usr/bin/env groovy

import groovy.xml.*

def parser = new XmlParser()
File directory = new File('.')
directory.eachFileMatch(~/.*\.xml$/) { File file ->
	def tool = parser.parse(file)
	String service = tool.command.text().split()[1]
	tool.command[0].value = "lsd \$__tool_directory__/invoke.lsd $service \$input \$output"
	tool.command[0].attributes().remove('interpreter')
	//tool.inputs[0].appendNode 'param', [name:'username', type:'text', value:'tester', label:'Username']
	//tool.inputs[0].appendNode 'param' , [name:'password', type:'text', value:'tester', label:'Password']
	
	def writer = new PrintWriter(new FileWriter(new File('/tmp', file.name)))
	def printer = new XmlNodePrinter(writer, "\t")
	printer.preserveWhitespace = true
	printer.print(tool)
	println "Wrote ${file.path}"
}
