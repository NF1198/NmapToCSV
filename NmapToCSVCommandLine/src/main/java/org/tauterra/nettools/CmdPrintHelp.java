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
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Nicholas Folse
 */
public class CmdPrintHelp implements SubCommand {

    private CmdPrintHelp() {
    }

    private static final CmdPrintHelp INSTANCE = new CmdPrintHelp();

    public static CmdPrintHelp getInstance() {
        return INSTANCE;
    }

    private final Options options;

    {
        options = null;
    }

    @Override
    public Options getOptions() {
        return options;
    }

    @Override
    public void process(String cmd, String[] args) throws ParseException {

        PrintStream out = System.out;

        if (args.length == 0) {
            CmdPrintUsage.getInstance().process(cmd, null);
            return;
        }
        
        String subCmd = args[0];
        
        CommandMap.PrintUsage(subCmd);
    }

}
