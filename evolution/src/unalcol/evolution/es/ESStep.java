package unalcol.evolution.es;

import unalcol.search.Goal;
import unalcol.search.population.Population;
import unalcol.search.population.RealQualifyPopulationSearch;
import unalcol.search.population.VariationReplacePopulationSearch;
import unalcol.search.space.Space;
import unalcol.search.space.variation.BuildOne;
import unalcol.search.variation.ArityOneSearchOperator;

public class ESStep<T,P> extends VariationReplacePopulationSearch<T,Double> implements RealQualifyPopulationSearch<T>{
	protected Space<P> s_space;
	public ESStep(int mu, int lambda, int ro, 
       		BuildOne<T> y_recombination, ArityOneSearchOperator<T> mutation, 
       		BuildOne<P> s_recombination, ArityOneSearchOperator<P> s_mutation, Space<P> s_space,
       		ESReplacement<T> replacement ){
		super( 	mu, new ESVariation<T,P>(lambda, ro, y_recombination, mutation, s_recombination, s_mutation), 
				replacement);
		this.s_space = s_space;
	}

	public ESStep(int mu, int lambda, int ro, 
       		BuildOne<T> y_recombination, ArityOneSearchOperator<T> mutation, 
       		BuildOne<P> s_recombination, ArityOneSearchOperator<P> s_mutation, Space<P> s_space,
       		boolean plus_replacement ){
		this(	mu, lambda, ro, y_recombination, mutation, s_recombination, s_mutation, s_space, 
				plus_replacement? new PlusReplacement<T>(mu):new CommaReplacement<T>(mu) );
	}
	
	@Override
	public Population<T> init(Space<T> space, Goal<T, Double> goal) {
    	Population<T> pop = super.init(space, goal);
    	for( int i=0; i<pop.size(); i++ ){
    		pop.get(i).set(ESVariation.PARAMETERS_OPERATOR, s_space.pick() );
    	}
    	return pop;
	}
}