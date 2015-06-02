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
		def findConnectedComponent(
			                          visiting: Set[String],
			                          rest: Set[String],
			                          component: Set[String] = Set.empty): (Set[String], Set[String], Set[String]) = {
			if (visiting.isEmpty) {
				(visiting, rest, component)
			} else {
				val v = visiting.head;
				val retRest = rest - v
				val retVisiting = visiting.tail
				if (component.contains(v)) {
					findConnectedComponent(retVisiting, retRest, component)
				} else {
					val retComponent = component + v
					graph.get(v) match {
						case None => findConnectedComponent(retVisiting, retRest, retComponent)
						case Some(vs) => findConnectedComponent(retVisiting ++ vs, retRest, retComponent)
					}
				}
			}
		}
		@tailrec
		def findConnectedComponents(vxs: Set[String],
		                            cts: Set[Set[String]] = Set.empty): Set[Set[String]] = {
			val (visiting, vertexes, component) = findConnectedComponent(Set(vxs.head), vxs.tail)
			val components = cts + component
			if (vertexes.isEmpty) {
				components
			} else {
				findConnectedComponents(vertexes, components)
			}
		}
		if (graph.isEmpty) {
			Set.empty
		} else {
			findConnectedComponents(graph.keySet)
		}
	}
}
