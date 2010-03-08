package gov.lanl.yadas;

/** 
 * ComponentIntegrator interface is used in system reliability modeling.  
 * A class that implements this interface tells how components are 
 * combined to form a subsystem.  Common examples are SeriesIntegrator, 
 * for which the combineProbabilities method takes the product and 
 * the combineFailureRates method takes the sum, and ParallelIntegrator.  
 * the combineFailureRates method probably does not make sense unless 
 * failures are exponential.  
 * @see gov.lanl.yadas.ReliableSystem
 */
public interface ComponentIntegrator {

    public double combineProbabilities (double[][] componentprobs);
    
    public double combineFailureRates (double[] componentrates);

    /** 
     * returns the integer label that indicates which node in the graph 
     * corresponds to this subsystem
     */ 
    public int getSubsystem ();

    /**
     * returns an array of integer labels of which nodes are components 
     * in this subsystem
     */
    public int[] getComponents ();

}
