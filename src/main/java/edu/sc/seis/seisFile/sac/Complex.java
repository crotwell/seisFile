package edu.sc.seis.seisFile.sac;



public class Complex {

    private double real;

    private double imaginary;

    public Complex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public double getReal() {
        return real;
    }
    
    public double getImaginary() {
        return imaginary;
    }

    public final double mag() {
        return (Math.sqrt(this.getReal() * this.getReal() + this.getImaginary() * this.getImaginary()));
    }
    
    public final Complex conjg() {
        return new Complex(getReal(), -1*getImaginary());
    }

    public static final Complex add(double a, Complex b) {
        return add(new Complex(a, 0), b);
    }

    public static final Complex add(Complex a, double b) {
        return add(a, new Complex(b, 0));
    }

    public static final Complex add(Complex a, Complex b) {
        return new Complex( a.getReal() + b.getReal(),
                            a.getImaginary() + b.getImaginary());
    }

    public static final Complex sub(double a, Complex b) {
        return Complex.sub(new Complex(a, 0), b);
    }

    public static final Complex sub(Complex a, double b) {
        return Complex.sub(a, new Complex(b, 0));
    }

    public static final Complex sub(Complex a, Complex b) {
        return new Complex(a.getReal() - b.getReal(),
                           a.getImaginary() - b.getImaginary());
    }

    public static final Complex mul(double a, Complex b) {
        return Complex.mul(new Complex(a, 0), b);
    }

    public static final Complex mul(Complex a, double b) {
        return Complex.mul(a, new Complex(b, 0));
    }

    public static final Complex mul(Complex a, Complex b) {
        return new Complex(a.getReal() * b.getReal() - a.getImaginary() * b.getImaginary(), 
                           a.getImaginary() * b.getReal() + a.getReal() * b.getImaginary());
    }

    public static final Complex div(double a, Complex b) {
        return div(new Complex(a, 0), b);
    }

    public static final Complex div(Complex a, double b) {
        return div(a, new Complex(b, 0));
    }

    public static final Complex div(Complex a, Complex b) {
        double r, den;
        if(Math.abs(b.getReal()) >= Math.abs(b.getImaginary())) {
            r = b.getImaginary() / b.getReal();
            den = b.getReal() + r * b.getImaginary();
            return new Complex((a.getReal() + r * a.getImaginary()) / den,
                               (a.getImaginary() - r * a.getReal()) / den);
        } else {
            r = b.getReal() / b.getImaginary();
            den = b.getImaginary() + r * b.getReal();
            return new Complex((a.getReal() * r + a.getImaginary()) / den,
                               (a.getImaginary() * r - a.getReal()) / den);
        }
    }
    
    public boolean equals(Object obj) {
        if(super.equals(obj)) {
            return true;
        }
        if(obj instanceof Complex) {
            Complex other = (Complex)obj;
            return (getReal() == other.getReal()) && (getImaginary() == other.getImaginary());
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = 17;
        long tmp = Double.doubleToLongBits(getImaginary());
        i = 31 * i + (int)(tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(getReal());
        i = 37 * i + (int)(tmp ^ (tmp >>> 32));
        return i;
    }
    
}
