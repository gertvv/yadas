package gov.lanl.yadas;

/**
 * Mainly used for the loggamma function (the code for which I stole from 
 * some C function, can't remember where, sorry).  
 */
public final class Tools {

    public Tools () {}

    public static double loggamma(double xx) {
	
	double x, y, tmp, ser;
	final double cof[] = {76.18009172947146, -86.50532032941677,
			      24.01409824083091, -1.231739572450155,
			      0.1208650973866179e-2, -0.5395239384953e-5};
	int j;
	
	y = x = xx;
	tmp = x + 5.5;
	tmp -= (x+0.5) * Math.log(tmp);
	ser = 1.000000000190015;
	for (j = 0; j <= 5; j++) ser += cof[j]/++y;
	return -tmp + Math.log(2.5066282746310005*ser/x);
    }

    public static double[] rep(double x, int n) {
	double[] temp = new double[n];
	for (int i = 0; i < n; i++) {
	    temp[i] = x;
	}
	return temp;
    }

	public static int[] repi (int x, int n) {
		return repi (new int[] { x }, n);
	}

    public static int[] repi (int[] x, int n) {
	int[] out = new int[x.length * n];
	for (int i = 0; i < x.length; i++) {
	    for (int j = 0; j < n; j++) {
		out[j*x.length + i] = x[i];
	    }
	}
	return out;
    }

    public static double mean(double[] vec) {
	double out = 0.0;
	for (int i = 0; i < vec.length; i++) {
	    out += vec[i];
	}
	return out / vec.length;
    }
}
