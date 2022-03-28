package epam.zlatamigas.multithreading.entity;

import epam.zlatamigas.multithreading.exception.LogisticBaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class LogisticBase {

    private static final Logger logger = LogManager.getLogger();

    private static final Random random = new Random();
    private static final int TIMER_INTERVAL = 100;
    private static final int TIMER_DELAY = 20;
    private static final int STORAGE_OPERATION_TIME_INTERVAL = 1000;
    private static final int TERMINALS_NUMBER = 4;
    private static final int STORAGE_CAPACITY = 200;
    private static final double MIN_LOAD_FACTOR = 0.25;
    private static final double MAX_LOAD_FACTOR = 0.75;

    private static final Lock terminalLock = new ReentrantLock(true);
    private static final Condition terminalReleasedCondition = terminalLock.newCondition();
    private static final AtomicInteger prioritizedWaiting = new AtomicInteger(0);

    private static final Lock storageLock = new ReentrantLock(true);
    private static final Condition notFullStorageCondition = storageLock.newCondition();
    private static final Condition notEmptyStorageCondition = storageLock.newCondition();

    private static final Lock instanceLock = new ReentrantLock(true);
    private static final AtomicBoolean isInstanceCreated = new AtomicBoolean(false);
    private static LogisticBase instance;

    private final Storage storage;
    private final StorageManager storageManager;
    private final Deque<Terminal> availableTerminals;
    private final Deque<Terminal> occupiedTerminals;

    private LogisticBase() {

        storage = new Storage(STORAGE_CAPACITY, 0);
        storageManager = new StorageManager();
        availableTerminals = new ArrayDeque<>(TERMINALS_NUMBER);
        IntStream.range(0, TERMINALS_NUMBER)
                .mapToObj(i -> new Terminal(i + 1))
                .forEach(availableTerminals::add);
        occupiedTerminals = new ArrayDeque<>(TERMINALS_NUMBER);

        Timer timer = new Timer(true);
        timer.schedule(new StorageManager(), TIMER_DELAY, TIMER_INTERVAL);
    }

    public static LogisticBase getInstance() {

        if (!isInstanceCreated.get()) {
            try {
                instanceLock.lock();
                if (instance == null) {
                    instance = new LogisticBase();
                    isInstanceCreated.set(true);
                    logger.debug("Logistic base instance created");
                }
            } finally {
                instanceLock.unlock();
            }
        }
        logger.debug("Logistic base instance obtained");
        return instance;
    }

    public Terminal obtainTerminal(boolean isPrioritized) throws LogisticBaseException {
        Terminal terminal = null;
        if (isPrioritized) {
            prioritizedWaiting.incrementAndGet();
        }

        terminalLock.lock();
        try {
            if (isPrioritized) {
                logger.debug("Start waiting for terminal for prioritized truck");
                while (availableTerminals.isEmpty()) {
                    logger.debug("Wait terminal for prioritized truck");
                    terminalReleasedCondition.await();
                }
                logger.debug("Obtain terminal for prioritized truck");
            } else {
                logger.debug("Start waiting for terminal for non-prioritized truck");
                while (prioritizedWaiting.get() > 0 || availableTerminals.isEmpty()) {
                    logger.debug("Wait terminal for non-prioritized truck");
                    terminalReleasedCondition.await();
                }
                logger.debug("Obtain terminal for non-prioritized truck");
            }
            terminal = availableTerminals.pop();
            occupiedTerminals.push(terminal);
            logger.debug("Obtained terminal " + terminal.getId());
        } catch (InterruptedException e) {
            logger.error("Error obtaining terminal: " + e.getMessage());
            throw new LogisticBaseException("Error obtaining terminal", e);
        } finally {
            if (isPrioritized) {
                prioritizedWaiting.decrementAndGet();
            }
            terminalLock.unlock();
        }
        return terminal;
    }

    public void releaseTerminal(Terminal terminal) {
        terminalLock.lock();
        try {
            occupiedTerminals.remove(terminal);
            availableTerminals.add(terminal);
            logger.debug("Released terminal " + terminal.getId());
            terminalReleasedCondition.signalAll();
        } finally {
            terminalLock.unlock();
        }
    }

    public void addToStorage(int goodsCount) throws LogisticBaseException {

        logger.debug("Wait to unload " + goodsCount + " from truck");

        storageLock.lock();
        try {
            while (storage.getAvailableSize() < goodsCount
                    || storage.getOccupiedSize() + goodsCount > storage.getCapacity()) {
                logger.debug("Wait to unload truck");
                notFullStorageCondition.await();
            }
            storage.addGoods(goodsCount);
            TimeUnit.MILLISECONDS.sleep(random.nextInt(STORAGE_OPERATION_TIME_INTERVAL));
            notEmptyStorageCondition.signalAll();
        } catch (InterruptedException e) {
            logger.debug("Error unloading truck: " + e.getMessage());
            throw new LogisticBaseException("Error unloading truck", e);
        } finally {
            storageLock.unlock();
        }

        logger.debug("Finished unloading truck");
    }

    public void addToStorageByPortions(int goodsCount) throws LogisticBaseException {

        int storageCapacity = storage.getCapacity();
        int goodsPortion;

        while (goodsCount > 0) {

            goodsPortion = random.nextInt(Math.min(storageCapacity, goodsCount)) + 1;
            logger.debug("Wait to unload " + goodsPortion + "(of " + goodsCount + ") from truck");

            storageLock.lock();
            try {
                while (storage.getAvailableSize() < goodsPortion
                        || storage.getOccupiedSize() + goodsPortion > storageCapacity) {
                    logger.debug("Wait to unload truck");
                    notFullStorageCondition.await();
                }
                storage.addGoods(goodsPortion);
                goodsCount -= goodsPortion;
                TimeUnit.MILLISECONDS.sleep(random.nextInt(STORAGE_OPERATION_TIME_INTERVAL));
                logger.debug("Unloaded " + goodsPortion + " from truck");
                notEmptyStorageCondition.signalAll();
            } catch (InterruptedException e) {
                logger.debug("Error unloading truck: " + e.getMessage(), e.getCause());
                throw new LogisticBaseException("Error unloading truck", e);
            } finally {
                storageLock.unlock();
            }
        }

        logger.debug("Finished unloading truck");
    }

    public void removeFromStorage(int goodsCount) throws LogisticBaseException {

        logger.debug("Wait to load " + goodsCount + " to truck");

        storageLock.lock();
        try {
            while (storage.getOccupiedSize() < goodsCount) {
                logger.debug("Wait to load truck");
                notEmptyStorageCondition.await();
            }
            storage.removeGoods(goodsCount);
            TimeUnit.MILLISECONDS.sleep(random.nextInt(STORAGE_OPERATION_TIME_INTERVAL));
            notFullStorageCondition.signalAll();
        } catch (InterruptedException e) {
            logger.debug("Error loading truck: " + e.getMessage(), e.getCause());
            throw new LogisticBaseException("Error loading truck", e);
        } finally {
            storageLock.unlock();
        }

        logger.debug("Finished loading truck");
    }

    public void removeFromStorageByPortions(int goodsCount) throws LogisticBaseException {

        int storageCapacity = storage.getCapacity();
        int goodsPortion;

        while (goodsCount > 0) {

            goodsPortion = random.nextInt(Math.min(storageCapacity, goodsCount)) + 1;
            logger.debug("Wait to load " + goodsPortion + "(of " + goodsCount + ") to truck");

            storageLock.lock();
            try {
                while (storage.getOccupiedSize() < goodsPortion) {
                    logger.debug("Wait to load truck");
                    notEmptyStorageCondition.await();
                }
                storage.removeGoods(goodsPortion);
                goodsCount -= goodsPortion;
                TimeUnit.MILLISECONDS.sleep(random.nextInt(STORAGE_OPERATION_TIME_INTERVAL));
                logger.debug("Loaded " + goodsPortion + " to truck");
                notFullStorageCondition.signalAll();
            } catch (InterruptedException e) {
                logger.debug("Error loading truck: " + e.getMessage(), e.getCause());
                throw new LogisticBaseException("Error loading truck", e);
            } finally {
                storageLock.unlock();
            }
        }

        logger.debug("Finished loading truck");
    }

    public void manageStorage() {
        storageLock.lock();
        try {
            logger.debug("Logistic base storage filled " + storage.getOccupiedSize() + "/" + storage.getCapacity());
            if (storage.getOccupiedSize() < STORAGE_CAPACITY * MIN_LOAD_FACTOR) {
                storage.setOccupiedSize((int) (STORAGE_CAPACITY * MIN_LOAD_FACTOR));
                logger.debug("Logistic base managed storage MIN filled " + storage.getOccupiedSize() + "/" + storage.getCapacity());
                notEmptyStorageCondition.signalAll();
            } else if (storage.getOccupiedSize() > STORAGE_CAPACITY * MAX_LOAD_FACTOR) {
                storage.setOccupiedSize((int) (STORAGE_CAPACITY * MAX_LOAD_FACTOR));
                logger.debug("Logistic base managed storage MAX filled " + storage.getOccupiedSize() + "/" + storage.getCapacity());
                notFullStorageCondition.signalAll();
            }
        } finally {
            storageLock.unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LogisticBase that = (LogisticBase) o;
        return storage.equals(that.storage)
                && storageManager.equals(that.storageManager)
                && availableTerminals.equals(that.availableTerminals)
                && occupiedTerminals.equals(that.occupiedTerminals);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + storage.hashCode();
        result = 31 * result + storageManager.hashCode();
        result = 31 * result + availableTerminals.hashCode();
        result = 31 * result + occupiedTerminals.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LogisticBase{");
        sb.append("storage=").append(storage);
        sb.append(", storageManager=").append(storageManager);
        sb.append(", availableTerminals=").append(availableTerminals);
        sb.append(", occupiedTerminals=").append(occupiedTerminals);
        sb.append('}');
        return sb.toString();
    }
}