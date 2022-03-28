package test.zlatamigas.multithreading.parser.impl;

import epam.zlatamigas.multithreading.entity.Storage;
import epam.zlatamigas.multithreading.entity.Truck;
import epam.zlatamigas.multithreading.entity.TruckTask;
import epam.zlatamigas.multithreading.parser.TruckParser;
import epam.zlatamigas.multithreading.parser.impl.TruckParserImpl;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class TruckParserImplTest {

    private TruckParser parser;

    @DataProvider(name = "truckSinglePr")
    public Object[][] createDataSingleTruck() {
        return new Object[][]{
                {"1 false 20 10 UNLOAD 3", new Truck(1, false, new Storage(20, 10), new TruckTask(TruckTask.TaskType.UNLOAD, 3))},
                {"2 true 18 2 LOAD 4", new Truck(2, true, new Storage(18, 2), new TruckTask(TruckTask.TaskType.LOAD, 4))},
                {"3 true 30 29 UNLOAD_AND_LOAD 15 27", new Truck(3, true, new Storage(30, 29), new TruckTask(15, 27))},
                {"4 true 60 29 UNLOAD_AND_LOAD 25 7", new Truck(4, true, new Storage(60, 29), new TruckTask(25, 7))},
                {"5 false 20 10 UNLOAD 3", new Truck(5, false, new Storage(20, 10), new TruckTask(TruckTask.TaskType.UNLOAD, 3))},
                {"6 true 18 2 LOAD 4", new Truck(6, true, new Storage(18, 2), new TruckTask(TruckTask.TaskType.LOAD, 4))},
                {"7 true 8 2 LOAD 4", new Truck(7, true, new Storage(8, 2), new TruckTask(TruckTask.TaskType.LOAD, 4))},
                {"8 true 20 17 LOAD 3", new Truck(8, true, new Storage(20, 17), new TruckTask(TruckTask.TaskType.LOAD, 3))}
        };
    }

    @DataProvider(name = "truckAllPr")
    public Object[][] createDataAllTruck() {
        return new Object[][]{
                {Arrays.asList(
                        "1 false 20 10 UNLOAD 3",
                        "2 true 18 2 LOAD 4",
                        "3 true 30 29 UNLOAD_AND_LOAD 15 27",
                        "4 true 60 29 UNLOAD_AND_LOAD 25 7",
                        "5 false 20 10 UNLOAD 3",
                        "6 true 18 2 LOAD 4",
                        "7 true 8 2 LOAD 4",
                        "8 true 20 17 LOAD 3"
                ), Arrays.asList(
                        new Truck(1, false, new Storage(20, 10), new TruckTask(TruckTask.TaskType.UNLOAD, 3)),
                        new Truck(2, true, new Storage(18, 2), new TruckTask(TruckTask.TaskType.LOAD, 4)),
                        new Truck(3, true, new Storage(30, 29), new TruckTask(15, 27)),
                        new Truck(4, true, new Storage(60, 29), new TruckTask(25, 7)),
                        new Truck(5, false, new Storage(20, 10), new TruckTask(TruckTask.TaskType.UNLOAD, 3)),
                        new Truck(6, true, new Storage(18, 2), new TruckTask(TruckTask.TaskType.LOAD, 4)),
                        new Truck(7, true, new Storage(8, 2), new TruckTask(TruckTask.TaskType.LOAD, 4)),
                        new Truck(8, true, new Storage(20, 17), new TruckTask(TruckTask.TaskType.LOAD, 3))
                )},
        };
    }

    @BeforeClass
    public void setUp() {
        parser = new TruckParserImpl();
    }

    @Test(dataProvider = "truckSinglePr")
    public void testParseTruckStr(String truckStr, Truck expected) {
        Truck actual = parser.parseTruckStr(truckStr);
        assertEquals(actual, expected);
    }

    @Test(dataProvider = "truckAllPr")
    public void testParseTruckStrs(List<String> truckStrs, List<Truck> expected) {
        List<Truck> actual = parser.parseTruckStrs(truckStrs);
        assertEquals(actual, expected);
    }
}