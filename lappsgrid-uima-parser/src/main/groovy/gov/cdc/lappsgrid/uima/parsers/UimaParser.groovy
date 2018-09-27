/*-
 * Copyright 2018 The Centers for Disease Control and Prevention
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package gov.cdc.lappsgrid.uima.parsers

import gov.cdc.lappsgrid.uima.parsers.error.ParseError
import groovy.xml.QName
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

/**
 * <p>The UimaParser is able to parse the XCAS and XMI formats to create a LIF Container object.</p>
 * <p>
 * The XCAS/XMI is read into a string and parsed with the Groovy XML parser.  The process(Node)
 * method then walks the tree of Nodes creating LIF Annotation objects as it goes.
 * </p>
 */
class UimaParser {
    public static final String UTF8 = 'UTF-8'

    XmlParser parser
    Container container
    View view
    int id
    Map<String,String> namespaces
    Deque<Annotation> stack

    public UimaParser() {
        parser = new XmlParser()
        stack = new ArrayDeque<>()
        namespaces = new HashMap<String,String>()
    }

    Container parse(File file) {
        return parse(file, (UTF8))
    }

    Container parse(File file, String charset) {
        return parse(file.getText(charset))
    }

    Container parse(URL url) {
        return parse(url, UTF8)
    }

    Container parse(URL url, String charset) {
        return parse(url.getText(charset))
    }

    Container parse(InputStream stream) {
        return parse(stream, UTF8)
    }

    Container parse(InputStream stream, String charset) {
        return parse(stream.getText(charset))
    }

    Container parse(String xml) {
        id = 0
        container = new Container()
        view = container.newView()
        Node node = parser.parseText(xml)
        process(node)
        if (namespaces.size() > 0) {
            container.metadata.namespaces = namespaces
        }
        Container result = container
        view = null
        container = null
        return result
    }

    private void process(Node node) {
        String name = getName(node)
        if (name == 'nodeTags') {
            Annotation annotation = stack.peek()
            annotation.addFeature('nodeTags', node.text())
            return
        }

        Annotation a = view.newAnnotation("a${id++}", name)
        List<Node> children = node.children()

        node.attributes().each { k,v ->
            if (k == 'begin') {
                a.start = v as int
            }
            else if (k == 'end') {
                a.end = v as int
            }
            else if (k == 'sofaString') {
                container.text = v
            }
            else if (k instanceof QName) {
                QName qName = (QName) k
                a.addFeature(qName.qualifiedName, v)
            }
            else {
                a.addFeature(k, v)
            }
        }

        if (name == "uima.tcas.DocumentAnnotation") {
            container.language = node.attribute("language").toString();
        }
        else if (name == 'uima.cas.FSArray' || name == 'uima.cas.StringArray') {
            // Add the array elements as a feature of the parent
            List elements = children.inject([]) {list, it -> list.add(it.text()); return list }
            a.addFeature('i', elements)
        }
        else if (children.size() > 0) {
            stack.push(a)
            children.each { process it }
            stack.pop()
        }
    }

    private void process(String value) {
        throw new ParseError("Processing unexpected string value: " + value)
    }

    private String getName(Node node) {
        def name = node.name()
        if (name instanceof QName) {
            QName qName = (QName) name
            String prefix = qName.prefix
            String uri = namespaces[prefix]
            if (uri == null) {
                namespaces[prefix] = qName.namespaceURI
            }
            return qName.qualifiedName
        }
        return name.toString()
    }

}
