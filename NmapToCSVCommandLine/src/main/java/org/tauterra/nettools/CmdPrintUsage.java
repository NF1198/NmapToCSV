/*
 * Copyright 2017 tauTerra, LLC.
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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.commonmark.node.Code;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentNodeRendererContext;
import org.commonmark.renderer.text.TextContentRenderer;
import org.commonmark.renderer.text.TextContentWriter;
import static org.tauterra.nettools.NmapToCSVCommandLine.*;

/**
 *
 * @author Nicholas Folse
 */
public class CmdPrintUsage implements SubCommand {

    private CmdPrintUsage() {
    }

    private static final CmdPrintUsage INSTANCE = new CmdPrintUsage();

    public static CmdPrintUsage getInstance() {
        return INSTANCE;
    }

    private static String getMessage() {
        String usageMessage = "nmaptocsv -D <directory> -i <filename>";

        final String header = " arguments:\n";
        final String footer = "";
        Map<String, SubCommand> cmdmap = CommandMap.getCommands();
        Set<String> cmdSet = cmdmap.keySet().stream().sorted().collect(Collectors.toSet());
        HelpFormatter fmt = new HelpFormatter();
        StringWriter swriter = new StringWriter();
        PrintWriter writer = new PrintWriter(swriter);
        for (String cmd : cmdSet) {
            Options opts = cmdmap.get(cmd).getOptions();
            if (opts != null) {
                String h = opts.getOptions().size() > 0 ? cmd + header : "(no options)";
                fmt.printHelp(writer, 200, cmd, h, opts, 0, 2, footer, true);
            }
        }
        String fixedFmt = swriter.toString()
                .replaceAll("usage: ", "## ")
                .replaceAll("\\[", "&#91;")
                .replaceAll("\\]", "&#93;")
                .replaceAll("\\<", "&lt;")
                .replaceAll("\\>", "&gt;");
        return MessageFormat.format(usageMessage + "\n" + fixedFmt, new Object[]{APP_NAME, VERSION});

    }

    private final Options options;

    {
        options = new Options();
        options.addOption("f", "format", true, "output format [text, html]");
    }

    @Override
    public Options getOptions() {
        return options;
    }

    @Override
    public void process(String cmd, String[] args) throws ParseException {

        PrintStream out = System.out;

        CommandLineParser parser = new DefaultParser();
        CommandLine cmdline = null;
        try {
            cmdline = parser.parse(options, args);
        } catch (ParseException e) {
            //...
        }

        Parser mdParser = Parser.builder().build();
        Node document = mdParser.parse(getMessage());

        String outFmt = (cmdline != null) ? cmdline.getOptionValue("f", "text") : "text";

        switch (outFmt) {
            case "html":
                HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();
                out.print(htmlRenderer.render(document));
                break;
            case "text":
                TextContentRenderer textRenderer = TextContentRenderer.builder()
                        .nodeRendererFactory((TextContentNodeRendererContext tcnrc) -> new HeadingRenderer(tcnrc))
                        .build();
                out.println(textRenderer.render(document));
            default:
                break;
        }

    }

    class HeadingRenderer implements NodeRenderer {

        private final TextContentWriter text;

        HeadingRenderer(TextContentNodeRendererContext context) {
            this.text = context.getWriter();
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            // Return the node types we want to use this renderer for.
            return Collections.<Class<? extends Node>>singleton(Heading.class);
        }

        private void ExtractNodeText(Node n, StringBuilder sb) {
            if (n == null) {
                return;
            }
            Node node = n;
            while (node != null) {
                if (node instanceof Text) {
                    sb.append(((Text) node).getLiteral());
                }
                if (node instanceof Code) {
                    sb.append(((Code) node).getLiteral());
                }
                node = node.getNext();
            }
            if (n.getFirstChild() != null) {
                ExtractNodeText(n.getNext(), sb);
            }
        }

        @Override
        public void render(Node node) {
            // We only handle one type as per getNodeTypes, so we can just cast it here.
            Heading heading = (Heading) node;
            StringBuilder sb = new StringBuilder();
            ExtractNodeText(heading.getFirstChild(), sb);
            String headingText = sb.toString();
            text.write("\n");
//            text.write("[ ");
            text.write((headingText != null) ? headingText : "");
//            text.write(" ]");
            text.line();
            text.write("======================================");
            text.line();
        }
    }
}
