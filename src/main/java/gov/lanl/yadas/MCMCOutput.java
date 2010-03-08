package gov.lanl.yadas;

/**
 * Interface defining what happens at the end of an MCMC iteration.  The most 
 * common class implementing this interface is MCMCParameter, which writes another 
 * line to its output file.  
 */
public interface MCMCOutput {

	public void output ();

}
