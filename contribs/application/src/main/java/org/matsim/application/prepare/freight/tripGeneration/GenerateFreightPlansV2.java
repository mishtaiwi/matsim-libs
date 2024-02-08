package org.matsim.application.prepare.freight.tripGeneration;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.*;
import org.matsim.application.MATSimAppCommand;
import org.matsim.application.options.LanduseOptions;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import picocli.CommandLine;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@CommandLine.Command(
        name = "generate-freight-plans",
        description = "Generate german wide freight population",
        showDefaultValues = true
)
public class GenerateFreightPlansV2 implements MATSimAppCommand {
    private static final Logger log = LogManager.getLogger(GenerateFreightPlansV2.class);

    @CommandLine.Option(names = "--data", description = "Path generated freight data from GenerateFreightDemandData.java",
            defaultValue = "output/longDistanceFreightData2/german_freightData.xml.gz")
    private String dataPath;

    @CommandLine.Option(names = "--network", description = "Path to desired network file",
            defaultValue = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/german-wide-freight/v2/germany-europe-network.xml.gz")
    private String networkPath;

    @CommandLine.Option(names = "--nuts", description = "Path to desired network file", required = true, defaultValue = "../public-svn/matsim/scenarios/countries/de/german-wide-freight/raw-data/shp/NUTS3/NUTS3_2010_DE.shp")
    // TODO Change this to URL pointing to SVN--> need to update the Location calculator
    private Path shpPath;

    @CommandLine.Option(names = "--output", description = "Output folder path", required = true, defaultValue = "output/german_freightPlans_new/")
    private Path output;

    @CommandLine.Option(names = "--truck-load", defaultValue = "13.0", description = "Average load of truck")
    private double averageTruckLoad;

    @CommandLine.Option(names = "--working-days", defaultValue = "260", description = "Number of working days in a year")
    private int workingDays;
	//TODO discuss if 260 is a good value

    @CommandLine.Option(names = "--sample", defaultValue = "0.1", description = "Scaling factor of the freight traffic (0, 1)")
    private double sample;

    @CommandLine.Mixin
    private LanduseOptions landuse = new LanduseOptions();

    @Override
    public Integer call() throws Exception {
        Network network = NetworkUtils.readNetwork(networkPath);
        log.info("Network successfully loaded!");

        log.info("preparing freight agent generator...");
        FreightAgentGenerator freightAgentGenerator = new FreightAgentGenerator(network, shpPath, landuse, averageTruckLoad, workingDays, sample);
        log.info("Freight agent generator successfully created!");

        log.info("Reading freight data...");
		Population inputFreightDemandData = PopulationUtils.readPopulation(dataPath);
        log.info("Freight data successfully loaded. There are " + inputFreightDemandData.getPersons().size() + " trip relations");

        log.info("Start generating population...");
        Population outputPopulation = PopulationUtils.createPopulation(ConfigUtils.createConfig());
		// TODO leerfahrten ergänzen (Ergänzen einer 3. Aktivität)
		// TODO überlegen ob man für die Leerfahrten bei längeren Touren eigenen Agenten erstellt

		int i = 0;
        for (Person freightDemandDataRelation : inputFreightDemandData.getPersons().values()) {
			if (i % 500000 == 0) {
				log.info("Processing: " + i + " out of " + inputFreightDemandData.getPersons().size() + " entries have been processed");
			}
			i++;
            List<Person> persons = freightAgentGenerator.generateRoadFreightAgents(freightDemandDataRelation, Integer.toString(i));
            for (Person person : persons) {
                outputPopulation.addPerson(person);
            }
        }

        if (!Files.exists(output)) {
            Files.createDirectory(output);
        }

		String sampleName = getSampleNameOfOutputFolder(sample);
        String outputPlansPath = output.toString() + "/german_freight." + sampleName+ "pct.plans.xml.gz";
        PopulationWriter populationWriter = new PopulationWriter(outputPopulation);
        populationWriter.write(outputPlansPath);

        // Write down tsv file for visualisation and analysis
        String freightTripTsvPath = output.toString() + "/freight_trips_" + sampleName + "pct_data.tsv";
        CSVPrinter tsvWriter = new CSVPrinter(new FileWriter(freightTripTsvPath), CSVFormat.TDF);
        tsvWriter.printRecord("trip_id", "fromCell", "toCell", "from_x", "from_y", "to_x", "to_y", "goodsType", "tripType");
        for (Person person : outputPopulation.getPersons().values()) {
            List<PlanElement> planElements = person.getSelectedPlan().getPlanElements();
            Activity act0 = (Activity) planElements.get(0);
            Activity act1 = (Activity) planElements.get(2);
            Coord fromCoord = network.getLinks().get(act0.getLinkId()).getCoord();
            Coord toCoord = network.getLinks().get(act1.getLinkId()).getCoord();
			String fromCell = freightAgentGenerator.getVerkehrszelleOfLink(act0.getLinkId());
			String toCell = freightAgentGenerator.getVerkehrszelleOfLink(act1.getLinkId());
			String tripType = LongDistanceFreightUtils.getTripType(person);

			int goodsType = switch (tripType) {
                case "pre-run" -> LongDistanceFreightUtils.getGoodsTypePreRun(person);
                case "main-run" -> LongDistanceFreightUtils.getGoodsTypeMainRun(person);
                case "post-run" -> LongDistanceFreightUtils.getGoodsTypePostRun(person);
                default -> throw new IllegalArgumentException("Unknown trip type: " + tripType);
            };
            tsvWriter.printRecord(person.getId().toString(), fromCell, toCell, fromCoord.getX(), fromCoord.getY(), toCoord.getX(), toCoord.getY(), goodsType, tripType);
        }
        tsvWriter.close();

        return 0;
    }

    public static void main(String[] args) {
        new GenerateFreightPlansV2().execute(args);
    }

	private static String getSampleNameOfOutputFolder(double sample) {
		String sampleName;
		if ((sample * 100) % 1 == 0)
			sampleName = String.valueOf((int) (sample * 100));
		else
			sampleName = String.valueOf((sample * 100));
		return sampleName;
	}
}
