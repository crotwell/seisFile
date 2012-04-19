package edu.sc.seis.seisFile.syncFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.junit.Test;

import edu.sc.seis.seisFile.SeisFileException;

public class SyncFileCompareTest {

    @Test
    public void testSyncFileCompareAfter() throws IOException, SeisFileException {
        SyncFile a = loadResource("A.sync");
        SyncFile allAfter = loadResource("allAfter.sync");
        SyncFileCompare allAfterCompare = new SyncFileCompare(a, allAfter);
        assertEquals(0, allAfterCompare.getInAinB().getSyncLines().size());
        assertEquals(a.getSyncLines().size(), allAfterCompare.getInAnotB().getSyncLines().size());
        assertEquals(allAfter.getSyncLines().size(), allAfterCompare.getNotAinB().getSyncLines().size());
    }

    @Test
    public void testSyncFileCompareBefore() throws IOException, SeisFileException {
        SyncFile a = loadResource("A.sync");
        SyncFile allBefore = loadResource("allBefore.sync");
        SyncFileCompare allBeforeCompare = new SyncFileCompare(a, allBefore);
        assertEquals(0, allBeforeCompare.getInAinB().getSyncLines().size());
        assertEquals(a.getSyncLines().size(), allBeforeCompare.getInAnotB().getSyncLines().size());
        assertEquals(allBefore.getSyncLines().size(), allBeforeCompare.getNotAinB().getSyncLines().size());
    }

    @Test
    public void testSyncFileCompareDisjoint() throws IOException, SeisFileException {
        SyncFile a = loadResource("A.sync");
        SyncFile disjoint = loadResource("disjoint.sync");
        SyncFileCompare disjointCompare = new SyncFileCompare(a, disjoint);
        assertEquals(0, disjointCompare.getInAinB().getSyncLines().size());
        assertEquals(a.getSyncLines().size(), disjointCompare.getInAnotB().getSyncLines().size());
        assertEquals(disjoint.getSyncLines().size(), disjointCompare.getNotAinB().getSyncLines().size());
    }

    @Test
    public void testSyncFileCompareAllWithBeforeAfter() throws IOException, SeisFileException {
        SyncFile a = loadResource("A.sync");
        SyncFile b = loadResource("allWithBeforeAndAfter.sync");
        SyncFileCompare compare = new SyncFileCompare(a, b);
        assertEquals(a.getSyncLines().size(), compare.getInAinB().getSyncLines().size());
        assertEquals(0, compare.getInAnotB().getSyncLines().size());
        assertEquals(b.getSyncLines().size()-a.getSyncLines().size(), compare.getNotAinB().getSyncLines().size());
    }

    @Test
    public void testSyncFileCompareOverlaps() throws IOException, SeisFileException {
        SyncFile a = loadResource("A.sync");
        SyncFile b = loadResource("overlaps.sync");
        SyncFileCompare compare = new SyncFileCompare(a, b);
        assertEquals(a.getSyncLines().size(), compare.getInAinB().getSyncLines().size());
        assertEquals(2, compare.getInAnotB().getSyncLines().size());
        assertEquals(4, compare.getNotAinB().getSyncLines().size());
    }

    @Test
    public void testProcessItem() throws SeisFileException {
        SyncLine a = SyncLine.parse("CO|JSC|00|HHZ|2010,243,08:00:05|2010,244,03:27:34||100.0|||||||");
        SyncLine b = new SyncLine(a, new Date(a.getStartTime().getTime() - 60 * 1000), a.getEndTime());
        SyncFile inAinB = new SyncFile("inAinB");
        SyncFile inAnotB = new SyncFile("inAnotB");
        SyncFile notAinB = new SyncFile("notAinB");
        SyncLine[] out = SyncFileCompare.processItem(a, b, inAinB, notAinB, inAnotB);
        assertEquals("inAnotB", 0, inAnotB.getSyncLines().size());
        assertEquals("notAinB", 1, notAinB.getSyncLines().size());
        assertEquals("inAinB", 1, inAinB.getSyncLines().size());
        assertNull("a", out[0]);
        assertNull("b", out[1]);
    }

    SyncFile loadResource(String filename) throws IOException, SeisFileException {
        return SyncFile.load(new BufferedReader(new InputStreamReader(SyncFileCompareTest.class.getClassLoader()
                .getResourceAsStream("edu/sc/seis/seisFile/syncFile/" + filename))));
    }
}
