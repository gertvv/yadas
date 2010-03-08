package gov.lanl.yadas;

/**
 * Interface for defining arbitrary java functions of several real 
 * arguments.  Its most common usage is in a FunctionalArgument.
 * Almost all functions are defined anonymously; emulate the 
 * example included in the web page yadas.lanl.gov.  
 * @see gov.lanl.yadas.FunctionalArgument
 */
public interface Function {
    
    public double f(double[] x);

}
