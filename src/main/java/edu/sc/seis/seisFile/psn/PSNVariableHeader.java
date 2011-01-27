package edu.sc.seis.seisFile.psn;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.sc.seis.seisFile.sac.SacTimeSeries;

/**
 * PSNVariableHeader.java
 *
 * @author Created by Philip Oliver-Paull
 */
public class PSNVariableHeader{
    private DataInputStream dis;
    private Map entries = new HashMap();

    public PSNVariableHeader(DataInputStream data, int length) throws IOException{
        dis = data;

        for (int i = 0; i < length; i++) {

            //Sanity Check: this should be equal to 0x55
            byte check = dis.readByte();
            //System.out.println("Check: " + check);
            if (check != 0x55){
                throw new IOException("file may not be a standard Type 4 PSN format");
            }

            //read descriptor id
            byte id = dis.readByte();
            int fieldLength = SacTimeSeries.swapBytes(dis.readInt());
            //System.out.println("varHeader iteration " + i + ", id " + id + ", fieldLength " + fieldLength);

            switch (id) {
                case 0:
                    if (fieldLength != 0){
                        throw new IOException("end of header has unread data");
                    }
                    break;
                case 1:
                case 2:
                case 3:
                case 7:
                    //entries.put(new Byte(id), readNullTerminatedString(dis, fieldLength));
                    byte[] stringArray = new byte[fieldLength];
                    dis.readFully(stringArray);
                    String aString = new String(PSNDataFile.chopToLength(stringArray));
                    entries.put(new Byte(id), aString);
                    //System.out.println(aString);
                    break;
                case 4:
                    List eventInfoList;
                    if (entries.containsKey(new Byte(id))){
                        eventInfoList = (ArrayList)entries.get(new Byte(id));
                    }
                    else{
                        eventInfoList = new ArrayList();
                    }
                    eventInfoList.add(new PSNEventInfo(dis));
                    entries.put(new Byte(id), eventInfoList);
                    break;
                case 5:
                    List phasePickList;
                    if (hasPhasePicks()){
                        phasePickList = (ArrayList)entries.get(new Byte(id));
                    }
                    else{
                        phasePickList = new ArrayList();
                    }
                    phasePickList.add(new PSNPhasePick(dis));
                    entries.put(new Byte(id), phasePickList);
                    break;
                case 11:
                    entries.put(new Byte(id), new PSNSensorAmpAtoD(dis));
                    break;
                case 12:
                    entries.put(new Byte(id), new PSNPolesAndZeros(dis));
                    break;
                default:
                    dis.skip(fieldLength);
                    break;
            }

            i+=(fieldLength + 5);
        }
    }

    public boolean hasSensorLocation(){
        return entries.containsKey(new Byte((byte)1));
    }

    public String getSensorLocation(){
        return (String)entries.get(new Byte((byte)1));
    }

    public boolean hasSensorInformation(){
        return entries.containsKey(new Byte((byte)2));
    }

    public String getSensorInformation(){
        return (String)entries.get(new Byte((byte)2));
    }

    public boolean hasComment(){
        return entries.containsKey(new Byte((byte)3));
    }

    public String getComment(){
        return (String)entries.get(new Byte((byte)3));
    }

    public boolean hasEventInfo(){
        return entries.containsKey(new Byte((byte)4));
    }

    public PSNEventInfo[] getEventInfo(){
        List eventInfoList = (ArrayList)entries.get(new Byte((byte)4));
        return (PSNEventInfo[])eventInfoList.toArray(new PSNEventInfo[0]);
    }

    public boolean hasPhasePicks(){
        return entries.containsKey(new Byte((byte)5));
    }

    public PSNPhasePick[] getPhasePicks(){
        List eventInfoList = (ArrayList)entries.get(new Byte((byte)5));
        return (PSNPhasePick[])eventInfoList.toArray(new PSNPhasePick[0]);
    }

    public boolean hasDataLoggerID(){
        return entries.containsKey(new Byte((byte)7));
    }

    public String getDataLoggerID(){
        return (String)entries.get(new Byte((byte)7));
    }

    public boolean hasSensorAmpAtoD(){
        return entries.containsKey(new Byte((byte)11));
    }

    public PSNSensorAmpAtoD getSensorAmpAtoD(){
        return (PSNSensorAmpAtoD)entries.get(new Byte((byte)11));
    }

    public boolean hasPolesAndZeros(){
        return entries.containsKey(new Byte((byte)12));
    }

    public PSNPolesAndZeros getPolesAndZeros(){
        return (PSNPolesAndZeros)entries.get(new Byte((byte)12));
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();

        Set ks = entries.keySet();
        Iterator iter = ks.iterator();
        while (iter.hasNext()){
            Byte curr = (Byte)iter.next();
            byte id = curr.byteValue();

            switch (id) {
                case 1:
                    buf.append("Sensor Location: " + (String)entries.get(curr) + '\n');
                    break;
                case 2:
                    buf.append("Sensor Info: " + (String)entries.get(curr) + '\n');
                    break;
                case 3:
                    buf.append("Comments: " + (String)entries.get(curr) + '\n');
                    break;
                case 7:
                    buf.append("DataLogger ID: " + (String)entries.get(curr) + '\n');
                    break;
                case 4:
                    List eventInfoList = (ArrayList)entries.get(curr);

                    Iterator it = eventInfoList.iterator();
                    while (it.hasNext()){
                        PSNEventInfo evInf = (PSNEventInfo)it.next();
                        buf.append(evInf.toString());
                    }
                    break;
                case 5:
                    List phasePickList = (ArrayList)entries.get(curr);

                    Iterator it2 = phasePickList.iterator();
                    while (it2.hasNext()){
                        PSNEventInfo evInf = (PSNEventInfo)it2.next();
                        buf.append(evInf.toString());
                    }
                    break;
                case 11:
                    PSNSensorAmpAtoD psnSens = (PSNSensorAmpAtoD)entries.get(curr);
                    buf.append(psnSens.toString());
                    break;
                case 12:
                    PSNPolesAndZeros psnPoles = (PSNPolesAndZeros)entries.get(curr);
                    buf.append(psnPoles.toString());
                    break;
            }
        }

        return buf.toString();
    }

}

