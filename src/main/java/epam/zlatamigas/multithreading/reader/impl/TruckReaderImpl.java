package epam.zlatamigas.multithreading.reader.impl;

import epam.zlatamigas.multithreading.exception.ReaderException;
import epam.zlatamigas.multithreading.reader.TruckReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class TruckReaderImpl implements TruckReader {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public String readFirstTruckStrFromFile(String filePath) throws ReaderException {

        File file = new File(filePath);
        if (file.exists() && file.length() == 0) {
            return null;
        }

        logger.debug("Reader got access to file: " + filePath);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

            String str;
            while ((str = bufferedReader.readLine()) != null) {
                str = str.trim();
                if (!str.isEmpty()) {
                    return str;
                }
            }

            logger.warn("No valid track found in " + filePath);
            return null;

        } catch (IOException e) {
            throw new ReaderException("Error occurred while reading", e);
        }
    }

    @Override
    public List<String> readAllTruckStrFromFile(String filePath) throws ReaderException {

        File file = new File(filePath);
        if (file.exists() && file.length() == 0) {
            return null;
        }

        logger.debug("Reader got access to file: " + filePath);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return bufferedReader.lines().map(String::trim).filter(s -> !s.isEmpty()).toList();
        } catch (IOException e) {
            throw new ReaderException("Error occurred while reading", e);
        }
    }
}
