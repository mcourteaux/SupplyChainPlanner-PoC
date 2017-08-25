
import com.martijncourteaux.supplychainplanner.generic.shortestpaths.GraphBuilder;
import com.martijncourteaux.supplychainplanner.generic.shortestpaths.ShortestPathsSolver;
import com.martijncourteaux.supplychainplanner.generic.shortestpaths.TransportGraph;
import com.martijncourteaux.supplychainplanner.generic.shortestpaths.TransportPath;
import com.martijncourteaux.supplychainplanner.poc.PoCContext;
import com.martijncourteaux.supplychainplanner.poc.logic.PoCTransportFilter;
import com.martijncourteaux.supplychainplanner.poc.model.PoCLocation;
import com.martijncourteaux.supplychainplanner.poc.model.PoCShipment;
import com.martijncourteaux.supplychainplanner.poc.model.PoCTransport;
import java.sql.SQLException;
import java.util.List;
import junit.framework.TestCase;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author martijn
 */
public class PoCTransportFilterTest extends TestCase {

    private PoCContext context;

    public PoCTransportFilterTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = new PoCContext();
        context.prepareContext();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        context.destroyContext();
    }

    public void testFilterQuery() throws SQLException {
        PoCTransportFilter htft = new PoCTransportFilter();

        /* Source and destination of the shipment:
         * works by selecting a warehouse for source and destination, by id. */
        PoCShipment cm = new PoCShipment();
        cm.source = context.getLocation(3);
        cm.destination = context.getLocation(59);

        /* Specify the size of the consignment, such that the offers can be
         * filtered. Eg: some offers are only valid up to a certain amount of
         * weight. */
        cm.pallets = 5;
        cm.volume_m3 = 20;
        cm.weight_kg = 800;

        /* Cost weights */
        cm.basic_cost_weight = 1.0;      ///< \
        cm.cost_per_kg_weight = 1.0;     ///< |
        cm.cost_per_m3_weight = 1.0;     ///< | Actual costs in euros are weighted as is. 
        cm.cost_per_pallet_weight = 1.0; ///< /
        cm.duration_hours_weight = 30.0; ///< Cost function increases by 30 for every hour of transportation. (Ie: an hour "costs" 30 euros).

        /* Some parameters for extra filtering. */
        cm.allow_ferry = true;

        /* Some agents that the client had trouble with and doesn't want to
         * work with again. */
        cm.disallowed_agents.add(7);

        /* Fetch the possible transport offers given these shipment details. */
        List<PoCTransport> result = htft.fetch(context, cm);
        System.out.printf("Showing the first 3 results of the %d%n", result.size());
        for (int i = 0; i < Math.min(3, result.size()); ++i) {
            System.out.println(result.get(i));
        }

        System.out.println("Counting distinct locations");
        long countFrom = result.stream().map(t -> t.line_from).distinct().count();
        long countTill = result.stream().map(t -> t.line_to).distinct().count();
        System.out.printf("Locations from: %d%nLocations till: %d%n", countFrom, countTill);

        /* Now build a graph with the resulting options from the database. */
        GraphBuilder<PoCContext, PoCLocation, PoCTransport> gb = new GraphBuilder<>();
        PoCLocation from = cm.source;
        PoCLocation to = cm.destination;
        TransportGraph graph = gb.buildGraph(context, result, from, to);

        /* And start searching for shortest paths in it. */
        System.out.println("Searching in graph for shortest paths...");
        ShortestPathsSolver sps = new ShortestPathsSolver(graph);

        int total_found = 0;
        int count = 10;
        for (int i = 0; i < count; ++i) {
            int found = sps.searchPaths(1);
            if (found == 0) {
                break;
            }

            System.out.println("====== PATH " + i + " ======");

            System.out.println(sps.getTopKShortestPaths().get(i));
            System.out.println();
            /* Print out a google embed url to visualize using the
             * ./visualize.html utility page. */
            System.out.println(googleVisualizePath(graph, sps.getTopKShortestPaths().get(i)));
            System.out.println();

            total_found++;
        }

        System.out.println("Found " + total_found + "/" + count + " paths.");
    }

    public static String googleVisualizePath(TransportGraph<PoCLocation, PoCTransport> graph, TransportPath<PoCLocation, PoCTransport> path) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://www.google.com/maps/embed/v1/directions");
        sb.append("?key=AIzaSyD4iE2xVSpkLLOXoyqT-RuPwURN3ddScAI");
        sb.append("&origin=");
        sb.append(googleCoords(graph.getSourceLocation()));
        sb.append("&destination=");
        sb.append(googleCoords(graph.getSinkLocation()));
        if (path.numberOfTransports() > 1) {
            sb.append("&waypoints=");
            for (int i = 1; i < path.numberOfTransports(); ++i) {
                if (i > 1) {
                    sb.append("|");
                }
                PoCTransport t = path.getTransportLeg(i);
                sb.append(googleCoords(t.source));
            }
        }
        return sb.toString();
    }

    public static String googleCoords(PoCLocation l) {
        return String.format("%.4f,%.4f", l.lat, l.lon);
    }
}
