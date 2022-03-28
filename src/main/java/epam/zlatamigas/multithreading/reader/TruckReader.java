package epam.zlatamigas.multithreading.reader;

import epam.zlatamigas.multithreading.exception.ReaderException;

import java.util.List;

public interface TruckReader {

    String readFirstTruckStrFromFile(String filePath) throws ReaderException;
    List<String> readAllTruckStrFromFile(String filePath) throws ReaderException;
}
