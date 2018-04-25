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
package org.tauterra.nettools;

import com.tauterra.nettools.NmapScanResultParser;
import com.tauterra.nettools.nmap.model.NmapAddressElement;
import com.tauterra.nettools.nmap.model.NmapHostElement;
import com.tauterra.nettools.nmap.model.NmapScanResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import static java.lang.System.err;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Nicholas Folse
 */
public class NmapExportHostsServices implements SubCommand {

    private final Options options;

    public NmapExportHostsServices() {

        options = new Options();
        options.addOption(Option.builder("i")
                .longOpt("input")
                .hasArg(true)
                .required(false)
                .build()
        );
        options.addOption(Option.builder("D")
                .longOpt("directory")
                .required(false)
                .hasArg(true)
                .build()
        );
        options.addOption("v", "verbose", false, "verbose logging");
    }

    @Override
    public void process(String subCommand, String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmdline = parser.parse(options, args);

        boolean verbose = cmdline.hasOption("v");
        final List<File> inputFiles = new ArrayList<>();
        File outFile = null;

        String[] iFiles = cmdline.getOptionValues("i");

        if (iFiles != null) {
            for (String fname : iFiles) {
                File file = new File(fname);
                if (!file.exists()) {
                    err.println("Specified file doesn't exist <" + fname + ">");
                    return;
                }
            }
        }

        String[] iDirectories = cmdline.getOptionValues("D");
        if (iDirectories != null) {
            for (String dname : iDirectories) {
                File dir = new File(dname);
                if (!dir.exists()) {
                    err.println("Specified direcotry doesn't exist <" + dname + ">");
                    return;
                }
                if (!dir.isDirectory()) {
                    err.println("Specified directory isn't a directory <" + dname + ">");
                    return;
                }
                inputFiles.addAll(Arrays.asList(dir.listFiles((d, name) -> name.toLowerCase().endsWith(".xml"))));
            }
        }

        NmapScanResultParser nmapResultParser = new NmapScanResultParser();
        final Map<NmapAddressElement, NmapHostElement> hosts = new TreeMap<>();
        for (File f : inputFiles) {
            if (verbose) {
                System.err.println("processing file: " + f.getPath().toString());
            }
            try {
                XMLInputFactory inputFactory = XMLInputFactory.newFactory();
                InputStream in = new FileInputStream(f);
                XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
                NmapScanResult result = nmapResultParser.parse(eventReader);
                result.getHosts().forEach(host -> {
                    host.getAddresses().forEach(addr -> {
                        if ("ipv4".equals(addr.getAddrtype())) {
                            hosts.put(addr, host);
                        }
                    });
                });
            } catch (FileNotFoundException | XMLStreamException | InstantiationException | IllegalAccessException ex) {
                System.err.println("Error parsing XML document: " + f.getName());
            }
        }

        StringBuilder sb = new StringBuilder();
        printHostHeader(sb);
        hosts.entrySet().forEach((t) -> {
            printHostServices(sb, t.getKey(), t.getValue());
        });
        System.out.println(sb.toString());
    }

    private static final String DELIM = ",";

    private static void printRow(final StringBuilder sb, final String delimeter, final String... cols) {
        for (int idx = 0; idx < cols.length; idx++) {
            sb.append(cols[idx]);
            if (idx < cols.length - 1) {
                sb.append(delimeter);
            }
        }
        sb.append("\n");
    }

    private final void printHostHeader(StringBuilder sb) {
        printRow(sb, DELIM, "IPv4", "hostname", "service", "port", "proto", "state", "product");
    }

    private final void printHostServices(StringBuilder sb, NmapAddressElement addr, NmapHostElement host) {
        String ip4addr = addr.getAddr();
        String hostname = (host.getHostnames().isEmpty()) ? "" : host.getHostnames().get(0).getName();
        host.getPorts().forEach((port) -> {
            final String proto = port.getProtocol();
            final String portID = Integer.toString(port.getPortid());
            final String state = port.getState().getState();
            final String serviceProduct = port.getService().getProduct();
            final String serviceName = port.getService().getName();
            printRow(sb, DELIM, ip4addr, hostname, serviceName, portID, proto, state, serviceProduct);
        });
    }

    @Override
    public Options getOptions() {
        return this.options;
    }

}
