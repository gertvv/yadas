package gov.lanl.yadas;

/**
 * The functional form of logs of terms in the posterior distribution.  
 * The idea behind this interface is that YADAS requires only a 
 * small number of density functions (Gaussian, Binomial, etc).  
 * "Likelihood" is a terrible name, because these functions actually 
 * compute the logs of density functions (or probability functions), 
 * and are used for priors as much as likelihoods.    
 * The compute() methods of Likelihoods are written assuming that 
 * the arguments are in the form of rectangular arrays: for example, 
 * the Gaussian likelihood assumes that i'th entry in the first column 
 * has mean given by the i'th entry in the second column and standard 
 * deviation given by the i'th entry in the third column.  
 * Classes implementing the ArgumentMaker interface are used to 
 * arrange this.  
 * @see gov.lanl.yadas.BasicMCMCBond
 */ 
public interface Likelihood {

    public double compute (double[][] args);

    /**
     * Deprecated.
     */
    //public double compute (double[][] args, int[] indices);

}
