package epam.zlatamigas.multithreading.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TimerTask;

public class StorageManager extends TimerTask {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void run() {
        LogisticBase logisticBase = LogisticBase.getInstance();

        logger.debug("Started storage management");
        logisticBase.manageStorage();
        logger.debug("Finished storage management");
    }
}
