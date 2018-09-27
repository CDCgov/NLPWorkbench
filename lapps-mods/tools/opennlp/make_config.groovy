/*
 * This script generates the XML config files required by Galaxy. It also
 * generates the <section> element that can be used in the tool_conf.xml file.
 */
 
import groovy.xml.MarkupBuilder
import groovy.json.JsonOutput
import static org.lappsgrid.discriminator.Discriminators.Uri

// Constructs the full URL for a Brandeis service.
String getUrl(String provider, String name) {
	return "http://eldrad.cs-i.brandeis.edu:8080/service_manager/invoker/brandeis_eldrad_grid_1:${provider}.$name"
}

// The service names. These are the names that will be appended to the grid id to 
// obtain the full service id.
def services = [ 'tokenizer', 'postagger', 'namedentityrecognizer', 'splitter', 'parser' ]

// The list of tools that have been registered. This is used to generate the <section>
// element for the tool_conf.xml file.
tools = []


generate('opennlp', 'OpenNLP', services)
generate('stanfordnlp', 'Stanford', services)

// Generate the <section> element and save it to a file.
StringWriter writer = new StringWriter()
def xml = new MarkupBuilder(writer)
xml.section(id:'brandeis.tools', name:'Brandeis Tools') {
	tools.each { t ->
		tool(file:"lapps_brandeis/${t}")
	}
}
new File('section.xml').text = writer.toString()
println "Wrote the tool_conf.xml section."
println "Done"
return

// Generates the tool XML files for each of the services.
void generate(String provider, String name, List services) {
	println "Generating Galaxy configuration files for $provider"
	services.each { service ->
		String url = getUrl(provider, service)
		def client = new ServiceClient(url, "tester", "tester")
		println "Fetching metadata from $url"
		String json = client.getMetadata()
		Data d = Serializer.parse(json, Data)
		if (d.discriminator == Uri.ERROR) {
			println "Can not fetch metadata from $url"
		}
		else {
			Map metadata
			if (d.payload instanceof String) {
				metadata = Serializer.parse(d.payload, HashMap)			
			}
			else if (d.payload instanceof Map) {
				metadata = d.payload
			}
			else {
				throw new IOException("Unsupported payload.")			
			}
			String localName = metadata.name.tokenize('.')[-1]
			String fullName = "${name} ${localName}"
			String id = "${provider}.${service}"
			StringWriter writer = new StringWriter()
			def xml = new MarkupBuilder(writer)
			xml.tool(id:id, name:fullName, version:'2.0.0') {
				description metadata.description
				command(interpreter:'lsd', "invoke.lsd ${id} \$input \$output")
				inputs {
					param(name:'input', type:'data', format:'lif', label:'input')
				}
				outputs {
					data(name:'output', format:'lif')
				}
			}
			String filename = "${id}.xml"
			File file = new File(filename)
			file.text = writer.toString()
			println "Wrote ${file.path}"
			tools.add(filename)
			/*
			println "Name        : ${metadata.name}"
			println "Description : ${metadata.description}"
			println "Version     : ${metadata.version}"
			*/
		}
	}
}

/*
<tool id='gate.coref_2.0.0' name='GATE Coreferencer v2.0.0' version='2.0.0'>
  <description>Coreferencer from GATE</description>
  <command interpreter='lsd'>invoke.lsd gate.coref_2.0.0 $input $output</command>
  <inputs>
    <param name='input' type='data' format='gate' label='input' />
  </inputs>
  <outputs>
    <data name='output' format='gate' />
  </outputs>
  <help>Coreferencer from GATE</help>
</tool>
*/
