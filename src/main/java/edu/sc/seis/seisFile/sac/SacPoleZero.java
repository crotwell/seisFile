package edu.sc.seis.seisFile.sac;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author crotwell Created on Jul 15, 2005
 */
public class SacPoleZero {

    public SacPoleZero(BufferedReader in) throws IOException {
        read(in);
    }

    public SacPoleZero(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        read(in);
        in.close();
    }

    public SacPoleZero(Complex[] poles, Complex[] zeros, float constant) {
        this.poles = poles;
        this.zeros = zeros;
        this.constant = constant;
    }

    public float getConstant() {
        return constant;
    }

    public Complex[] getPoles() {
        return poles;
    }

    public Complex[] getZeros() {
        return zeros;
    }

    public String toString() {
        DecimalFormat formatter = new DecimalFormat(" 0.0000;-0.0000");
        DecimalFormat constantFormatter = new DecimalFormat("0.0#######E00");
        String out = ZEROS+" "+zeros.length+"\n";
        for (int i = 0; i < zeros.length; i++) {
            out += formatter.format(zeros[i].getReal())+" "+formatter.format(zeros[i].getImaginary())+"\n";
        }
        out += POLES+" "+poles.length+"\n";
        for (int i = 0; i < poles.length; i++) {
            out += formatter.format(poles[i].getReal())+" "+formatter.format(poles[i].getImaginary())+"\n";
        }
        out += CONSTANT+" "+constantFormatter.format(constant)+"\n";
        return out;
    }

    protected void read(BufferedReader in) throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        String s;
        while((s = in.readLine()) != null) {
            lines.add(s.trim());
        }
        Complex[] poles = new Complex[0];
        Complex[] zeros = new Complex[0];
        float constant = 1;
        Iterator<String> it = lines.iterator();
        String line = nextLine(it);
        while(!line.equals("")) {
            if(line.startsWith(POLES)) {
                String num = line.substring(POLES.length()).trim();
                int numPoles = Integer.parseInt(num);
                poles = initCmplx(numPoles);
                line = nextLine(it);
                for(int i = 0; i < poles.length && it.hasNext(); i++) {
                    if(line.matches("^-?\\d+\\.\\d+\\s+-?\\d+\\.\\d+")) {
                        poles[i] = parseCmplx(line);
                        line = nextLine(it);
                    } else {
                        break;
                    }
                }
            } else if(line.startsWith(ZEROS)) {
                String num = line.substring(ZEROS.length()).trim();
                int numZeros = Integer.parseInt(num);
                zeros = initCmplx(numZeros);
                line = nextLine(it);
                for(int i = 0; i < zeros.length && it.hasNext(); i++) {
                    if(line.matches("^-?\\d+\\.\\d+\\s+-?\\d+\\.\\d+")) {
                        zeros[i] = parseCmplx(line);
                        line = nextLine(it);
                    } else {
                        break;
                    }
                }
            } else if(line.startsWith(CONSTANT)) {
                line = line.replaceAll("\\s+", " ");
                String[] sline = line.split(" ");
                constant = Float.parseFloat(sline[1]);
                line = nextLine(it);
            } else {
                throw new IOException("Unknown line in SAC polezero file: "
                        + line);
            }
        }
        this.poles = poles;
        this.zeros = zeros;
        this.constant = constant;
    }

    private static String nextLine(Iterator it) {
        if(it.hasNext()) {
            return (String)it.next();
        } else {
            return "";
        }
    }

    public static Complex[] initCmplx(int length) {
        Complex[] out = new Complex[length];
        for(int i = 0; i < out.length; i++) {
            out[i] = new Complex(0, 0);
        }
        return out;
    }

    static Complex parseCmplx(String line) throws IOException {
        line = line.trim().replaceAll("\\s+", " ");
        String[] sline = line.split(" ");
        return new Complex(Float.parseFloat(sline[0]), Float.parseFloat(sline[1]));
    }

    public boolean close(Object obj) {
        if(super.equals(obj)) {
            return true;
        }
        if(obj instanceof SacPoleZero) {
            SacPoleZero spz = (SacPoleZero)obj;
            if(!close(spz.constant, constant)) {
                System.out.println("const not close");
                return false;
            } else {
                return closeButConstant(obj);
            }
        } else {
            return false;
        }
    }

    public boolean closeButConstant(Object obj) {
        if(super.equals(obj)) {
            return true;
        }
        if(obj instanceof SacPoleZero) {
            SacPoleZero spz = (SacPoleZero)obj;
            if(spz.poles.length != poles.length
                    || spz.zeros.length != zeros.length) {
                return false;
            } else {
                for(int i = 0; i < poles.length; i++) {
                    if( ! closeFourDigit(spz.poles[i], poles[i])) {
                        System.out.println("pole " + i + " not equal"
                                + spz.poles[i].getImaginary() + " " + poles[i].getImaginary() + " "
                                + spz.poles[i].getReal() + " " + poles[i].getReal());
                        return false;
                    }
                }
                for(int i = 0; i < zeros.length; i++) {
                    if( ! closeFourDigit(spz.zeros[i], zeros[i])) {
                        System.out.println("zero " + i + " not equal");
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    private static boolean close(double a, double b) {
        if(Math.abs(a - b) / a > 0.0001) {
            System.out.println("fail close " + a + " " + b + " "
                    + (Math.abs(a - b) / a) + " ratio=" + (a / b));
            return false;
        }
        return true;
    }

    private static boolean closeFourDigit(Complex a, Complex b) {
        return closeFourDigit(a.getReal(), b.getReal()) && closeFourDigit(a.getImaginary(), b.getImaginary());
    }

    private static boolean closeFourDigit(double a, double b) {
        if(Math.abs(a - b) > 0.0001) {
            System.out.println("fail closeFourDigit " + a + " " + b + " "
                    + (Math.abs(a - b)) + " ratio=" + (a / b));
            return false;
        }
        return true;
    }

    public boolean equals(Object obj) {
        if(super.equals(obj)) {
            return true;
        }
        if(obj instanceof SacPoleZero) {
            SacPoleZero spz = (SacPoleZero)obj;
            if((Math.abs(spz.constant - constant) / constant) > .001) {
                return false;
            } else if(spz.poles.length != poles.length
                    || spz.zeros.length != zeros.length) {
                return false;
            } else {
                for(int i = 0; i < poles.length; i++) {
                    if(spz.poles[i].getImaginary() != poles[i].getImaginary()
                            || spz.poles[i].getReal() != poles[i].getReal()) {
                        return false;
                    }
                }
                for(int i = 0; i < zeros.length; i++) {
                    if(spz.zeros[i].getImaginary() != zeros[i].getImaginary()
                            || spz.zeros[i].getReal() != zeros[i].getReal()) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = 17;
        i = 29 * i + poles.length;
        i = 31 * i + zeros.length;
        for(int j = 0; j < poles.length; j++) {
            i = 37 * i + poles[j].hashCode();
        }
        for(int j = 0; j < zeros.length; j++) {
            i = 43 * i + zeros[j].hashCode();
        }
        return i;
    }

    private Complex[] poles;

    private Complex[] zeros;

    private float constant;

    static String POLES = "POLES";

    static String ZEROS = "ZEROS";

    static String CONSTANT = "CONSTANT";

}