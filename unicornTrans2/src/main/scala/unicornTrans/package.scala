import scala.annotation.tailrec

/**
 * @author jubu
 */
package object graph {

	/**
	 * bidirectional graph
	 */
	type Graph = Map[String, Set[String]];

	/**
	 * build bidirectional graph.
	 * Implementation doesn't allow insert abandoned vertex
	 * @param edges
	 * @param graph
	 * @return tuple graph instance and list of duplicated edges if any
	 */
	def graphFromEdges(edges: Seq[(String, String)])(graph: Graph = Map.empty) = {
		@tailrec
		def build(p: Seq[(String, String)], graph: Graph, duplicities: List[(String, String)]): (Graph, List[(String, String)]) = {
			val pp: Seq[(String, String)] = p
			p match {
				case (v, k) :: xs => {
					val setV = graph.getOrElse(v, Set[String]())
					val setK = graph.getOrElse(k, Set[String]())
					if (setV.contains(k) || setK.contains(k)) {
						build(xs, graph, (v, k) :: duplicities)
					} else {
						val g2 = (graph + (v -> (setV + k))) + (k -> (setK + v))
						build(xs, g2, duplicities)
					}
				}
				case Nil => (graph, duplicities)
			}
		}
		build(edges, graph, List.empty)
	}

	@tailrec
	def graphFromVertexes(vertexes: Seq[String])(graph: Graph = Map.empty): Graph = vertexes match {
		case v :: xs => if (graph.contains(v)) {
			graphFromVertexes(xs)(graph)
		} else {
			graphFromVertexes(xs)(graph + (v -> Set.empty))
		}
		case Nil => graph
	}

	def findAbandonedVertexes(graph: Graph) = {
		(for ((v, s) <- graph if (s.isEmpty)) yield (v)).toSet
	}

	def findConnectedComponents(graph: Graph) = {
		@tailrec
		def findConnectedComponent(from: String, end: String, graph: Graph, rest: Set[String] = Set.empty, component: Set[String] = Set.empty): Set[String] = {
			if (component.contains(end)) {
				component
			} else {
				val retComponent = component + end
				val vs = graph.get(end)
				graph.get(end) match {
					case None => retComponent
					case Some(vs) => {
						val rds = (vs - from)
						if (rds.isEmpty) {
							retComponent
						} else {
							findConnectedComponent(end, rds.head, graph, rds.tail, retComponent)
						}
					}
				}
			}
		}
		@tailrec
		def findConnectedComponents(
			                           vertex: String, graph: Graph,
			                           vertexes: Set[String],
			                           components: Set[Set[String]] = Set.empty): Set[Set[String]] = {
			val ct = findConnectedComponent(vertex, vertex, graph)
			val cts = components + ct
			val diff = (vertexes &~ ct)
			if (diff.isEmpty) {
				cts
			} else {
				findConnectedComponents(diff.head, graph, diff, cts)
			}
		}
		if (graph.isEmpty) {
			Set.empty
		} else {
			findConnectedComponents(graph.head._1, graph, graph.keySet)
		}
	}
}
