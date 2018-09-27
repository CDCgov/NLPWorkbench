import groovy.xml.*;
import groovy.util.*;

File root = new File("./")

Deque<File> stack = new ArrayDeque();
stack.push(root)
while (!stack.isEmpty()) {
    File entry = stack.pop();
    if (entry.isDirectory()) {
        for (File child : entry.listFiles()) {
            stack.push(child)
        }
    }
    else if (entry.getName() == 'pom.xml') {
        parse(entry)
    }
}
println "Done"
return

void parse(File pomFile) {
    XmlParser parser = new XmlParser()
    Node project = parser.parse(pomFile)
    boolean b1 = parse(project, 'ctakes-lvg', 'ctakes-lvg-file')
    boolean b2 = parse(project, 'ctakes-dictionary-lookup-fast')
    if (b1 || b1) {
        println "Fixing ${pomFile.path}"
        new File(pomFile.parentFile, "pom-bak.xml").text = pomFile.text
        pomFile.text = XmlUtil.serialize(project)
    }
}

boolean parse(Node project, String term) {
    Node node = project.dependencies.dependency.find { it.artifactId[0].text() == term}
    if (node) {
        Node groupId = node.groupId[0]
        groupId.value = 'gov.cdc.lappsgrid.uima.ctakes'
//        Node version = node.version[0]
//        version.value = '1.0.0-SNAPSHOT'
        return true
    }
    return false;
}

boolean parse(Node project, String term, String replacement) {
    Node node = project.dependencies.dependency.find { it.artifactId[0].text() == term}
    if (node) {
        Node groupId = node.groupId[0]
        groupId.value = 'gov.cdc.lappsgrid.uima.ctakes'
        Node artifactId = node.artifactId[0]
        artifactId.value = replacement
//        Node version = node.version[0]
//        version.value = '1.0.0-SNAPSHOT'
        return true
//        println XmlUtil.serialize(project)
    }
    return false
}
