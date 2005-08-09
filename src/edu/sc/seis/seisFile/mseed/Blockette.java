
package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/** Superclass of all seed blockettes. The actual blockettes do not store
 * either their blockette type or their length in the case of ascii blockettes
 * or next blockettes offset in the case of data blockettes as these are either
 * already known (ie type) or may change after reading due to data changes.
 * Instead each of these values are calculated based on the data.
 */
public abstract class  Blockette {
    
    public Blockette() {
    }
    
    /**
     * Method writeASCII
     *
     * @param    out                 a  Writer
     *
     */
    public abstract void writeASCII(Writer out) throws IOException;
    
    public static Blockette parseBlockette(int type, byte[] bytes)
        throws IOException, SeedFormatException {
        
        try {
            Class blocketteClass = Class.forName("edu.sc.seis.seisFile.mseed.Blockette"+type);
            //System.out.println(" Class.forName suceeded");
            
            Class[] argTypes = new Class[1];
            //      argTypes[0] = Class.forName("byte[]");
            argTypes[0] = byte[].class;
            Constructor read = blocketteClass.getConstructor(argTypes);
            Object[] arguments = new Object[1];
            arguments[0] = bytes;
            //System.out.println("Constructor  suceeded");
            
            Blockette blockette = (Blockette)read.newInstance(arguments);
            
            
            //System.out.println("read suceeded");
            return blockette;
        } catch (ClassNotFoundException e) {
            // must not be installed, read an  unknownblockette
            System.out.println(" Class.forName failed: "+type);
            Blockette blockette = new BlocketteUnknown(bytes, type);
            
            return blockette;
        } catch ( NoSuchMethodException e) {
            throw new SeedFormatException("Can't load blockette for type="+type, e);
        } catch (InstantiationException e) {
            throw new SeedFormatException("Can't load blockette for type="+type, e);
        } catch (IllegalAccessException  e) {
            throw new SeedFormatException("Can't load blockette for type="+type, e);
        } catch (InvocationTargetException  e) {
            throw new SeedFormatException("Can't load blockette for type="+type, e);
        }
    }
    
    public abstract int getType();
    
    public abstract String getName();
    
    public abstract int getSize();
    
    public abstract byte[] toBytes();
    
    public String toString() {
        String s = getType()+": "+getName();
        return s;
    }
    
}

