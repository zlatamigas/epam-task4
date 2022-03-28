package epam.zlatamigas.multithreading.entity;

import epam.zlatamigas.multithreading.exception.LogisticBaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

public class Truck implements Callable<Boolean> {

    private static final Logger logger = LogManager.getLogger();

    private long id;
    private boolean isPrioritized;
    private Storage storage;
    private TruckTask truckTask;

    public Truck(){
        id = -1;
        isPrioritized = false;
    }

    public Truck(long id, boolean isPrioritized, Storage storage, TruckTask truckTask) {
        this.id = id;
        this.isPrioritized = isPrioritized;
        this.storage = storage;
        this.truckTask = truckTask;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isPrioritized() {
        return isPrioritized;
    }

    public void setPrioritized(boolean prioritized) {
        isPrioritized = prioritized;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public TruckTask getTruckTask() {
        return truckTask;
    }

    public void setTruckTask(TruckTask truckTask) {
        this.truckTask = truckTask;
    }

    @Override
    public Boolean call() throws Exception {

        LogisticBase logisticBase = LogisticBase.getInstance();

        logger.debug("Truck " + id + " arrives");
        Terminal terminal = null;
        try {
            terminal = logisticBase.obtainTerminal(isPrioritized);
            logger.debug("Truck " + id + " obtained terminal " + terminal.getId());
        } catch (LogisticBaseException e) {
            logger.error("Truck " + id + " cannot obtain terminal", e);
            return false;
        }

        try {
            switch (truckTask.getType()) {
                case LOAD -> {
                    terminal.loadTruck(this);
                    logger.debug("Loaded " + this);
                }
                case UNLOAD -> {
                    terminal.unloadTruck(this);
                    logger.debug("Unloaded " + this);
                }
                case UNLOAD_AND_LOAD -> {
                    terminal.unloadTruck(this);
                    logger.debug("Unloaded " + this);
                    terminal.loadTruck(this);
                    logger.debug("Loaded " + this);
                }
            }
        } catch (LogisticBaseException e) {
            logger.error("Truck " + id + " cannot finish task", e);
            return false;
        } finally {
            logisticBase.releaseTerminal(terminal);
            logger.debug("Truck " + id + " released terminal " + terminal.getId());
            logger.debug("Truck " + id + " departed");
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Truck truck = (Truck) o;
        return id == truck.id
                && isPrioritized == truck.isPrioritized
                && storage != null
                && storage.equals(truck.storage)
                && truckTask != null
                && truckTask.equals(truck.truckTask);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (int) id;
        result = 31 * result + (isPrioritized ? 1 : 0);
        result = 31 * result + (storage != null ? storage.hashCode() : 0);
        result = 31 * result + (truckTask != null ? truckTask.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Truck {");
        sb.append("ID=").append(id);
        sb.append(", PRIORITY=").append(isPrioritized);
        sb.append(", ").append(storage);
        sb.append(", ").append(truckTask);
        sb.append('}');
        return sb.toString();
    }

}
