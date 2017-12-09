package services;

import factories.ContainerId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableService {
    public ContainerId[] convertContainerIds(HashMap<ContainerId, String> containers) {
        Object[] x = containers.keySet().toArray();
        ContainerId[] containerIds = new ContainerId[x.length];
        for (int i = 0; i < x.length; i++) {
            containerIds[i] = (ContainerId) x[i];
        }
        return  containerIds;
    }

    public String[] convertContainerNames(HashMap<ContainerId, String> containers) {
        Object[] x = containers.values().toArray();
        String[] names = new String[x.length];

        for (int i = 0; i < x.length; i++) {
            names[i] =(String) x[i];
        }
        return  names;
    }

    public ContainerId getContainerIdBySelected(ContainerId[] containerIds, int tableSelecionada) {
        return containerIds[tableSelecionada];
    }
}
