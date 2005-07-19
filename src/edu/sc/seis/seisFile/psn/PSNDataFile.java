package edu.sc.seis.seisFile.psn;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PSNDataFile.java
 * See http://www.seismicnet.com/psnformat4.html
 *
 * @author Created by Philip Oliver-Paull
 */
public class PSNDataFile {

    private PSNEventRecord[] eventRecs;

    /**
     * Old constructor that takes file name
     */
    public PSNDataFile(String filename) throws FileNotFoundException, IOException{
        this(new DataInputStream(new BufferedInputStream(new FileInputStream(filename))));
    }

    /**
     * New constructor that takes DataInputStream.
     */
    public PSNDataFile(DataInputStream dis) throws FileNotFoundException, IOException{
        readFile(dis);
        dis.close();
    }

    private void readFile(DataInputStream dis) throws IOException, FileNotFoundException{
        PSNHeader header = new PSNHeader(dis);
        if (!header.isVolumeFile()){
            eventRecs = new PSNEventRecord[]{new PSNEventRecord(header, dis)};
        }
        else{
            eventRecs = new PSNEventRecord[header.getNumRecords()];
            for (int i = 0; i < header.getNumRecords(); i++) {
                eventRecs[i] = new PSNEventRecord(dis);
            }
        }
    }

    public PSNEventRecord[] getEventRecords(){
        return eventRecs;
    }

    public static byte[] chopToLength(byte[] byteArray){
        List list = new ArrayList();
        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] != 0){
                list.add(new Byte(byteArray[i]));
            }
        }
        byte[] newArray = new byte[list.size()];
        for (int i = 0; i < list.size(); i++){
            newArray[i]  = ((Byte)list.get(i)).byteValue();
        }

        return newArray;
    }

    public static void main(String[] args){
        try{
            PSNDataFile psnData = new PSNDataFile(args[0]);
            PSNEventRecord[] records = psnData.getEventRecords();

            System.out.println("Number of Records: " + records.length);
            for (int i = 0; i < records.length; i++) {
                System.out.println("****** Event Record " + i + " ******");
                System.out.println(records[i].toString());
            }
        }
        catch(Throwable ee){
            ee.printStackTrace();
        }
    }

}

