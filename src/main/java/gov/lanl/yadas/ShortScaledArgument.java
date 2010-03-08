package gov.lanl.yadas;

/** 
 * Used to extract a single element from a parameter, and multiply it by an element of 
 * a scaling vector (for example, this may be an exposure measure for use in Poisson 
 * regression).  
 */
public class ShortScaledArgument implements ArgumentMaker {

    // Used to return the mean number of events when the rate parameter 
    // is the 0'th parameter, and the time on test is given by 'timetested'.

    public ShortScaledArgument (double[] timetested, int which, int j) {
	this.timetested = new double[timetested.length];
	System.arraycopy (timetested, 0, this.timetested, 0, 
			  timetested.length);
	this.which = which;
	which2 = j;
    }

    public double[] getArgument (double[][] params) {
	return new double[] { params[which][which2] * timetested[which2] };
    }

    private double[] timetested;
    private int which;
    private int which2;
}
