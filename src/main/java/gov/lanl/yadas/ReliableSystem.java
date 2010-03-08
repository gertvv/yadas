package gov.lanl.yadas;
import java.util.*;

/**
 * Used to define system reliability analyses.  The analyses can be built 
 * using ComponentIntegrators, which specify that some nodes combine in 
 * (for example) series or parallel to form other nodes.  
 */
public class ReliableSystem {

    /**
     * The constructor requires only one argument, the number of nodes.  
     * This number should include not only the number of leaf nodes, 
     * but all subsystems and the system itself.  
     */
    public ReliableSystem (int size) {
	this.size = size;
	integrators = new ComponentIntegrator[size];
	for (int i = 0; i < size; i++) {
	    integrators[i] = new NullIntegrator(i);
	}
    }

    public class DiagnosticSystemProbArgument implements ArgumentMaker {
	public DiagnosticSystemProbArgument (int[] expander, int[] order, 
			ArgumentMaker biasarg) {
	    this.expander = expander;
	    this.order = order;
	    if (expander.length == size) {
		vec = expander;
	    }
	    else {
		build();
	    }
		this.biasarg = biasarg;
	}
	public DiagnosticSystemProbArgument (int[] expander, ArgumentMaker biasarg) {
	    this.expander = expander;
	    order = new int[size];
	    for (int i = 0; i < size; i++) {
		order[i] = i;
	    }
	    System.out.println("Using default order to compute subsystem " + 
			       "probabilities.");
	    if (expander.length == size) {
		vec = expander;
	    }
	    else {		
		build();
	    }
		this.biasarg = biasarg;
	}
	public double[] getArgument (double[][] params) {
		double[] barg = biasarg.getArgument(params);
	    double[] outp = new double[size];
	    int j;
	    for (int i = 0; i < size; i++) {
		j = order[i];
		if (vec[j] > -1) outp[j] = params[0][vec[j]];
		else {
		    double tempop = integrators[j].
			combineProbabilities(new double[][] { outp });
			outp[j] = tempop / (tempop + (1-tempop) * 
				Math.exp(-barg[j])); 
		}
	    }
	    return outp;
	}
	public void build() {
	    vec = new int[size];
	    for (int i = 0; i < size; i++) {
		vec[i] = -1;
	    }
	    for (int i = 0; i < expander.length; i++) {
		vec[expander[i]] = i;
	    }
	}
	int[] vec;
	int[] expander;
	int[] order;
	ArgumentMaker biasarg;
    }


    public class VJSystemProbArgument implements ArgumentMaker {
	public VJSystemProbArgument (int[] expander, int[] order) {
	    this.expander = expander;
	    this.order = order;
	    if (expander.length == size) {
		vec = expander;
	    }
	    else {
		build();
	    }
	}
	public VJSystemProbArgument (int[] expander) {
	    this.expander = expander;
	    order = new int[size];
	    for (int i = 0; i < size; i++) {
		order[i] = i;
	    }
	    System.out.println("Using default order to compute subsystem " + 
			       "probabilities.");
	    if (expander.length == size) {
		vec = expander;
	    }
	    else {		
		build();
	    }
	}
	public double[] getArgument (double[][] params) {
	    double[] outp = new double[size];
	    int j;
	    for (int i = 0; i < size; i++) {
		j = order[i];
		if (vec[j] > -1) outp[j] = params[0][vec[j]];
		else {
		    /*
		      System.out.print(j + ":");
		      for (int k = 0; k < outp.length; k++) {
		      System.out.print(outp[k] + ", ");
		      }
		      System.out.println();
		    */
		    outp[j] = integrators[j].
			combineProbabilities(new double[][] { outp }); 
		}
	    }
	    return outp;
	}
	public void build() {
	    vec = new int[size];
	    for (int i = 0; i < size; i++) {
		vec[i] = -1;
	    }
	    for (int i = 0; i < expander.length; i++) {
		vec[expander[i]] = i;
	    }
	}
	int[] vec;
	int[] expander;
	int[] order;
    }

    public class LogitAgeProbArgument implements ArgumentMaker {
	public LogitAgeProbArgument (DataFrame d, String variablename, 
				     int[] expander, int[] order, 
				     int[] nodes, int which0, int which1) {
	    this (d, variablename, expander, nodes, which0, which1);
	    this.order = order;
	}
	public LogitAgeProbArgument (DataFrame d, String variablename, 
				     int[] expander, int[] nodes,
				     int which0, int which1) {
	    this.which0 = which0;
	    this.which1 = which1;
	    this.n = d.length();
	    this.nodes = nodes;
	    age = new double[size][n];
	    for (int i = 0; i < size; i++) {
		age[i] = d.r(variablename + i);
	    }
	    this.expander = expander;
	    // here's where we need to develop a data structure that 
	    // makes probability calculations more efficient by only 
	    // computing the nodes that are needed for the current data pt
	    order = new int[size];
	    for (int i = 0; i < size; i++) {
		order[i] = i;
	    }
	    if (expander.length == size) {
		vec = expander;
	    }
	    else {		
		build();
	    }
	}
	public double[] getArgument (double[][] params) {
	    // parameter 0 = intercept ("nugget")
	    // parameter 1 = age slope
	    double[] outp = new double[n];
	    double[] midp = new double[size];
	    for (int k = 0; k < n; k++) {
		// because code is inefficient, compute entire prob network
		int j;
		for (int i = 0; i < size; i++) {
		    j = order[i];
		    //System.out.println("i = " + i + ", j = " + j + ", vec[j] = " + vec[j]);
		    if (vec[j] > -1) {
			midp[j] = 1 / (1 + Math.exp
				       (params[0][vec[j]] + 
					params[1][vec[j]] * age[vec[j]][k]));
			/*
			printrealvector ( new double[] 
			    { j, k, vec[j], params[0][vec[j]], 
			      params[1][vec[j]], age[vec[j]][k], 
			      midp[j]});
			*/
		    } else {
			midp[j] = integrators[j].
			    combineProbabilities(new double[][] { midp }); 
			//printrealvector ( new double[] { j, midp[j] } );
		    }
		}
		// extract correct entries and put them in outp
		//for (int l = 0; l < n; l++) {
		int l = k;
		outp[l] = midp[nodes[l]];
		//printrealvector(new double[] { l, outp[l] });
		    //}
	    }
	    return outp;
	}
	public void build() {
	    vec = new int[size];
	    for (int i = 0; i < size; i++) {
		vec[i] = -1;
	    }
	    for (int i = 0; i < expander.length; i++) {
		vec[expander[i]] = i;
	    }
	}
	int n;
	String variablename;
	int[] vec;
	int[] expander;
	int[] order;
	double[][] age;
	int[][] indexarr;
	int[] nodes;
	int which0, which1;
    }

    public class LogitAgeProbArgumentPlus implements ArgumentMaker {
	public LogitAgeProbArgumentPlus 
	    (DataFrame d, String variablename, 
	     int[] expander, int[] order, 
	     int[] nodes, int which0, int which1,
	     int[] code, int jstar) {
	    this (d, variablename, expander, nodes, which0, which1, 
		  code, jstar);
	    this.order = order;
	}
	public LogitAgeProbArgumentPlus (DataFrame d, String variablename, 
					 int[] expander, int[] nodes,
					 int which0, int which1,
					 int[] code, int jstar) {
	    this.which0 = which0;
	    this.which1 = which1;
	    this.code = code;
	    this.jstar = jstar;
	    this.n = d.length();
	    this.nodes = nodes;
	    age = new double[size][n];
	    for (int i = 0; i < size; i++) {
		age[i] = d.r(variablename + i);
	    }
	    this.expander = expander;
	    // here's where we need to develop a data structure that 
	    // makes probability calculations more efficient by only 
	    // computing the nodes that are needed for the current data pt
	    order = new int[size];
	    for (int i = 0; i < size; i++) {
		order[i] = i;
	    }
	    if (expander.length == size) {
		vec = expander;
	    }
	    else {		
		build();
	    }
	}
	public double[] getArgument (double[][] params) {
	    // parameter 0 = intercept ("nugget")
	    // parameter 1 = age slope
	    // parameter 2 = crummy battery multiplier
	    double[] outp = new double[n];
	    double[] midp = new double[size];
	    for (int k = 0; k < n; k++) {
		// because code is inefficient, compute entire prob network
		int j;
		for (int i = 0; i < size; i++) {
		    j = order[i];
		    if ((j == jstar) && (vec[j] > -1)) 
			midp[j] = 1 / (1 + Math.exp
				       (params[0][vec[j]] + 
					params[1][vec[j]] * age[vec[j]][k] * 
					params[2][code[k]]));
		    else if (vec[j] > -1) {
			midp[j] = 1 / (1 + Math.exp
				       (params[0][vec[j]] + 
					params[1][vec[j]] * age[vec[j]][k]));
		    }
		    else {
			midp[j] = integrators[j].
			    combineProbabilities(new double[][] { midp }); 
		    }
		}
		// extract correct entries and put them in outp
		outp[k] = midp[nodes[k]];
	    }
	    return outp;
	}
	public void build() {
	    vec = new int[size];
	    for (int i = 0; i < size; i++) {
		vec[i] = -1;
	    }
	    for (int i = 0; i < expander.length; i++) {
		vec[expander[i]] = i;
	    }
	}
	int n;
	String variablename;
	int[] vec;
	int[] expander;
	int[] order;
	double[][] age;
	int[][] indexarr;
	int[] nodes;
	int which0, which1;
	int[] code;
	int jstar;
    }

    public class ArbitraryNodeProbArgument implements ArgumentMaker {
	public ArbitraryNodeProbArgument 
	    ( ArgumentMaker[] argarray, int n, int[] expander, int[] order, 
	      int[] nodes) {
	    this (argarray, n, expander, nodes);
	    this.order = order;
	}
	public ArbitraryNodeProbArgument 
	    ( ArgumentMaker[] argarray, int n, int[] expander, int[] nodes) {
	    this.argarray = argarray;
	    this.n = n;
	    this.nodes = nodes;
	    this.expander = expander;
	    // here's where we need to develop a data structure that 
	    // makes probability calculations more efficient by only 
	    // computing the nodes that are needed for the current data pt
	    order = new int[size];
	    for (int i = 0; i < size; i++) {
		order[i] = i;
	    }
	    if (expander.length == size) {
		vec = expander;
	    }
	    else {		
		build();
	    }
	}
	public double[] getArgument (double[][] params) {
	    double[] outp = new double[n];
	    double[] midp = new double[size];
	    double[][] argvalues = new double[argarray.length][];
	    for (int h = 0; h < argarray.length; h++ ) {
			argvalues[h] = argarray[h].getArgument (params);
	    }
	    for (int k = 0; k < n; k++) {
		// because code is inefficient, compute entire prob network
			int j;
			for (int i = 0; i < size; i++) {
				j = order[i];
				if (vec[j] > -1) {
					//System.out.println(j + " , " + k + "," + vec[j]);
					//System.out.println(argvalues[vec[j]].length);
					midp[j] = argvalues[vec[j]][k];
				} else {
					midp[j] = integrators[j].
					combineProbabilities(new double[][] { midp }); 
					//printrealvector ( new double[] { j, midp[j] } );
				}
			}
			// extract correct entries and put them in outp
			//for (int l = 0; l < n; l++) {
			int l = k;
			outp[l] = midp[nodes[l]];
	    }
	    return outp;
	}
	
	public void build() {
	    vec = new int[size];
	    for (int i = 0; i < size; i++) {
		vec[i] = -1;
	    }
	    for (int i = 0; i < expander.length; i++) {
		vec[expander[i]] = i;
	    }
	}
	
	ArgumentMaker[] argarray;
	int n;
	int[] vec;
	int[] expander;
	int[] order;
	int[] nodes;
    }

    public class ArbitraryNodeProbArgumentBNHack implements ArgumentMaker {
	public ArbitraryNodeProbArgumentBNHack 
	    ( ArgumentMaker[] argarray, int n, int[] expander, int[] order, 
	      int[] nodes, double[] condprobs ) {
	    this (argarray, n, expander, nodes, condprobs);
	    this.order = order;
	}
	public ArbitraryNodeProbArgumentBNHack 
	    ( ArgumentMaker[] argarray, int n, int[] expander, int[] nodes,
		  double[] condprobs ) {
	    this.argarray = argarray;
	    this.n = n;
	    this.nodes = nodes;
	    this.expander = expander;
		this.condprobs = condprobs;
	    // here's where we need to develop a data structure that 
	    // makes probability calculations more efficient by only 
	    // computing the nodes that are needed for the current data pt
	    order = new int[size];
	    for (int i = 0; i < size; i++) {
		order[i] = i;
	    }
	    if (expander.length == size) {
		vec = expander;
	    }
	    else {		
		build();
	    }
	}
	public double[] getArgument (double[][] params) {
	    double[] outp = new double[n];
	    double[] midp = new double[size];
	    double[][] argvalues = new double[argarray.length][];
	    for (int h = 0; h < argarray.length; h++ ) {
		argvalues[h] = argarray[h].getArgument (params);
	    }
	    for (int k = 0; k < n; k++) {
		// because code is inefficient, compute entire prob network
		int j;
		for (int i = 0; i < size; i++) {
		    j = order[i];
		    if (vec[j] > -1) {
			//System.out.println(j + " , " + k + "," + vec[j]);
			//System.out.println(argvalues[vec[j]].length);
			midp[j] = argvalues[vec[j]][k];
		    } else if (j == 0) {
				double[] cp = condprobs;
				double p1 = midp[1];
				double p2 = midp[2];
				double p3 = midp[3];
				midp[0] = cp[0] * p1 * p2 * p3 + 
						  cp[1] * (1.-p1) * p2 * p3 +
						  cp[2] * p1 * (1.-p2) * p3 + 
						  cp[3] * p1 * p2 * (1.-p3) + 
						  cp[4] * (1.-p1) * (1.-p2) * p3 + 
						  cp[5] * p1 * (1.-p2) * (1.-p3) + 
						  cp[6] * (1.-p1) * p2 * (1.-p3) + 
						  cp[7] * (1.-p1) * (1.-p2) * (1.-p3);
			} else {
				midp[j] = integrators[j].
				combineProbabilities(new double[][] { midp }); 
			//printrealvector ( new double[] { j, midp[j] } );
		    }
		}
		// extract correct entries and put them in outp
		//for (int l = 0; l < n; l++) {
		int l = k;
		outp[l] = midp[nodes[l]];
	    }
	    return outp;
	}
	public void build() {
	    vec = new int[size];
	    for (int i = 0; i < size; i++) {
		vec[i] = -1;
	    }
	    for (int i = 0; i < expander.length; i++) {
		vec[expander[i]] = i;
	    }
	}
	
	ArgumentMaker[] argarray;
	int n;
	int[] vec;
	int[] expander;
	int[] order;
	int[] nodes;
	double[] condprobs;
    }

    /**
     * Add to the definition of the subsystem another integrator of  
     * some components into a subsystem.  
     */
    public void addSubsystem (ComponentIntegrator integrator) {
	integrators[integrator.getSubsystem()] = integrator;
    }

    public void setNames (String[] names) {
	componentNames = names;
    }

    public int length () {
	return size;
    }
    
    public int getSize () {
	return size;
    }

    public ComponentIntegrator getIntegrator (int i) {
	return integrators[i];
    }

    /**
     * This method returns an ArgumentMaker for use in a BasicMCMCBond, 
     * and this argument generates a vector of the success probabilities 
     * of all the nodes (not just the leaf nodes).  
     *
     * @param expander expands raw probabilities into probabilities for all 
     * components and subsystems.  For some reason it can be handled in 
     * two ways.  We eventually also want to be able to expand the entire 
     * probability vector into several copies.  
     * @param order Caution: the 'order' record is going to be easy to 
     * make mistakes with in the stupid way the code is currently 
     * implemented.  The first entry 
     * in the order vector is the first element whose p will be calculated, 
     * as opposed to indicating in which position the first component's p 
     * will be calculated, in case that makes any sense.  
     */
    public VJSystemProbArgument fillProbs (int[] expander, int[] order) {
	return new VJSystemProbArgument(expander, order);
    }

    public VJSystemProbArgument fillProbs (int[] expander) {
	return new VJSystemProbArgument(expander);
    }

	public DiagnosticSystemProbArgument diagnosticFillProbs 
		(int[] expander, int[] order, ArgumentMaker biasarg) {
			return new DiagnosticSystemProbArgument (
				expander, order, biasarg);
	}

    public LogitAgeProbArgument logisticAgeProbs
	(DataFrame d, String variablename, int[] expander, int[] order, 
	 int[] nodes, int which0, int which1) {
	return new LogitAgeProbArgument (d, variablename, expander, order,
					 nodes, which0, which1);
    }
    public LogitAgeProbArgument logisticAgeProbs
	(DataFrame d, String variablename, int[] expander, int[] nodes,
	 int which0, int which1) {
	return new LogitAgeProbArgument (d, variablename, expander, nodes, 
					 which0, which1);
    }

    public LogitAgeProbArgumentPlus logisticAgeProbsPlus
	(DataFrame d, String variablename, int[] expander, int[] order, 
	 int[] nodes, int which0, int which1, int[] code, int jstar) {
	return new LogitAgeProbArgumentPlus (d, variablename, expander, order,
					     nodes, which0, which1,
					     code, jstar);
    }
    public LogitAgeProbArgumentPlus logisticAgeProbsPlus
	(DataFrame d, String variablename, int[] expander, int[] nodes,
	 int which0, int which1, int[] code, int jstar) {
	return new LogitAgeProbArgumentPlus (d, variablename, expander, nodes, 
					     which0, which1, code, jstar);
    }

    public ArbitraryNodeProbArgument fillArbitraryProbs
	( ArgumentMaker[] argarray, int n, int[] expander, int[] order, 
	  int[] nodes) {
	return new ArbitraryNodeProbArgument 
	    ( argarray, n, expander, order, nodes );
    }
    
    public ArbitraryNodeProbArgument fillArbitraryProbs
	( ArgumentMaker[] argarray, int n, int[] expander, int[] nodes) {
	return new ArbitraryNodeProbArgument
	    ( argarray, n, expander, nodes );
    }

    public ArbitraryNodeProbArgumentBNHack fillArbitraryProbsBayesNetHack
	( ArgumentMaker[] argarray, int n, int[] expander, int[] order, 
	  int[] nodes, double[] condprobs) {
	return new ArbitraryNodeProbArgumentBNHack 
	    ( argarray, n, expander, order, nodes, condprobs );
    }
    
    /**
     * Define a system consisting only of series subsystems by reading 
     * the contents of a single array of integers.  The i'th element of 
     * the 'parents' array is the parent of node i: for example, if nodes 
     * 6 and 7 combine in series to form node 4, the 6th and 7th elements 
     * of 'parents' should both be 4.  The 'parent' of the full subsystem 
     * node should be -1.  
     */
    public void SeriesIntegratorsFromFile (int[] parents) {
	// loop over the parents array, putting stuff in an appropriate 
	// collection.  Find all children with the 
	// same parent, and make a SeriesIntegrator therefrom.  
	ArrayList[] al = new ArrayList[size];
	for (int i = 0; i < size; i++) {
	    al[i] = new ArrayList();
	}
	for (int i = 0; i < size; i++) {
	    if (parents[i] >= 0) {
		al[parents[i]].add(new Integer(i));
	    }
	}
	int[] children;
	for (int i = 0; i < size; i++) {
	    if (!al[i].isEmpty()) {
		children = new int[al[i].size()];
		for (int j = 0; j < al[i].size(); j++) {
		    children[j] = ((Integer)(al[i].get(j))).intValue();
		}
		/*
		  for (int k = 0; k < children.length; k++) {
		  System.out.print(children[k] + " ");
		  }
		  System.out.println(" make up subsystem " + i);
		*/
		addSubsystem (new SeriesIntegrator ( children, i ));
	    }
	}
    }

    /**
     * Define a system consisting only of series and parallel subsystems 
     * by reading 
     * the contents of two arrays of integers.  The i'th element of 
     * the 'parents' array is the parent of node i: for example, if nodes 
     * 6 and 7 combine in series to form node 4, the 6th and 7th elements 
     * of 'parents' should both be 4.  The 'parent' of the full subsystem 
     * node should be -1.  The 'gate' array should contain positive numbers 
     * where components combine in series, are nonpositive numbers for 
     * parallel.  
     */
    public void SPIntegratorsFromFile (int[] parents, int[] gate) {
	// loop over the parents array, putting stuff in an appropriate 
	// collection.  Find all children with the 
	// same parent, and make an Integrator therefrom.
	// gate > 0 means series, gate <= 0 means parallel
	// This method makes SeriesIntegratorsFromFile deprecated
	ArrayList[] al = new ArrayList[size];
	for (int i = 0; i < size; i++) {
	    al[i] = new ArrayList();
	}
	for (int i = 0; i < size; i++) {
	    if (parents[i] >= 0) {
		al[parents[i]].add(new Integer(i));
	    }
	}
	int[] children;
	for (int i = 0; i < size; i++) {
	    if (!al[i].isEmpty()) {
		children = new int[al[i].size()];
		for (int j = 0; j < al[i].size(); j++) {
		    children[j] = ((Integer)(al[i].get(j))).intValue();
		}
		/*
		  for (int k = 0; k < children.length; k++) {
		  System.out.print(children[k] + " ");
		  }
		  System.out.println(" make up subsystem " + i);
		*/
		if (gate[i] > 0) {
		    addSubsystem (new SeriesIntegrator ( children, i ));
		} else {
		    addSubsystem (new ParallelIntegrator ( children, i));
		}
	    }
	}
    }

    public static void printrealvector (double[] vec) {
	String str = "";
	for (int i = 0; i < vec.length; i++) {
	    str += vec[i] + " ";
	}
	System.out.println(str);
    }

    public static void main (String[] args) {
	ReliableSystem testsys = new ReliableSystem (5);
	testsys.addSubsystem (new SeriesIntegrator(new int[] {3,4}, 2));
	testsys.addSubsystem (new ParallelIntegrator(new int[] {1,2}, 0));
	ArgumentMaker arg = testsys.logisticAgeProbs 
	    ( new DataFrame ("h:/projects/stinger/fake/crap.dat"), "age",
			     new int[] {-1,0,-1,1,2}, 
			     new int[] {4,3,2,1,0}, 
			     new int[] {0,1,2,3,4,0,1,2,3,4}, 0, 1);
	printrealvector (arg.getArgument 
			 ( new double[][] { new double[] {-0.5, 0, 0.5},
					    new double[] {0.75, 0, 0.75} }));
    }

    int size;
    ComponentIntegrator[] integrators;
    String[] componentNames;

}

