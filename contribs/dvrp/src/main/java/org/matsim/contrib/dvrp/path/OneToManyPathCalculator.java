/*
 * *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2020 by the members listed in the COPYING,        *
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
 * *********************************************************************** *
 */

package org.matsim.contrib.dvrp.path;

import static org.matsim.contrib.dvrp.path.VrpPaths.FIRST_LINK_TT;
import static org.matsim.core.router.util.LeastCostPathCalculator.Path;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.IdMap;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.dvrp.path.OneToManyPathSearch.PathData;

import ch.sbb.matsim.routing.graph.LeastCostPathTree;

/**
 * @author Michal Maciejewski (michalm)
 */
class OneToManyPathCalculator {
	private final IdMap<Node, Node> nodeMap;
	private final LeastCostPathTree dijkstraTree;
	private final boolean forwardSearch;
	private final Link fromLink;
	private final double startTime;

	OneToManyPathCalculator(IdMap<Node, Node> nodeMap, LeastCostPathTree dijkstraTree, boolean forwardSearch,
			Link fromLink, double startTime) {
		this.nodeMap = nodeMap;
		this.dijkstraTree = dijkstraTree;
		this.forwardSearch = forwardSearch;
		this.fromLink = fromLink;
		this.startTime = startTime;
	}

	void calculateDijkstraTree(Collection<Link> toLinks) {
		int fromNodeIdx = getStartNode(fromLink).getId().index();
		Stream<Node> toNodes = toLinks.stream().filter(link -> link != fromLink).map(this::getEndNode);
		MultiNodeStopCriterion stopCriterion = new MultiNodeStopCriterion(toNodes);

		if (stopCriterion.counter > 0) {
			if (forwardSearch) {
				dijkstraTree.calculate(fromNodeIdx, startTime, null, null, stopCriterion);
			} else {
				dijkstraTree.calculateBackwards(fromNodeIdx, startTime, null, null, stopCriterion);
			}
		}
	}

	PathData createPathDataLazily(Link toLink) {
		if (toLink == fromLink) {
			Supplier<Path> pathSupplier = () -> new Path(List.of(getStartNode(fromLink)), List.of(), 0, 0);
			return new PathData(pathSupplier, 0, 0);
		} else {
			double pathTravelTime = getTravelTime(getEndNode(toLink).getId().index());
			Supplier<Path> pathSupplier = () -> createPath(getEndNode(toLink));
			return new PathData(pathSupplier, pathTravelTime,
					getFirstAndLastLinkTT(fromLink, toLink, pathTravelTime, startTime));
		}
	}

	PathData createPathDataEagerly(Link toLink) {
		if (toLink == fromLink) {
			return new PathData(new Path(List.of(getStartNode(fromLink)), List.of(), 0, 0), 0);
		} else {
			Path path = createPath(getEndNode(toLink));
			return new PathData(path, getFirstAndLastLinkTT(fromLink, toLink, path.travelTime, startTime));
		}
	}

	Path createPath(Node toNode) {
		var nodes = constructNodeSequence(dijkstraTree, toNode, forwardSearch);
		var links = constructLinkSequence(nodes);
		int toNodeIndex = toNode.getId().index();
		double travelTime = getTravelTime(toNodeIndex);
		double cost = dijkstraTree.getCost(toNodeIndex);
		return new Path(nodes, links, travelTime, cost);
	}

	private double getTravelTime(int toNodeIndex) {
		int travelTimeMultiplier = forwardSearch ? 1 : -1;
		return travelTimeMultiplier * (dijkstraTree.getTime(toNodeIndex).seconds() - startTime);
	}

	private List<Node> constructNodeSequence(LeastCostPathTree dijkstraTree, Node toNode, boolean forward) {
		ArrayList<Node> nodes = new ArrayList<>();
		nodes.add(toNode);

		int index = dijkstraTree.getComingFrom(toNode.getId().index());
		while (index >= 0) {
			nodes.add(nodeMap.get(Id.get(index, Node.class)));
			index = dijkstraTree.getComingFrom(index);
		}

		if (forward) {
			Collections.reverse(nodes);
		}
		return nodes;
	}

	private List<Link> constructLinkSequence(List<Node> nodes) {
		List<Link> links = new ArrayList<>(nodes.size() - 1);
		Node prevNode = nodes.get(0);
		for (int i = 1; i < nodes.size(); i++) {
			Node nextNode = nodes.get(i);
			for (Link link : prevNode.getOutLinks().values()) {
				//FIXME this method will not work properly if there are many prevNode -> nextNode links
				//TODO save link idx in tree OR pre-check: at most 1 arc per each node pair OR choose faster/better link
				if (link.getToNode() == nextNode) {
					links.add(link);
					break;
				}
			}
			prevNode = nextNode;
		}
		return links;
	}

	private Node getEndNode(Link link) {
		return forwardSearch ? link.getFromNode() : link.getToNode();
	}

	private Node getStartNode(Link link) {
		return forwardSearch ? link.getToNode() : link.getFromNode();
	}

	private double getFirstAndLastLinkTT(Link fromLink, Link toLink, double pathTravelTime, double time) {
		double lastLinkTT = forwardSearch ?
				VrpPaths.getLastLinkTT(toLink, time + pathTravelTime) :
				VrpPaths.getLastLinkTT(fromLink, time);
		return FIRST_LINK_TT + lastLinkTT;
	}

	private static class MultiNodeStopCriterion implements LeastCostPathTree.StopCriterion {
		private final BitSet nodesToVisit = new BitSet(Id.getNumberOfIds(Node.class));
		private int counter;

		public MultiNodeStopCriterion(Stream<Node> endNodes) {
			endNodes.forEach(node -> nodesToVisit.set(node.getId().index()));
			counter = nodesToVisit.cardinality();
		}

		@Override
		public boolean stop(int nodeIndex, double arrivalTime, double travelCost, double distance,
				double departureTime) {
			if (nodesToVisit.get(nodeIndex)) {
				counter--;
			}
			return counter == 0;
		}
	}
}
