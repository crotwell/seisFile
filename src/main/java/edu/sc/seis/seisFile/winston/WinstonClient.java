package edu.sc.seis.seisFile.winston;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.zip.DataFormatException;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.mseed.Blockette1000;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;


public class WinstonClient {


    protected WinstonClient(String[] args) throws SeisFileException, FileNotFoundException, IOException {
        params = new QueryParams(args);

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p")) {
                winstonConfig.load(new BufferedReader(new FileReader(args[i + 1])));
            } else if (args[i].equals("-u")) {
                winstonConfig.put("winston.url", args[i + 1]);
            }
        }
        if (winstonConfig.getProperty("winston.url") == null) {
            winstonConfig.put("winston.url", "jdbc:mysql://localhost/?user=wwsuser");
            winstonConfig.put("winston.driver", WinstonUtil.MYSQL_DRIVER);
            winstonConfig.put("winston.prefix", "W");
        }
    }
    
    QueryParams params;
    
    Properties winstonConfig = new Properties();

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        WinstonClient client = new WinstonClient(args);
        client.readData();
    }

    public void readData() throws SeisFileException, SQLException, DataFormatException, FileNotFoundException, IOException {
        if (params.isPrintHelp()) {
            System.out.println(getHelp());
            return;
        } else if (params.isPrintVersion() || params.getNetwork() == null || params.getStation() == null || params.getLocation() == null || params.getChannel() == null) {
            System.out.println(BuildVersion.getDetailedVersion());
            return;
        }
        WinstonUtil winston = new WinstonUtil(getDbURL(), getUser(), getPassword(), winstonConfig.getProperty("winston.prefix"));
        WinstonSCNL channel = winston.createWinstonSCNL(params.getStation(), params.getChannel(), params.getNetwork(), params.getLocation());
        List<TraceBuf2> tbList = winston.extractData(channel, params.getBegin(), params.getEnd());
        for (TraceBuf2 traceBuf2 : tbList) {
            DataRecord mseed = traceBuf2.toMiniSeed();
            mseed.write(params.getDataOutputStream());
        }
        winston.close();
        params.getDataOutputStream().close();
    }
    
    String getDbURL() {
        return winstonConfig.getProperty("winston.url");
    }
    
    String getUser() throws MalformedURLException, SeisFileException {
        return getUrlQueryParam("user");
    }
    
    String getPassword() throws MalformedURLException, SeisFileException {
        return getUrlQueryParam("password");
    }
    
    String getUrlQueryParam(String name) throws MalformedURLException, SeisFileException {
        URL dburl = new URL(getDbURL());
        String[] urlParts = dburl.getQuery().split("\\&");
        for (int i = 0; i < urlParts.length; i++) {
            if (urlParts[i].startsWith(name+"=")) {
                return urlParts[i].substring((name+"=").length());
            }
        }
        throw new SeisFileException("Unable to find '"+name+"' query param in database url: "+getDbURL());
    }
    
    public String getHelp() {
        return "java "
        + WinstonClient.class.getName()
        + " [-p <winston.config file>][-u databaseURL][-n net][-s sta][-l loc][-c chan][-b yyyy-MM-ddTHH:mm:ss.SSS][-d seconds][-o outfile][-m maxpackets][--verbose][--version][--help]";
    }
   
}
