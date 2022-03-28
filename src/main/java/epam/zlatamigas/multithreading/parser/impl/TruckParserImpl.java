package epam.zlatamigas.multithreading.parser.impl;

import epam.zlatamigas.multithreading.entity.Storage;
import epam.zlatamigas.multithreading.entity.Truck;
import epam.zlatamigas.multithreading.entity.TruckTask;
import epam.zlatamigas.multithreading.parser.TruckParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TruckParserImpl implements TruckParser {

    private static final Logger logger = LogManager.getLogger();

    private static final String DELIMITER = " ";

    @Override
    public Truck parseTruckStr(String str) {

        String[] truckData = str.split(DELIMITER);
        if (truckData.length < 6) {
            return null;
        }

        int id;
        boolean priority;
        int capacity;
        int occupied;
        TruckTask task;

        try {
            id = Integer.parseInt(truckData[0]);
            switch (truckData[1].toLowerCase()) {
                case "true" -> {
                    priority = true;
                }
                case "false" -> {
                    priority = false;
                }
                default -> {
                    logger.error("Invalid boolean priority type: " + truckData[1]);
                    return null;
                }
            }
            capacity = Integer.parseInt(truckData[2]);
            occupied = Integer.parseInt(truckData[3]);
            TruckTask.TaskType taskType = TruckTask.TaskType.valueOf(truckData[4]);
            task = switch (taskType) {
                case LOAD, UNLOAD -> new TruckTask(taskType, Integer.parseInt(truckData[5]));
                case UNLOAD_AND_LOAD -> new TruckTask(Integer.parseInt(truckData[5]), Integer.parseInt(truckData[6]));
            };
        } catch (NumberFormatException e) {
            logger.error("Invalid number format: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid TruckTask.TaskType: " + truckData[4]);
            return null;
        }


        Truck truck = new Truck();

        truck.setId(id);
        truck.setPrioritized(priority);
        truck.setStorage(new Storage(capacity, occupied));
        truck.setTruckTask(task);

        return truck;
    }

    @Override
    public List<Truck> parseTruckStrs(List<String> strs) {
        return strs.stream().map(this::parseTruckStr).filter(s -> s != null).toList();
    }
}
