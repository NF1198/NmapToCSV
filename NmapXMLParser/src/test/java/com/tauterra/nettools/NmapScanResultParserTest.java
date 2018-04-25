/*
 * Copyright 2018 Nicholas Folse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tauterra.nettools;

import com.tauterra.nettools.nmap.model.NmapHostElement;
import com.tauterra.nettools.nmap.model.NmapScanResult;
import java.io.InputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nicholas Folse
 */
public class NmapScanResultParserTest {

    public NmapScanResultParserTest() {
    }

    /**
     * Test of parse method, of class NmapScanResultParser.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("Testing Nmap Scan Result Parser");

        XMLInputFactory inputFactory = XMLInputFactory.newFactory();
        InputStream in = NmapScanResultParserTest.class.getResourceAsStream("/test_scan.xml");
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

        NmapScanResultParser instance = new NmapScanResultParser();
        NmapScanResult result = instance.parse(eventReader);

        System.out.println(result.getHosts().size());

        NmapHostElement host = result.getHosts().get(0);
        System.out.println(host.getAddresses().get(0).getAddr());
        host.getPorts().forEach((port) -> {
            System.out.println(port.getProtocol() + " : " + port.getPortid() + " : " + port.getService().getName());
        });
        host.getHostnames().forEach((hn) -> {
            System.out.println(hn.getName());
        });

    }

}
