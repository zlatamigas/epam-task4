package epam.zlatamigas.multithreading.parser;

import epam.zlatamigas.multithreading.entity.Truck;

import java.util.List;

public interface TruckParser {
    Truck parseTruckStr(String str);
    List<Truck> parseTruckStrs(List<String> strs);
}
