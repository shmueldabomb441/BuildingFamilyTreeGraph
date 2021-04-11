/*
 * (C) Copyright 2017-2020, by Dimitrios Michail and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.alg.scoring;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;

import java.util.*;

/**
 * Harmonic centrality.
 * 
 * <p>
 * The harmonic centrality of a vertex $x$ is defined as $H(x)=\sum_{y \neq x} 1/d(x,y)$, where
 * $d(x,y)$ is the shortest path distance from $x$ to $y$. In case a distance $d(x,y)=\infinity$,
 * then $1/d(x,y)=0$. When normalization is used the score is divided by $n-1$ where $n$ is the
 * total number of vertices in the graph.
 *
 * For details see the following papers:
 * <ul>
 * <li>Yannick Rochat. Closeness centrality extended to unconnected graphs: The harmonic centrality
 * index. Applications of Social Network Analysis, 2009.</li>
 * <li>Newman, Mark. 2003. The Structure and Function of Complex Networks. SIAM Review, 45(mars),
 * 167–256.</li>
 * </ul>
 * and the <a href="https://en.wikipedia.org/wiki/Closeness_centrality">wikipedia</a> article.
 *
 * <p>
 * This implementation computes by default the centrality using outgoing paths and normalizes the
 * scores. This behavior can be adjusted by the constructor arguments.
 * 
 * <p>
 * Shortest paths are computed either by using Dijkstra's algorithm or Floyd-Warshall depending on
 * whether the graph has edges with negative edge weights. Thus, the running time is either $O(n (m
 * + n \log n))$ or $O(n^3)$ respectively, where $n$ is the number of vertices and $m$ the number of
 * edges of the graph.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * 
 * @author Dimitrios Michail
 */
public final class HarmonicCentrality<V, E>
    extends
    ClosenessCentrality<V, E>
{
    /**
     * Construct a new instance. By default the centrality is normalized and computed using outgoing
     * paths.
     * 
     * @param graph the input graph
     */
    public HarmonicCentrality(Graph<V, E> graph)
    {
        this(graph, false, true);
    }

    /**
     * Construct a new instance.
     * 
     * @param graph the input graph
     * @param incoming if true incoming paths are used, otherwise outgoing paths
     * @param normalize whether to normalize by dividing the closeness by $n-1$, where $n$ is the
     *        number of vertices of the graph
     */
    public HarmonicCentrality(Graph<V, E> graph, boolean incoming, boolean normalize)
    {
        super(graph, incoming, normalize);
    }

    @Override
    protected void compute()
    {
        // create result container
        this.scores = new HashMap<>();

        // initialize shortest path algorithm
        ShortestPathAlgorithm<V, E> alg = getShortestPathAlgorithm();

        // compute shortest paths
        int n = graph.vertexSet().size();
        for (V v : graph.vertexSet()) {
            double sum = 0d;

            SingleSourcePaths<V, E> paths = alg.getPaths(v);
            for (V u : graph.vertexSet()) {
                if (!u.equals(v)) {
                    sum += 1.0 / paths.getWeight(u);
                }
            }

            if (normalize && n > 1) {
                this.scores.put(v, sum / (n - 1));
            } else {
                this.scores.put(v, sum);
            }
        }
    }

}
