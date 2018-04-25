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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import static org.tauterra.nettools.NmapToCSVCommandLine.APP_NAME;

/**
 *
 * @author Nicholas Folse
 */
public class CommandMap {

    private final Map<String, SubCommand> CMDMAP = genCommands();

    private Map<String, SubCommand> genCommands() {
        Map<String, SubCommand> cmds = new HashMap<>();
        cmds.put("usage", CmdPrintUsage.getInstance());
        cmds.put("help", CmdPrintHelp.getInstance());
        cmds.put("version", CmdPrintVersion.getInstance());
        cmds.put("exportHosts", new NmapExportHostsServices());
        return cmds;
    }

    private static final CommandMap INSTANCE = new CommandMap();

    public static Map<String, SubCommand> getCommands() {
        return INSTANCE.CMDMAP;
    }
    
    public static void PrintError(String error, String subCommand) {
        System.out.println("Error: " + error);
        PrintUsage(subCommand);
    }

    public static void PrintUsage(String subCommand) {
        HelpFormatter formatter = new HelpFormatter();
        Options opts = null;
        SubCommand cmd = getCommands().getOrDefault(subCommand, null);
        if (cmd != null) {
            opts = cmd.getOptions();
        }
        if (opts != null) {
            formatter.printHelp(APP_NAME + " " + subCommand, opts, true);
        } else {
            String subCommands = "["
                    + CommandMap.getCommands().keySet().stream().sorted().collect(Collectors.joining(", "))
                    + "]";
            System.out.println("specify sub-command: " + subCommands);
        }
    }
}
