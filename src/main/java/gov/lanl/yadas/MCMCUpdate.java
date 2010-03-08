package gov.lanl.yadas;

/**
 * Interface that defines all possible steps in a YADAS MCMC algorithm.  
 * The critical method is, naturally, update(), which generates a new 
 * value of the unknown parameters.  The other methods are related to 
 * diagnostic output, including numbers of accepted moves.  
 */
public interface MCMCUpdate 
{
    public void update ();

    // I guess if multiple arrays of candidates are needed, they will be 
    // concatenated together
    // public double[] candidate ();

    // generating candidate by modifying group i: should be unnecessary if 
    // group-able MCMCParameters keep track of which group's turn it is
    // public Object candidate (int i);

    // if multiple parameters can be modified simultaneously, return a List(?)
    // of all bonds that involve them
    // public MCMCBond[] relevantBonds ();

    // typically return likelihood ratio, sometimes multiplied by ratio of 
    // transition probabilities
    // public double acceptanceProbability ();

    // modify the value records of all affected parameters
    // public void takeStep ();

    public String accepted ();

    public void updateoutput ();

    public void finish ();

}
