package org.tauterra.nettools;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class NmapToCSVCommandLine implements SubCommand {

    public static final String VERSION = isNull(NmapToCSVCommandLine.class.getPackage().getImplementationVersion(), "0.01");
    public static final String APP_NAME = "namap-tool";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new NmapToCSVCommandLine().process(APP_NAME, args);
        } catch (ParseException e) {
            System.out.println("Error parsing options");
            System.out.println(e);
        }
    }

    public static String isNull(String any, String def) {
        return (any != null) ? any : def;
    }

    @Override
    public void process(String cmd, String[] args) throws ParseException {
        if (args.length == 0) {
            CmdPrintUsage.getInstance().process(APP_NAME, null);
            return;
        }
        final String subCommand = args[0];
        System.err.println(subCommand);
        final String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);
        SubCommand command = CommandMap.getCommands().getOrDefault(subCommand, CmdPrintUsage.getInstance());
        command.process(subCommand, subArgs);
    }

    @Override
    public Options getOptions() {
        return null;
    }
}
