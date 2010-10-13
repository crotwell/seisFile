package edu.sc.seis.seisFile.mseed;



public abstract class ControlBlockette extends Blockette {

    public ControlBlockette(byte[] info) {
        this.info = info;
    }

    byte[] info;

    @Override
    public int getSize() {
        byte[] lengthBytes = new byte[4];
        System.arraycopy(info, 3, lengthBytes, 0, 4);
        return Integer.parseInt(new String(lengthBytes));
    }

    @Override
    public byte[] toBytes() {
        return info;
    }
}
