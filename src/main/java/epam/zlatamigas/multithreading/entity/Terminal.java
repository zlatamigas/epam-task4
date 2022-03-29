package epam.zlatamigas.multithreading.entity;

import epam.zlatamigas.multithreading.exception.LogisticBaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Terminal terminal = (Terminal) o;
        return id == terminal.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Terminal ");
        sb.append("#").append(id);
        return sb.toString();
    }
}