package gov.lanl.yadas;

/**
 * Interface used in an abortive effort to clean up the various compute methods 
 * MCMCParameter and others.  Users should ignore this class and assume that the 
 * effort will never be completed.  
 */ 
public interface ParameterChanger {
    
    public void change (double[][] preargs);
    
}
