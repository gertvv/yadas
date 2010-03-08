package gov.lanl.yadas;

/** 
 * ArgumentMaker: an important interface used in defining statistical models 
 * in YADAS.  ArgumentMakers accept raw values of parameters as inputs and 
 * convert them into arguments for "Likelihood"s.  (Likelihoods are in fact 
 * used to define any term in a posterior distribution, including prior 
 * terms.)  Examples: if y_{ij} ~ N(mu_{i}, sigma^2), and {y, mu, sigma} 
 * are all MCMCParameters, an IdentityArgument will generally be used 
 * to create the y argument, a GroupArgument will be used to make an 
 * array of mu's the same length as y and with mu_i matched with each of 
 * the y_{ij}'s, and another GroupArgument will be used to make an array 
 * of the same length as y, each of whose values is the scalar sigma.  
 * @see gov.lanl.yadas.BasicMCMCBond
 * @see gov.lanl.yadas.Likelihood 
 */
public interface ArgumentMaker {

    public double[] getArgument (double[][] params);

}
