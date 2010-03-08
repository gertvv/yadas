package gov.lanl.yadas;

import java.util.*;
import java.io.*;
import java.text.NumberFormat;

/**
 * Allows users to specify Metropolis(-Hastings) steps that propose 
 * move to multiple parameters simultaneously, and tunes the step 
 * sizes to achieve desired acceptance rates.  
 * @see gov.lanl.yadas.TunablePerturber
 */ 
public class TunableMultipleParameterUpdate extends MultipleParameterUpdate 
    implements TunableMCMCUpdate {

    public TunableMultipleParameterUpdate (MCMCParameter[] params, 
					   TunablePerturber perturber,
					   String name) {
	super (params, perturber);
	this.name = name;
	nft = NumberFormat.getNumberInstance();
	nft.setMaximumFractionDigits(3);
	nft.setGroupingUsed(false);
    }		
    
    public double[] getStepSizes () {
	return ((TunablePerturber)perturber).getStepSizes ();
    }
    public void setStepSize (double s, int i) {
	((TunablePerturber)perturber).setStepSize (s, i);
    }
    public void setStepSizes (double[] s) {
	((TunablePerturber)perturber).setStepSizes (s);
    }

    public String toStringTune () {
	String out = "";
	double[] temp = getStepSizes ();
	for (int i = 0; i < temp.length - 1; i++) {
	    out += (nft.format(temp[i]) + "|");
	}
	out += nft.format(temp[temp.length-1]);
	return out;
    }

    public void tuneoutput () {
	if (firsttuneoutput) {
	    firsttuneoutput = false;
	    try {
		tune_out = new PrintWriter ( new FileWriter
		    ( name + ".tun") );
	    }
	    catch (IOException e) {
		System.out.print("Error: " + e);
		System.exit(1);
	    }
	}
	tune_out.println(toStringTune());
    }
    
    public void finish() {
	if (!firsttuneoutput) {
	    tune_out.close ();
	}
    }

    //TunablePerturber perturber;
    boolean firsttuneoutput = true;
    PrintWriter tune_out;
    public NumberFormat nft;

    public String name;
    private static int instanceCounter = 0;

}

