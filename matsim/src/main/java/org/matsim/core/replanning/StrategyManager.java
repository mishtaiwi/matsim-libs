/* *********************************************************************** *
 * project: org.matsim.*
 * StrategyManager.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007, 2009 by the members listed in the COPYING,  *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.core.replanning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.matsim.api.core.v01.population.HasPlansAndId;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.api.internal.MatsimManager;
import org.matsim.core.replanning.selectors.GenericPlanSelector;
import org.matsim.core.replanning.selectors.WorstPlanForRemovalSelector;

/**
 * Manages and applies strategies to agents for re-planning.
 *
 * @author mrieser
 */
public class StrategyManager implements MatsimManager {
	
	GenericStrategyManager<Plan> delegate = new GenericStrategyManager<Plan>() ;

	/**
	 * @param name the name of the subpopulation attribute
	 * in the person's object attributes.
	 */
	public final void setSubpopulationAttributeName(final String name) {
		delegate.setSubpopulationAttributeName(name);
	}

	@Deprecated
	public final void addStrategyForDefaultSubpopulation(
			final PlanStrategy strategy,
			final double weight) {
		addStrategy( strategy , null , weight );
	}

	/**
	 * Adds a strategy to this manager with the specified weight. This weight
	 * compared to the sum of weights of all strategies in this manager defines
	 * the probability this strategy will be used for an agent.
	 *
	 * @param strategy
	 * @param weight
	 */
	public final void addStrategy(
			final PlanStrategy strategy,
			final String subpopulation,
			final double weight) {
		delegate.addStrategy(strategy, subpopulation, weight);
	}


	@Deprecated
	public final boolean removeStrategyForDefaultSubpopulation(
			final PlanStrategy strategy) {
		return removeStrategy( strategy , null );
	}

	/**
	 * removes the specified strategy from this manager for the specified subpopulation
	 *
	 * @param strategy the strategy to be removed
	 * @param subpopulation the subpopulation for which the strategy must be removed
	 * @return true if the strategy was successfully removed from this manager,
	 * 		false if the strategy was not part of this manager and could thus not be removed.
	 */
	public final boolean removeStrategy(
			final PlanStrategy strategy,
			final String subpopulation) {
		return delegate.removeStrategy(strategy, subpopulation) ;
	}

	@Deprecated
	public final boolean changeWeightOfStrategyForDefaultSubpopulation(
			final GenericPlanStrategy<Plan> strategy,
			final double newWeight) {
		return changeWeightOfStrategy( strategy , null , newWeight );
	}

	/**
	 * changes the weight of the specified strategy
	 *
	 * @param strategy
	 * @param newWeight
	 * @return true if the strategy is part of this manager and the weight could
	 * 		be changed successfully, false otherwise.
	 */
	public final boolean changeWeightOfStrategy(
			final GenericPlanStrategy<Plan> strategy,
			final String subpopulation,
			final double newWeight) {
		return delegate.changeWeightOfStrategy(strategy, subpopulation, newWeight) ;
	}

	/**
	 * Randomly chooses for each person of the population a strategy and uses that
	 * strategy on the person, after adapting the strategies to any pending change
	 * requests for the specified iteration.
	 *
	 * @param population
	 * @param iteration the current iteration we're handling
	 * @param replanningContext 
	 */
	public final void run(final Population population, final int iteration, final ReplanningContext replanningContext) {
		// (this is not directly delegated since the run method of this StrategyManager includes two "hooks").
		delegate.handleChangeRequests(iteration);	
		run(population, replanningContext);
	}

	protected void beforePopulationRunHook( Population population, ReplanningContext replanningContext ) {
		// left empty for inheritance
	}

	/**
	 * Randomly chooses for each person of the population a strategy and uses that
	 * strategy on the person.
	 *
	 * @param population
	 * @param replanningContext 
	 */
	public final void run( final Population population, final ReplanningContext replanningContext) {
		beforePopulationRunHook( population, replanningContext ) ;
		
		// if anybody has a better idea of how to do the following, please go ahead, kai, nov'13
		Collection<HasPlansAndId<Plan>> members = new ArrayList<HasPlansAndId<Plan>>() ;
		for ( Person person : population.getPersons().values() ) {
			members.add(person) ;
		}
		delegate.run( members, population.getPersonAttributes(), replanningContext ) ;
		
//		delegate.run( (Collection<HasPlansAndId<Plan>>)population.getPersons().values(), population.getPersonAttributes(), replanningContext);
		// not sure why I need to cast this but I think in this case the <? extends Person> backfires. kai, nov'13
		// compiles under eclipse, but not on the build server. kai, nov'13

		afterRunHook( population ) ;
	}

	protected void afterRunHook( Population population ) {
		// left empty for inheritance
	}


	/**
	 * chooses a (weight-influenced) random strategy
	 *
	 * @param person The person for which the strategy should be chosen
	 * @return the chosen strategy
	 */
	public GenericPlanStrategy<Plan> chooseStrategy(final Person person, final String subpopulation) {
		return delegate.chooseStrategy(person,subpopulation) ;
	}

	/**
	 * Sets the maximal number of plans an agent can memorize. Setting
	 * maxPlansPerAgent to zero means unlimited memory (only limited by RAM).
	 * Agents can have up to maxPlansPerAgent plans plus one additional one with the
	 * currently modified plan they're trying out.
	 *
	 * @param maxPlansPerAgent
	 */
	public final void setMaxPlansPerAgent(final int maxPlansPerAgent) {
		delegate.setMaxPlansPerAgent(maxPlansPerAgent);
	}

	public final int getMaxPlansPerAgent() {
		return delegate.getMaxPlansPerAgent() ;
	}

	@Deprecated
	public final void addChangeRequestForDefaultSubpopulation(
			final int iteration,
			final PlanStrategy strategy,
			final double newWeight) {
		addChangeRequest( iteration , strategy , null , newWeight );
	}

	/**
	 * Schedules a {@link #changeStrategy changeStrategy(Strategy, subpopulation, double)} command for a later iteration. The
	 * change will take place before the strategies are applied.
	 *
	 * @param iteration
	 * @param strategy
	 * @param newWeight
	 */
	public final void addChangeRequest(
			final int iteration,
			final PlanStrategy strategy,
			final String subpopulation,
			final double newWeight) {
//		final StrategyWeights weights = getStrategyWeights( subpopulation );
//		Integer iter = Integer.valueOf(iteration);
//		Map<PlanStrategy, Double> iterationRequests = weights.changeRequests.get(iter);
//		if (iterationRequests == null) {
//			iterationRequests = new HashMap<PlanStrategy, Double>(3);
//			weights.changeRequests.put(iter, iterationRequests);
//		}
//		iterationRequests.put(strategy, Double.valueOf(newWeight));
		delegate.addChangeRequest(iteration, strategy, subpopulation, newWeight);
	}

	/**
	 * Sets a plan selector to be used for choosing plans for removal, if they
	 * have more plans than the specified maximum. This defaults to
	 * {@link WorstPlanForRemovalSelector}.
	 * <p/>
	 * Thoughts about using the logit-type selectors with negative logit model scale parameter:<ul>
	 * <li> Look at one agent.
	 * <li> Assume she has the choice between <i>n</i> different plans.
	 * <li> (Continuous) fraction <i>f(i)</i> of plan <i>i</i> develops as (master equation) 
	 * <blockquote><i>
	 * df(i)/dt = - p(i) * f(i) + 1/n
	 * </i></blockquote>
	 * where <i>p(i)</i> is from the choice model. 
	 * <li> Steady state solution (<i>df/dt=0</i>) <i> f(i) = 1/n * 1/p(i) </i>.
	 * <li> If <i> p(i) = e<sup>-b*U(i)</sup></i>, then <i> f(i) = e<sup>b*U(i)</sup> / n </i>.  Or in words:
	 * <i><b> If you use a logit model with a minus in front of the beta for plans removal, the resulting steady state distribution is
	 * the same logit model with normal beta.</b></i> 
	 * 
	 * </ul>
	 * The implication seems to be: divide the user-configured beta by two, use one half for choice and the other for plans removal.
	 * <p/>
	 * The path size version still needs to be tested (both for choice and for plans removal).
	 *
	 * @param planSelector
	 *
	 * @see #setMaxPlansPerAgent(int)
	 */
	public final void setPlanSelectorForRemoval(final GenericPlanSelector<Plan> planSelector) {
//		Logger.getLogger(this.getClass()).info("setting PlanSelectorForRemoval to " + planSelector.getClass() ) ;
//		this.removalPlanSelector = planSelector;
		delegate.setPlanSelectorForRemoval(planSelector);
	}

	@Deprecated
	public final List<GenericPlanStrategy<Plan>> getStrategiesOfDefaultSubpopulation() {
		return getStrategies( null );
	}

	public final List<GenericPlanStrategy<Plan>> getStrategies( final String subpopulation ) {
//		return getStrategyWeights( subpopulation ).unmodifiableStrategies;
		return delegate.getStrategies(subpopulation) ;
	}

	/**
	 * @return the weights of the strategies for the default subpopulation, in the same order as the strategies returned by {@link #getStrategiesOfDefaultSubpopulation()}
	 */
	@Deprecated
	public final List<Double> getWeightsOfDefaultSubpopulation() {
		return getWeights( null );
	}

	/**
	 * @return the weights of the strategies for the given subpopulation, in the same order as the strategies returned by {@link #getStrategies()}
	 */
	public final List<Double> getWeights( final String subpopulation ) {
//		return getStrategyWeights( subpopulation ).unmodifiableWeights;
		return delegate.getWeights( subpopulation ) ;
	}
}
