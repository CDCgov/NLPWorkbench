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

import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.lif.Container

/**
 * Not a test, but this class generates the LIF containers from the XCAS and XMI files to be used
 * for round trip testing.
 */
class GenerateContainers {

    void run() {
        UimaParser parser = new UimaParser()

        // Save the XCAS as LIF
        URL url = this.class.getResource('/ctakes.xml')
        Container container = parser.parse(url)
        save(container, 'target/ctakes-xcas.lif')

        // Save the XMI as LIF
        url = this.class.getResource('/ctakes.xmi')
        container = parser.parse(url)
        save(container, 'target/ctakes-xmi.lif')
    }

    void save(Container container, String path) {
        Data data = new Data(Discriminators.Uri.LIF, container)
        new File(path).text = data.asPrettyJson()
    }

    static void main(String[] args) {
        new GenerateContainers().run()
    }
}
