package edu.sc.seis.seisFile.sac;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;


public class ListHeader {
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        PrintWriter out = new PrintWriter(System.out, true);
        for (int i = 0; i < args.length; i++) {
            out.println();
            String filename = args[i];
            File f = new File(filename);
            out.println(filename);
            String dashLine = "";
            for (int j = 0; j < filename.length(); j++) {
                dashLine += "-";
            }
            out.println(dashLine);
            out.println();
            if (f.exists() && f.isFile()) {
                SacTimeSeries sac = new SacTimeSeries(filename);
                sac.printHeader(out);
            } else {
                out.println("Cannot load, exists="+f.exists()+" isFile="+f.isFile());
            }
        }
    }
}
