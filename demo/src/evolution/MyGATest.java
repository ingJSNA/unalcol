package evolution;

import java.util.Hashtable;
import java.util.Map.Entry;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import sun.nio.cs.HistoricallyNamedCharset;
import unalcol.descriptors.Descriptors;
import unalcol.descriptors.WriteDescriptors;
import unalcol.evolution.EAFactory;
import unalcol.io.Write;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.optimization.binary.BinarySpace;
import unalcol.optimization.binary.BitMutation;
import unalcol.optimization.binary.XOver;
import unalcol.optimization.binary.testbed.MaxOnes;
import unalcol.optimization.real.BinaryToRealVector;
import unalcol.optimization.real.HyperCube;
import unalcol.optimization.real.mutation.IntensityMutation;
import unalcol.optimization.real.mutation.PermutationPick;
import unalcol.optimization.real.mutation.PickComponents;
import unalcol.optimization.real.testbed.Rastrigin;
import unalcol.optimization.real.xover.LinearXOver;
import unalcol.optimization.real.xover.RealArityTwo;
import unalcol.random.real.DoubleGenerator;
import unalcol.random.real.SimplestSymmetricPowerLawGenerator;
import unalcol.search.Goal;
import unalcol.search.multilevel.CodeDecodeMap;
import unalcol.search.multilevel.MultiLevelSearch;
import unalcol.search.population.Population;
import unalcol.search.population.PopulationDescriptors;
import unalcol.search.population.PopulationSearch;
import unalcol.search.selection.Roulette;
import unalcol.search.selection.Selection;
import unalcol.search.selection.Tournament;
import unalcol.search.solution.Solution;
import unalcol.search.space.Space;
import unalcol.search.variation.Variation_1_1;
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.Tracer;
import unalcol.types.collection.bitarray.BitArray;
import unalcol.types.real.array.DoubleArray;
import unalcol.types.real.array.DoubleArrayPlainWrite;

public class MyGATest {

	public static void real() {
		// Search Space definition
		int DIM = 10;
		double[] min = DoubleArray.create(DIM, -5.12);
		double[] max = DoubleArray.create(DIM, 5.12);
		Space<double[]> space = new HyperCube(min, max);

		// Optimization Function
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
		PickComponents pick = new PermutationPick(DIM / 2); // It can be set to
															// null if the
															// mutation operator
															// is applied to
															// every component
															// of the solution
															// array
		// AdaptMutationIntensity adapt = new OneFifthRule(500, 0.9); // It can
		// be set to null if no mutation adaptation is required
		IntensityMutation mutation = new IntensityMutation(0.1, random, pick);
		RealArityTwo xover = new LinearXOver();

		// Search method
		int POPSIZE = 100;
		int MAXITERS = 100;
		EAFactory<double[]> factory = new EAFactory<double[]>();
		PopulationSearch<double[], Double> search = factory.generational_ga(
				POPSIZE, new Tournament<double[]>(4), mutation, xover, 0.0,
				MAXITERS);

		// Tracking the goal evaluations
		// WriteDescriptors write_desc = new WriteDescriptors();
		Write.set(double[].class, new DoubleArrayPlainWrite(false));
		Descriptors
				.set(Population.class, new PopulationDescriptors<double[]>());

		ConsoleTracer tracer = new ConsoleTracer();
		// Tracer.addTracer(goal, tracer); // Uncomment if you want to trace the
		// function evaluations
		Tracer.addTracer(search, tracer); // Uncomment if you want to trace the
											// hill-climbing algorithm

		// Apply the search method
		Solution<double[]> solution = search.solve(space, goal);

		System.out.println(solution.info(Goal.class.getName()));
	}

	public static void schemeTheorem() {
		// Search Space definition
		int DIM = 24;
		Space<BitArray> space = new BinarySpace(DIM);

		// Optimization Function
		OptimizationFunction<BitArray> function = new MaxOnes();
		Goal<BitArray, Double> goal = new OptimizationGoal<BitArray>(function,
				false); // maximizing, remove the parameter false if minimizing

		// Sel method
		int POPSIZE = 100;
		Selection<BitArray> selection = new Roulette<BitArray>();

		BitArray[] pop = space.pick(POPSIZE);

		// TODO create a for with pick
		@SuppressWarnings("unchecked")
		Solution<BitArray>[] solution = new Solution[pop.length];
		for (int i = 0; i < pop.length; i++) {
			solution[i] = new Solution<BitArray>(pop[i], goal);
		}

		int MAX_ITER = 100;

		for (int i = 0; i < MAX_ITER; i++) {
			solution = selection.pick(POPSIZE, solution);
			if (i % 10 == 0) {
				System.out.println("##### " + i + " ####");
				histogramHashtable(solution);
			}
		}

	}

	private static int[] histogram(BitArray[] pop) {
		int[] h = new int[pop.length];
		for (int i = 0; i < pop.length; i++) {
			for (int j = i + 1; j < h.length; j++) {
				if (pop[i].equals(pop[j])) {
					h[i]++;
				}
			}
		}
		return h;
	}

	private static Hashtable<Solution<BitArray>, Integer> histogramHashtable(
			Solution<BitArray>[] pop) {
		Hashtable<Solution<BitArray>, Integer> hash = new Hashtable<Solution<BitArray>, Integer>();
		for (int i = 0; i < pop.length; i++) {
			Integer k = hash.get(pop[i]);
			hash.put(pop[i], k != null ? (k + 1) : 1);
		}

		// Imprimir
		for (Entry<Solution<BitArray>, Integer> entry : hash.entrySet()) {
			System.out.println(String.format("%d: %s", entry.getKey(),
					entry.getValue()));
		}

		return hash;
	}

	public static void binary() {
		// Search Space definition
		int DIM = 24;
		Space<BitArray> space = new BinarySpace(DIM);

		// Optimization Function
		OptimizationFunction<BitArray> function = new MaxOnes();
		Goal<BitArray, Double> goal = new OptimizationGoal<BitArray>(function,
				false); // maximizing, remove the parameter false if minimizing

		// Variation definition
		Variation_1_1<BitArray> mutation = new BitMutation();
		XOver xover = new XOver();

		// Search method
		int POPSIZE = 100;
		int MAXITERS = 100;
		EAFactory<BitArray> factory = new EAFactory<BitArray>();
		PopulationSearch<BitArray, Double> search = factory.generational_ga(
				POPSIZE, new Roulette<BitArray>(), mutation, xover, 0.0,
				MAXITERS);

		// Tracking the goal evaluations
		WriteDescriptors write_desc = new WriteDescriptors();
		Write.set(double[].class, new DoubleArrayPlainWrite(false));
		Write.set(Population.class, write_desc);
		Descriptors
				.set(Population.class, new PopulationDescriptors<BitArray>());

		ConsoleTracer tracer = new ConsoleTracer();
		// Tracer.addTracer(goal, tracer); // Uncomment if you want to trace the
		// function evaluations
		Tracer.addTracer(search, tracer); // Uncomment if you want to trace the
											// hill-climbing algorithm

		// Apply the search method
		Solution<BitArray> solution = search.solve(space, goal);

		System.out.println(solution.info(Goal.class.getName()));
		// Remove for general use
		Glovito g = new Glovito(solution.object());
		System.out.println(g.toString());
	}

	public static void binary2real() {
		// Search Space definition
		int DIM = 10;
		double[] min = DoubleArray.create(DIM, -5.12);
		double[] max = DoubleArray.create(DIM, 5.12);
		Space<double[]> space = new HyperCube(min, max);

		// Optimization Function
		OptimizationFunction<double[]> function = new Rastrigin();
		Goal<double[], Double> goal = new OptimizationGoal<double[]>(function); // minimizing,
																				// add
																				// the
																				// parameter
																				// false
																				// if
																				// maximizing

		// CodeDecodeMap
		int BITS_PER_DOUBLE = 16; // Number of bits per integer (i.e. per real)
		CodeDecodeMap<BitArray, double[]> map = new BinaryToRealVector(
				BITS_PER_DOUBLE, min, max);

		// Variation definition
		Variation_1_1<BitArray> mutation = new BitMutation();
		XOver xover = new XOver();

		// Search method
		int POPSIZE = 100;
		int MAXITERS = 10;
		EAFactory<BitArray> factory = new EAFactory<BitArray>();
		PopulationSearch<BitArray, Double> bin_search = factory
				.generational_ga(POPSIZE, new Tournament<BitArray>(4),
						mutation, xover, 0.6, MAXITERS);

		// The multilevel search method (moves in the binary space, but computes
		// fitness in the real space)
		MultiLevelSearch<BitArray, double[], Double> search = new MultiLevelSearch<BitArray, double[], Double>(
				bin_search, map);

		// Tracking the goal evaluations
		// WriteDescriptors write_desc = new WriteDescriptors();
		Write.set(double[].class, new DoubleArrayPlainWrite(false));
		Descriptors
				.set(Population.class, new PopulationDescriptors<BitArray>());

		ConsoleTracer tracer = new ConsoleTracer();
		Tracer.addTracer(goal, tracer); // Uncomment if you want to trace the
										// function evaluations
		// Tracer.addTracer(search, tracer); // Uncomment if you want to trace
		// the hill-climbing algorithm

		// Apply the search method
		Solution<double[]> solution = search.solve(space, goal);

		System.out.println(solution.info(Goal.class.getName()));

	}

	public static void main(String[] args) {
		// real(); // Uncomment if testing real valued functions
		// binary(); // Uncomment if testing binary valued functions
		// binary2real(); // Uncomment if you want to try the multi-level search
		// method
		// queen(); // Uncomment if testing integer (queen) value functions
		schemeTheorem();
	}

}
