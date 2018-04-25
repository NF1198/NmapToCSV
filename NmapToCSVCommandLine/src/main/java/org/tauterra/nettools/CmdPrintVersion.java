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

import org.apache.commons.cli.Options;
import static org.tauterra.nettools.NmapToCSVCommandLine.APP_NAME;
import static org.tauterra.nettools.NmapToCSVCommandLine.VERSION;

/**
 *
 * @author Nicholas Folse
 */
public class CmdPrintVersion implements SubCommand {

    private CmdPrintVersion() {
    }

    private static final CmdPrintVersion INSTANCE = new CmdPrintVersion();

    public static CmdPrintVersion getInstance() {
        return INSTANCE;
    }

    @Override
    public Options getOptions() {
        return null;
    }

    @Override
    public void process(String cmd, String[] args) {
        System.out.println(APP_NAME + " version: " + VERSION);
    }

}
