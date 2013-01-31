package edu.sc.seis.seisFile.earthworm;

public class EarthwormMessage {

    public EarthwormMessage(byte[] bytes) {
        if (bytes.length < 9) {
            throw new IllegalArgumentException("Message must have at least 9 bytes");
        }
        institution = Integer.parseInt(new String(bytes, 0, 3).trim());
        module = Integer.parseInt(new String(bytes, 3, 3).trim());
        messageType = Integer.parseInt(new String(bytes, 6, 3).trim());
        data = new byte[bytes.length - 9];
        System.arraycopy(bytes, 9, data, 0, data.length);
    }

    public int getInstitution() {
        return institution;
    }

    public int getModule() {
        return module;
    }

    public int getMessageType() {
        return messageType;
    }

    public byte[] getData() {
        return data;
    }

    int institution;

    int module;

    int messageType;

    byte[] data;

    public static final byte MESSAGE_TYPE_TRACEBUF2 = 19;
    public static final byte MESSAGE_TYPE_HEARTBEAT = 3;
}
