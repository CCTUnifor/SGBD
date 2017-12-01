package services;

import java.util.ArrayList;
import java.util.List;

public class TableService {
    public List<String> mockTables() {
        List<String> tables = new ArrayList<String>();
        tables.add("Table");
        tables.add("Table2");
        tables.add("Table3");

        return tables;
    }
}
