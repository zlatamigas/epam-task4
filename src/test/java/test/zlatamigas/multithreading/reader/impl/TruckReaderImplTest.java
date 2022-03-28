package test.zlatamigas.multithreading.reader.impl;

import epam.zlatamigas.multithreading.Main;
import epam.zlatamigas.multithreading.exception.ReaderException;
import epam.zlatamigas.multithreading.reader.TruckReader;
import epam.zlatamigas.multithreading.reader.impl.TruckReaderImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class TruckReaderImplTest {

    private static final String TEST_PATH = "testtrucks/";
    private TruckReader reader;

    @DataProvider(name = "truckFirstPr")
    public Object[][] createDataFirstTruck() {
        return new Object[][]{
                {"truckdata_1.txt", "1 false 20 10 UNLOAD 3"},
                {"truckdata_2.txt", "1 false 20 10 UNLOAD 3"},
                {"truckdata_3.txt", null}
        };
    }

    @DataProvider(name = "truckAllPr")
    public Object[][] createDataAllTruck() {
        return new Object[][]{
                {"truckdata_1.txt", Arrays.asList(
                        "1 false 20 10 UNLOAD 3",
                        "2 true 18 2 LOAD 4",
                        "3 true 30 29 UNLOAD_AND_LOAD 15 27",
                        "4 true 60 29 UNLOAD_AND_LOAD 25 7",
                        "5 false 20 10 UNLOAD 3",
                        "6 true 18 2 LOAD 4",
                        "7 true 8 2 LOAD 4",
                        "8 true 20 17 LOAD 3"
                )},
                {"truckdata_2.txt", Arrays.asList(
                        "1 false 20 10 UNLOAD 3",
                        "2 true 18 2 LOAD 4",
                        "3 true 30 29 UNLOAD_AND_LOAD 15 27",
                        "4 true 60 29 UNLOAD_AND_LOAD 25 7",
                        "5 false 20 10 UNLOAD 3",
                        "6 true 18 2 LOAD 4",
                        "7 true 8 2 LOAD 4",
                        "8 true 20 17 LOAD 3"
                )},
                {"truckdata_3.txt", null}
        };
    }

    @BeforeClass
    public void setUp() {
        reader = new TruckReaderImpl();
    }

    @Test(dataProvider = "truckFirstPr")
    public void testReadFirstTruckStrFromFile(String filename, String expected) throws ReaderException {

        String filepath = getFullFilePath(TEST_PATH + filename);
        if (filepath == null) {
            fail("File does not exists: " + TEST_PATH + filename);
        }

        String actual = reader.readFirstTruckStrFromFile(filepath);
        assertEquals(actual, expected);
    }

    @Test(dataProvider = "truckAllPr")
    public void testReadAllTruckStrFromFile(String filename, List<String> expected) throws ReaderException {
        String filepath = getFullFilePath(TEST_PATH + filename);
        if (filepath == null) {
            fail("File does not exists: " + TEST_PATH + filename);
        }

        List<String> actual = reader.readAllTruckStrFromFile(filepath);
        assertEquals(actual, expected);
    }

    private String getFullFilePath(String filename) {

        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(filename);
        if (resource != null) {
            return resource.getFile();
        }

        return null;
    }
}