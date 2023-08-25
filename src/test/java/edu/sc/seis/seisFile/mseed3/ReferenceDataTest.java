package edu.sc.seis.seisFile.mseed3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.sac.TestSacFileData;
import org.junit.jupiter.api.Test;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.*;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ReferenceDataTest {

    public static final String[] fileList = new String[]{
            "reference-data/reference-detectiononly.mseed3",
            "reference-data/reference-sinusoid-FDSN-All.mseed3",
            "reference-data/reference-sinusoid-FDSN-Other.mseed3",
            "reference-data/reference-sinusoid-TQ-TC-ED.mseed3",
            "reference-data/reference-sinusoid-float32.mseed3",
            "reference-data/reference-sinusoid-float64.mseed3",
            "reference-data/reference-sinusoid-int16.mseed3",
            "reference-data/reference-sinusoid-int32.mseed3",
            "reference-data/reference-sinusoid-steim1.mseed3",
            "reference-data/reference-sinusoid-steim2.mseed3",
            "reference-data/reference-text.mseed3",
    };

    @Test
    public void recordVsJsonTest() throws IOException, SeedFormatException, FDSNSourceIdException {
        for (String f: fileList) {
            System.out.println(f);
            DataInputStream dis = new DataInputStream(new BufferedInputStream(ReferenceDataTest.class.getClassLoader()
                    .getResourceAsStream("edu/sc/seis/seisFile/mseed3/"+f)));
            MSeed3Record record = MSeed3Record.read(dis);

            String jsontextFile = f.replaceAll(".mseed3", ".json");
            BufferedReader jsontextReader = new BufferedReader(new InputStreamReader(ReferenceDataTest.class.getClassLoader()
                    .getResourceAsStream("edu/sc/seis/seisFile/mseed3/"+jsontextFile)));
            StringBuilder sb = new StringBuilder(512);
            int c=0;
            while((c= jsontextReader.read()) != -1) {
                sb.append((char)c);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnnnX").withZone(ZoneId.of("UTC"));
            JSONObject jsonRec = new JSONArray(sb.toString()).getJSONObject(0);
            assertEquals(jsonRec.getString("SID"), record.getSourceId().toString());
            assertEquals(jsonRec.getInt("RecordLength"), record.getSize());
            assertEquals(jsonRec.getInt("FormatVersion"), record.getFormatVersion());
            assertEquals(jsonRec.getString("StartTime"), formatter.format(record.getStartDateTime()));
//            assertEquals(jsonRec.getString("StartTime"), DateTimeFormatter.ISO_INSTANT.format(record.getStartDateTime()));
            assertEquals(jsonRec.getInt("EncodingFormat"), record.getTimeseriesEncodingFormat());
            assertEquals(jsonRec.getDouble("SampleRate"), record.getSampleRate());
            assertEquals(jsonRec.getInt("SampleCount"), record.getNumSamples());
            assertEquals(jsonRec.getString("CRC"), "0x"+Integer.toHexString(record.getRecordCRC()).toUpperCase());
            assertEquals(jsonRec.getInt("PublicationVersion"), record.getPublicationVersion());
            assertEquals(jsonRec.getInt("ExtraLength"), record.getExtraHeadersByteLength());
            assertEquals(jsonRec.getInt("DataLength"), record.getDataByteLength());

            assertEquals(jsonRec.has("ExtraHeaders"), record.getExtraHeadersByteLength()>2);
            if (jsonRec.has("ExtraHeaders")) {
                JSONObject refEH = jsonRec.getJSONObject("ExtraHeaders");
                JSONObject eh = record.getExtraHeaders();
                assertTrue(refEH.similar(eh));
            }
        }
    }
}
