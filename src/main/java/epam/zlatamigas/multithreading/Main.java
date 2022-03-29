package epam.zlatamigas.multithreading;

import epam.zlatamigas.multithreading.entity.LogisticBase;
import epam.zlatamigas.multithreading.entity.Truck;
import epam.zlatamigas.multithreading.exception.ReaderException;
import epam.zlatamigas.multithreading.parser.TruckParser;
import epam.zlatamigas.multithreading.parser.impl.TruckParserImpl;
import epam.zlatamigas.multithreading.reader.TruckReader;
import epam.zlatamigas.multithreading.reader.impl.TruckReaderImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    private static final Logger logger = LogManager.getLogger();

    private static final String FILEPATH = "trucks/truckdata.txt";
    private static final int THREAD_POOL = 10;

    public static void main(String[] args) throws InterruptedException {

        logger.info("Main start");

        TruckReader reader = new TruckReaderImpl();
        TruckParser parser = new TruckParserImpl();
        List<String> truckStrs = null;

        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(FILEPATH);
        if (resource != null) {
            try {
                truckStrs = reader.readAllTruckStrFromFile(resource.getFile());
            } catch (ReaderException e) {
                logger.error(e.getMessage());
                return;
            }
        }

        LogisticBase logisticBase = LogisticBase.getInstance();
        logger.info(logisticBase);

        List<Truck> trucks = parser.parseTruckStrs(truckStrs);
        List<Future<Boolean>> executionResults = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL);
        for (var tr : trucks) {
            logger.info("Truck is ready to run: " + tr);
            executionResults.add(executorService.submit(tr));
        }

        executorService.shutdown();
        int i = 0;
        try {
            for (; i < trucks.size(); i++) {

                logger.info("Finished " + (executionResults.get(i).get() ? "CORRECTLY" : "INCORRECTLY") + ": " +trucks.get(i));

            }
        } catch (ExecutionException e) {
            logger.error("Error while running truck " + i + ": " + e.getMessage());
        }

        logger.info(logisticBase);

        logger.info("Main finished");
    }
}
