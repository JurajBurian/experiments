package unicornTrans;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jubu
 */
public class RouteValidator {
	public Result validateLines(Set<String> cities, List<Route> routes) {
		Graph graph = new Graph();
		for (String city : cities) {
			graph.addVertex(city);
		}
		// duplicities
		List<Route> duplicatedRoutes = buildGraph(graph, routes);
		// no links
		Set<String> missingLines = checkMissingLines(graph, cities);

		if (!missingLines.isEmpty()) {
			return new Result<Set<String>>(Code.missingLines, missingLines);
		}
		if (!duplicatedRoutes.isEmpty()) {
			return new Result<List<Route>>(Code.duplicatedLines, duplicatedRoutes);
		}
		int islandsCount = checkIslands(graph);
		if (islandsCount > 0) {
			return new Result<Integer>(Code.hasIslands, islandsCount);
		}
		return new Result<Void>(Code.ok, null);
	}

	private int checkIslands(Graph graph) {
		return new ConnectivityInspector(graph).connectedSets().size();

	}

	private Set<String> checkMissingLines(Graph graph, Set<String> cities) {
		HashSet<String> ret = new HashSet<String>();
		for (String city : cities) {
			if (graph.edgesOf(city).size() == 0) {
				ret.add(city);
			}
		}
		return ret;
	}

	private List<Route> buildGraph(Graph graph, List<Route> routes) {
		List<Route> duplicatedRoutes = new ArrayList<Route>();
		// build bidirectional graph
		for (Route route : routes) {
			if (graph.containsEdge(route.key, route.value) || graph.containsEdge(route.value, route.key)) {
				duplicatedRoutes.add(route);
			} else {
				graph.addEdge(route.key, route.value);
			}
		}
		return duplicatedRoutes;
	}

	public enum Code {
		ok,
		missingLines,
		duplicatedLines,
		hasIslands
	}

	public static class Result<T> {
		public final Code code;
		public final T info;

		public Result(Code code, T info) {
			this.code = code;
			this.info = info;
		}
	}

	public static class Route {
		public final String key;
		public final String value;

		public Route(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Route)) return false;

			Route route = (Route) o;

			if (!key.equals(route.key)) return false;
			return value.equals(route.value);

		}

		@Override
		public int hashCode() {
			int result = key.hashCode();
			result = 31 * result + value.hashCode();
			return result;
		}
	}

	static class Graph extends DefaultDirectedGraph<String, DefaultEdge> {
		public Graph() {
			super(DefaultEdge.class);
		}
	}
}
