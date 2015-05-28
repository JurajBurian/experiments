package unicornTrans;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static unicornTrans.RouteValidator.Route;

/**
 * @author jubu
 */
public class RouteValidatorTest extends TestCase {

	private final static HashSet<String> cities1 = new HashSet<String>();
	private final static HashSet<String> cities2 = new HashSet<String>();
	private final static HashSet<String> cities = new HashSet<String>();


	static {
		for (int i = 0; i < 6; i++) {
			cities1.add(Integer.toString(i));
		}
		for (int i = 6; i < 10; i++) {
			cities2.add(Integer.toString(i));
		}
		cities.addAll(cities1);
		cities.addAll(cities2);
	}

	public void testValidateLinesMissingLines() throws Exception {

		List<Route> routes = Arrays.asList(new Route[]{
				new Route("0", "1"),
				new Route("1", "2"),
				new Route("1", "3"),
				new Route("2", "3"),
				new Route("3", "4"),
				new Route("4", "5"),
		});

		RouteValidator rv = new RouteValidator();
		RouteValidator.Result<Set<String>> result = rv.validateLines(cities , routes);
		assertEquals("Missing lines not detected", result.code, RouteValidator.Code.missingLines);
		assertEquals("Wrong info for missing lines", result.info, cities2);
	}

	public void testValidateLinesDuplicatedLines() throws Exception {

		List<Route> duplicatedRoutes = Arrays.asList(new Route[]{new Route("1", "0")});
		List<Route> routes = Arrays.asList(new Route[]{
				new Route("0", "1"),
				new Route("1", "2"),
				new Route("2", "3"),
				new Route("3", "4"),
				new Route("4", "5"),
				new Route("6", "7"),
				new Route("7", "8"),
				new Route("8", "9"),
				duplicatedRoutes.get(0),
		});

		RouteValidator rv = new RouteValidator();
		RouteValidator.Result result = rv.validateLines(cities , routes);
		assertEquals("Duplicated lines not detected", result.code, RouteValidator.Code.duplicatedLines);
		assertEquals("Wrong info for duplicated lines", result.info, duplicatedRoutes);
	}

	public void testValidateLinesHasIslands() throws Exception {

		List<Route> routes = Arrays.asList(new Route[]{
				// first component
				new Route("0", "1"),
				new Route("1", "2"),
				// seconnd componet
				new Route("3", "4"),
				new Route("3", "5")

		});

		new HashSet<String>(cities1);
		RouteValidator rv = new RouteValidator();
		RouteValidator.Result result = rv.validateLines(cities1 , routes);
		assertEquals("Islands not detected", result.code, RouteValidator.Code.hasIslands);
		assertEquals("Wrong info for islands", result.info, 2);
	}
}