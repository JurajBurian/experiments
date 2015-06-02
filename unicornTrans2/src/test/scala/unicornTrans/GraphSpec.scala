package unicornTrans

import org.scalatest.{FreeSpec, MustMatchers}

/**
 * @author jubu
 */
class GraphSpec extends FreeSpec with MustMatchers {

	import graph._

	"graph" - {
		"should find abandoned vertexes" in {
			val edges = Seq(("1", "2"), ("2", "3"))
			val vertexes = Seq("2", "4")

			val (graph, info) = graphFromEdges(edges)(graphFromVertexes(vertexes)())

			findAbandonedVertexes(graph) must be(Set("4"))
		}

		"should find duplicated vertexes" in {
			val edges = Seq(("1", "2"), ("2", "3"), ("3", "2"))
			val (graph, info) = graphFromEdges(edges)()

			info must be(List(("3", "2")))
		}

		"should find connected components" in {

			val edges = Seq(("1", "2"), ("2", "3"), ("5", "6"))
			val vertexes = Seq("4")

			val (graph, info) = graphFromEdges(edges)(graphFromVertexes(vertexes)())
			val cc = findConnectedComponents(graph)

			cc must be(Set(Set("1", "2", "3"), Set("5", "6"), Set("4")))
		}

		"should find empty components in empty graph" in {
			val (graph, info) = graphFromEdges(List.empty)()
			val cc = findConnectedComponents(graph)
			cc must be(Set[Set[String]]())
		}
	}
}

