package edu.sc.seis.seisFile.client;

import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LocCodeConsumer implements CommandLine.IParameterConsumer {
    @Override
    public void consumeParameters(Stack<String> args, CommandLine.Model.ArgSpec argSpec, CommandLine.Model.CommandSpec cmdSpec) {
        List<String> list = argSpec.getValue();
        if (list == null) {
            list = new ArrayList<>();
        }
        if (args.isEmpty()) {
            throw new CommandLine.ParameterException(cmdSpec.commandLine(),
                    "Error: option '--location' requires a parameter");
        }
        while ( !args.isEmpty()) {
            String peek = args.peek();
            if (!peek.startsWith("-") || peek.equals("--") || peek.startsWith("--,")) {
                String arg = args.pop();
                String[] itemList = arg.split(argSpec.splitRegex());
                for (String item : itemList) {
                    list.add(item);
                }
            } else {
                break;
            }
        }
        if (list.size() != 0) {
            argSpec.setValue(list);
        }
    }
}
