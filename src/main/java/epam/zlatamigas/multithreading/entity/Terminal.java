package epam.zlatamigas.multithreading.entity;

import epam.zlatamigas.multithreading.exception.LogisticBaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Terminal {

    private static final Logger logger = LogManager.getLogger();

    private int id;

    public Terminal(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void loadTruck(Truck truck) throws LogisticBaseException {
        int cargo = truck.getTruckTask().getLoadCount();
        try {
            LogisticBase.getInstance().removeFromStorage(cargo);
        } catch (LogisticBaseException e) {
            throw new LogisticBaseException("Cannot load truck " + truck.getId() + " at terminal " + id, e);
        }
        truck.getStorage().addGoods(cargo);
    }

    public void unloadTruck(Truck truck) throws LogisticBaseException {
        int cargo = truck.getTruckTask().getUnloadCount();
        try {
            LogisticBase.getInstance().addToStorage(cargo);
        } catch (LogisticBaseException e) {
            throw new LogisticBaseException("Cannot unload truck " + truck.getId() + " at terminal " + id, e);
        }
        truck.getStorage().removeGoods(cargo);
    }
}