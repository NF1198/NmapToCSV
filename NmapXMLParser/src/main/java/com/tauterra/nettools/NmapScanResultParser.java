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

import com.tauterra.nettools.nmap.model.NmapAddressElement;
import com.tauterra.nettools.nmap.model.NmapHostElement;
import com.tauterra.nettools.nmap.model.NmapHostname;
import com.tauterra.nettools.nmap.model.NmapHostnames;
import com.tauterra.nettools.nmap.model.NmapOSClass;
import com.tauterra.nettools.nmap.model.NmapOSElement;
import com.tauterra.nettools.nmap.model.NmapOSMatch;
import com.tauterra.nettools.nmap.model.NmapPortElement;
import com.tauterra.nettools.nmap.model.NmapPortState;
import com.tauterra.nettools.nmap.model.NmapPortUsed;
import com.tauterra.nettools.nmap.model.NmapPortsElement;
import com.tauterra.nettools.nmap.model.NmapScanResult;
import com.tauterra.nettools.nmap.model.NmapService;
import com.tauterra.nettools.nmap.model.NmapUptime;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Nicholas Folse
 */
public class NmapScanResultParser {

    private final StAXObjectBuilder<NmapScanResult> scanResultParser;

    public NmapScanResultParser() {

        final StAXObjectBuilder<NmapUptime> uptimeParser
                = new StAXObjectBuilder<>("uptime", () -> new NmapUptime());
        uptimeParser.addAttributeHandler("lastboot", (obj, val) -> obj.setLastboot(val));
        uptimeParser.addAttributeHandler("seconds", (obj, val) -> obj.setSeconds(parseLong(val, 0L)));

        final StAXObjectBuilder<NmapOSClass> osClassParser
                = new StAXObjectBuilder<>("osclass", () -> new NmapOSClass());
        osClassParser.addAttributeHandler("type", (obj, val) -> obj.setType(val));
        osClassParser.addAttributeHandler("osfamily", (obj, val) -> obj.setOsfamily(val));
        osClassParser.addAttributeHandler("vendor", (obj, val) -> obj.setVendor(val));
        osClassParser.addAttributeHandler("osgen", (obj, val) -> obj.setOsgen(val));
        osClassParser.addAttributeHandler("accuracy", (obj, val) -> obj.setAccuracy(parseInteger(val, 0)));

        final StAXObjectBuilder<NmapOSMatch> osMatchParser
                = new StAXObjectBuilder<>("osmatch", () -> new NmapOSMatch());
        osMatchParser.addAttributeHandler("line", (obj, val) -> obj.setLine(parseLong(val, 0L)));
        osMatchParser.addAttributeHandler("name", (obj, val) -> obj.setName(val));
        osMatchParser.addAttributeHandler("accuracy", (obj, val) -> obj.setAccuracy(parseInteger(val, 0)));
        osMatchParser.addHandler(osClassParser, (obj, val) -> obj.setOsclass(val));

        final StAXObjectBuilder<NmapPortUsed> portUsedParser
                = new StAXObjectBuilder<>("portused", () -> new NmapPortUsed());
        portUsedParser.addAttributeHandler("state", (obj, val) -> obj.setState(val));
        portUsedParser.addAttributeHandler("portid", (obj, val) -> obj.setPortID(parseInteger(val, 0)));
        portUsedParser.addAttributeHandler("proto", (obj, val) -> obj.setProto(val));

        final StAXObjectBuilder<NmapOSElement> osElementParser
                = new StAXObjectBuilder<>("os", () -> new NmapOSElement());
        osElementParser.addHandler(portUsedParser, (obj, val) -> obj.getPortsUsed().add(val));
        osElementParser.addHandler(osMatchParser, (obj, val) -> obj.getOsMatches().add(val));

        final StAXObjectBuilder<NmapService> serviceParser
                = new StAXObjectBuilder<>("service", () -> new NmapService());
        serviceParser.addAttributeHandler("extrainfo", (obj, val) -> obj.setExtrainfo(val));
        serviceParser.addAttributeHandler("product", (obj, val) -> obj.setProduct(val));
        serviceParser.addAttributeHandler("method", (obj, val) -> obj.setMethod(val));
        serviceParser.addAttributeHandler("version", (obj, val) -> obj.setVersion(val));
        serviceParser.addAttributeHandler("name", (obj, val) -> obj.setName(val));
        serviceParser.addAttributeHandler("conf", (obj, val) -> obj.setConf(parseInteger(val, 0)));

        final StAXObjectBuilder<NmapPortState> portStateParser
                = new StAXObjectBuilder<>("state", () -> new NmapPortState());
        portStateParser.addAttributeHandler("reason", (obj, val) -> obj.setReason(val));
        portStateParser.addAttributeHandler("state", (obj, val) -> obj.setState(val));
        portStateParser.addAttributeHandler("reason_ttl", (obj, val) -> obj.setReason_ttl(val));

        final StAXObjectBuilder<NmapPortElement> portParser
                = new StAXObjectBuilder<>("port", () -> new NmapPortElement());
        portParser.addAttributeHandler("protocol", (obj, val) -> obj.setProtocol(val));
        portParser.addAttributeHandler("portid", (obj, val) -> obj.setPortid(parseInteger(val, 0)));
        portParser.addHandler(portStateParser, (obj, val) -> obj.setState(val));
        portParser.addHandler(serviceParser, (obj, val) -> obj.setService(val));

        final StAXObjectBuilder<NmapPortsElement> portsParser
                = new StAXObjectBuilder<>("ports", () -> new NmapPortsElement());
        portsParser.addHandler(portParser, (obj, val) -> obj.getPorts().add(val));

        final StAXObjectBuilder<NmapAddressElement> addressParser
                = new StAXObjectBuilder<>("address", () -> new NmapAddressElement());
        addressParser.addAttributeHandler("addrtype", (obj, val) -> obj.setAddrtype(val));
        addressParser.addAttributeHandler("vendor", (obj, val) -> obj.setVendor(val));
        addressParser.addAttributeHandler("addr", (obj, val) -> obj.setAddr(val));

        final StAXObjectBuilder<NmapHostname> hostnameParser
                = new StAXObjectBuilder<>("hostname", () -> new NmapHostname());
        hostnameParser.addAttributeHandler("type", (obj, val) -> obj.setType(val));
        hostnameParser.addAttributeHandler("name", (obj, val) -> obj.setName(val));

        final StAXObjectBuilder<NmapHostnames> hostnamesParser
                = new StAXObjectBuilder<>("hostnames", () -> new NmapHostnames());
        hostnamesParser.addHandler(hostnameParser, (obj, val) -> obj.getHostnames().add(val));

        final StAXObjectBuilder<NmapHostElement> hostParser
                = new StAXObjectBuilder<>("host", () -> new NmapHostElement());
        hostParser.addAttributeHandler("comment", (obj, val) -> obj.setComment(val));
        hostParser.addHandler(addressParser, (obj, val) -> obj.getAddresses().add(val));
        hostParser.addHandler(portsParser, (obj, val) -> obj.getPorts().addAll(val.getPorts()));
        hostParser.addHandler(osElementParser, (obj, val) -> obj.setOs(val));
        hostParser.addHandler(hostnamesParser, (obj, val) -> obj.getHostnames().addAll(val.getHostnames()));

        final StAXObjectBuilder<NmapScanResult> scanResParser
                = new StAXObjectBuilder<>(null, () -> new NmapScanResult());
        scanResParser.addHandler(hostParser, (obj, val) -> obj.getHosts().add(val));

        this.scanResultParser = scanResParser;
    }

    public NmapScanResult parse(XMLEventReader eventReader) throws XMLStreamException, InstantiationException, IllegalAccessException {
        return this.scanResultParser.parseDocument(eventReader);
    }

    private static Long parseLong(String value, Long def) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static Integer parseInteger(String value, Integer def) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
