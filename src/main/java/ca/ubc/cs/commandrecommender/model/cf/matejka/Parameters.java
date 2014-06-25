package ca.ubc.cs.commandrecommender.model.cf.matejka;

/**
 * Parameters for {@link org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer}
 * used for {@link org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender} in
 * {@link ca.ubc.cs.commandrecommender.generator.LatentModelBasedCFRecGen}
 */
public class Parameters {

	public static double alpha = 20.0;
	public static int numIterations=100;
	public static int lambda=10;
	public static int numFeatures=20;
	
}
