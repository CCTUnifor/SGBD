package services;

import java.util.ArrayList;
import java.util.List;

public class CollumnService {
    public List<String> mockColunas() {
        List<String> colunas = new ArrayList<String>();

        for (int i = 1; i < 50; i++) {
            colunas.add("Test" + i);
        }

        return colunas;
    }
}
