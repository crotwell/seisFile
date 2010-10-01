package edu.sc.seis.seisFile.sac;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;


public class ListHeader {
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        PrintWriter out = new PrintWriter(System.out, true);
        out.println();
        for (int i = 0; i < args.length; i++) {
            String filename = args[i];
            SacTimeSeries sac = new SacTimeSeries(filename);
            out.println(filename);
            String dashLine = "";
            for (int j = 0; j < filename.length(); j++) {
                dashLine += "-";
            }
            out.println(dashLine);
            out.println();
            sac.printHeader(out);
        }
    }
}
