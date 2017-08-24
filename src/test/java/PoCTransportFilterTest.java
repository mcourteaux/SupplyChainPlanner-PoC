
import com.martijncourteaux.supplychainplanner.generic.shortestpaths.GraphBuilder;
import com.martijncourteaux.supplychainplanner.generic.shortestpaths.ShortestPathsSolver;
import com.martijncourteaux.supplychainplanner.generic.shortestpaths.TransportGraph;
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

        PoCShipment cm = new PoCShipment();
        cm.source = context.getLocation(3);
        cm.destination = context.getLocation(51);

        /* Specify the size of the consignment, such that the offers can be
             * filtered. */
        cm.pallets = 5;
        cm.volume_m3 = 20;
        cm.weight_kg = 800;

        /* Cost weights */
        cm.basic_cost_weight = 1.0;
        cm.cost_per_kg_weight = 1.0;
        cm.cost_per_m3_weight = 1.0;
        cm.cost_per_pallet_weight = 1.0;
        cm.duration_hours_weight = 300.0;

        /* Some parameters for extra filtering. */
        cm.allow_ferry = true;

        /* Some agents that the client had trouble with and doesn't want to
             * work with again. */
        cm.disallowed_agents.add(7);

        List<PoCTransport> result = htft.fetch(context, cm);
        System.out.printf("Showing the first 3 results of the %d%n", result.size());
        for (int i = 0; i < 3; ++i) {
            System.out.println(result.get(i));
        }

        System.out.println("Counting distinct regions");
        long countFrom = result.stream().map(t -> t.line_from).distinct().count();
        long countTill = result.stream().map(t -> t.line_to).distinct().count();
        System.out.printf("Locations from: %d%nLocations till: %d%n", countFrom, countTill);

        GraphBuilder<PoCContext, PoCLocation, PoCTransport> gb = new GraphBuilder<>();
        PoCLocation from = cm.source;
        PoCLocation to = cm.destination;
        TransportGraph graph = gb.buildGraph(context, result, from, to);

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

            total_found++;
        }

        System.out.println("Found " + total_found + "/" + count + " paths.");
    }

}
