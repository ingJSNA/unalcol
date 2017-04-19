package optimization;

import unalcol.descriptors.Descriptors;
import unalcol.descriptors.WriteDescriptors;
import unalcol.io.Write;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.optimization.binary.BinarySpace;
import unalcol.optimization.binary.BitMutation;
import unalcol.optimization.binary.testbed.Deceptive;
import unalcol.optimization.method.AdaptOperatorOptimizationFactory;
//import unalcol.optimization.binary.testbed.MaxOnes;
import unalcol.optimization.method.OptimizationFactory;
import unalcol.optimization.integer.IntHyperCube;
import unalcol.optimization.integer.MutationIntArray;
import unalcol.optimization.integer.testbed.QueenFitness;
import unalcol.optimization.real.BinaryToRealVector;
import unalcol.optimization.real.HyperCube;
import unalcol.optimization.real.mutation.IntensityMutation;
import unalcol.optimization.real.mutation.OneFifthRule;
import unalcol.optimization.real.mutation.PermutationPick;
import unalcol.optimization.real.mutation.PickComponents;
import unalcol.optimization.real.mutation.PowerLawMutation;
import unalcol.optimization.real.testbed.Rastrigin;
//import unalcol.optimization.real.testbed.Rastrigin;
import unalcol.optimization.real.testbed.Schwefel;
import unalcol.random.real.DoubleGenerator;
import unalcol.random.real.PowerLawGenerator;
import unalcol.random.real.SimplestPowerLawGenerator;
import unalcol.random.real.SimplestSymmetricPowerLawGenerator;
import unalcol.random.real.StandardGaussianGenerator;
import unalcol.random.real.StandardPowerLawGenerator;
import unalcol.reflect.tag.TaggedObject;
import unalcol.search.Goal;
import unalcol.search.local.LocalSearch;
import unalcol.search.solution.Solution;
import unalcol.search.solution.SolutionDescriptors;
import unalcol.search.solution.SolutionWrite;
import unalcol.search.multilevel.CodeDecodeMap;
import unalcol.search.multilevel.MultiLevelSearch;
import unalcol.search.space.Space;
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.Tracer;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.integer.array.IntArray;
import unalcol.types.integer.array.IntArrayPlainWrite;
import unalcol.types.real.Statistics;
import unalcol.types.real.array.DoubleArray;
import unalcol.types.real.array.DoubleArrayPlainWrite;

public class MyHillClimbingTest {

	public static void real() {
		// Search Space definition
		int DIM = 10;
		double[] min = DoubleArray.create(DIM, -5.12);
		double[] max = DoubleArray.create(DIM, 5.12);
		Space<double[]> space = new HyperCube(min, max);

		// Optimization Function
		// OptimizationFunction<double[]> function = new Schwefel();
		OptimizationFunction<double[]> function = new Rastrigin();
		Goal<double[], Double> goal = new OptimizationGoal<double[]>(function); // minimizing,
																				// add
																				// the
																				// parameter
																				// false
																				// if
																				// maximizing

		// Variation definition
		DoubleGenerator random = new SimplestSymmetricPowerLawGenerator(); // It
																			// can
																			// be
																			// set
																			// to
																			// Gaussian
																			// or
																			// other
																			// symmetric
																			// number
																			// generator
																			// (centered
																			// in
																			// zero)
		PickComponents pick = new PermutationPick(6); // It can be set to null
														// if the mutation
														// operator is applied
														// to every component of
														// the solution array
		IntensityMutation variation = new IntensityMutation(0.1, random, pick);

		// Search method
		int MAXITERS = 10_000;
		boolean neutral = true; // Accepts movements when having same function
								// value
		boolean adapt_operator = true; //
		LocalSearch<double[], Double> search;
		if (adapt_operator) {
			OneFifthRule adapt = new OneFifthRule(20, 0.9); // One Fifth rule
															// for adapting the
															// mutation
															// parameter
			AdaptOperatorOptimizationFactory<double[], Double> factory = new AdaptOperatorOptimizationFactory<double[], Double>();
			search = factory.hill_climbing(variation, adapt, neutral, MAXITERS);
		} else {
			OptimizationFactory<double[]> factory = new OptimizationFactory<double[]>();
			search = factory.hill_climbing(variation, neutral, MAXITERS);
		}
		// Tracking the goal evaluations
		SolutionDescriptors<double[]> desc = new SolutionDescriptors<double[]>();
		Descriptors.set(TaggedObject.class, desc);
		DoubleArrayPlainWrite write = new DoubleArrayPlainWrite(false);
		Write.set(double[].class, write);
		SolutionWrite<double[]> w_desc = new SolutionWrite<double[]>(true);
		Write.set(TaggedObject.class, w_desc);

		ConsoleTracer tracer = new ConsoleTracer();
		// Tracer.addTracer(goal, tracer); // Uncomment if you want to trace the
		// function evaluations
		Tracer.addTracer(search, tracer); // Uncomment if you want to trace the
											// hill-climbing algorithm

		// Apply the search method
		TaggedObject<double[]> solution = search.solve(space, goal);

		System.out.println(solution.info(Goal.class.getName()));
	}

	public static double realPowerLaw() {
		// Search Space definition
		int DIM = 10;
		double[] min = DoubleArray.create(DIM, -5.12);
		double[] max = DoubleArray.create(DIM, 5.12);
		Space<double[]> space = new HyperCube(min, max);

		// Optimization Function
		// OptimizationFunction<double[]> function = new Schwefel();
		OptimizationFunction<double[]> function = new Rastrigin();
		Goal<double[], Double> goal = new OptimizationGoal<double[]>(function); // minimizing,
																				// add
																				// the
																				// parameter
																				// false
																				// if
																				// maximizing

		// Variation definition
		DoubleGenerator random;
		// random = new SimplestPowerLawGenerator(); // It can be set to
		// Gaussian or other symmetric number generator (centered in zero)
		random = new StandardGaussianGenerator();
		PickComponents pick = null;
		pick = new PermutationPick(6); // It can be set to null if the
		// mutation operator is applied to every component of the solution array
		IntensityMutation variation = new IntensityMutation(0.1, random, pick);

		// Search method
		int MAXITERS = 100_000;
		boolean neutral = true; // Accepts movements when having same function
								// value
		boolean adapt_operator = true; //
		LocalSearch<double[], Double> search;
		if (adapt_operator) {
			OneFifthRule adapt = new OneFifthRule(20, 0.9); // One Fifth rule
															// for adapting the
															// mutation
															// parameter
			AdaptOperatorOptimizationFactory<double[], Double> factory = new AdaptOperatorOptimizationFactory<double[], Double>();
			search = factory.hill_climbing(variation, adapt, neutral, MAXITERS);
		} else {
			OptimizationFactory<double[]> factory = new OptimizationFactory<double[]>();
			search = factory.hill_climbing(variation, neutral, MAXITERS);
		}
		// Tracking the goal evaluations
		SolutionDescriptors<double[]> desc = new SolutionDescriptors<double[]>();
		Descriptors.set(TaggedObject.class, desc);
		DoubleArrayPlainWrite write = new DoubleArrayPlainWrite(false);
		Write.set(double[].class, write);
		SolutionWrite<double[]> w_desc = new SolutionWrite<double[]>(true);
		Write.set(TaggedObject.class, w_desc);

		ConsoleTracer tracer = new ConsoleTracer();
		// Tracer.addTracer(goal, tracer); // Uncomment if you want to trace the
		// function evaluations
		// Tracer.addTracer(search, tracer); // Uncomment if you want to trace
		// the hill-climbing algorithm

		// Apply the search method
		TaggedObject<double[]> solution = search.solve(space, goal);

		// System.out.println(solution.info(Goal.class.getName()));

		// return the final solution (fitness)
		return (double) solution.info(Goal.class.getName());
	}

	public static void experiment() {
		int runs = 30;
		double[] x = new double[runs];

		double avg = 0.0;
		for (int r = 0; r < runs; r++) {
			x[r] = realPowerLaw();
			System.out.println("run " + r + ": " + x[r]);
			avg += x[r];
		}

		Statistics stats = new Statistics(x);

		System.out.println("mean: " + stats.avg);

		avg /= runs;
		DoubleArray.merge(x);
		double median = x[runs / 2];
		System.out.println("median: " + median);

		double v_avg = 0.0;
		double v_median = 0.0;

		for (int i = 0; i < x.length; i++) {
			v_avg += (x[i] - avg) * (x[i] - avg);
			v_median += (x[i] - median) * (x[i] - median);
		}

		// Standart deviation
		double s_avg = Math.sqrt(v_avg / (runs - 1));
		double s_median = Math.sqrt(v_median / (runs - 1));

		System.out.println("Using mean: " + avg + "+/-" + s_avg);
		System.out.println("Using median: " + median + "+/-" + s_median);
	}

	public static void main(String[] args) {
		// real(); // Uncomment if testing real valued functions
		// binary(); // Uncomment if testing binary valued functions
		// binary2real(); // Uncomment if you want to try the multi-level search
		// method
		// queen(); // Uncomment if testing queens (integer) value functions

		experiment();
	}
}