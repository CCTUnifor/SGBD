package interfaces;

import exceptions.ContainerNoExistentException;
import exceptions.IndexNoExistentException;
import factories.ContainerId;

import java.io.IOException;
import java.util.List;

public interface IIndexFileManager {
    void createIndex(ContainerId containerId, String indexName) throws IndexNoExistentException;
    boolean existIndice(ContainerId containerId, String indexName);

    List<String> getIndicesPath(ContainerId containerIdSelecionado) throws IOException, ContainerNoExistentException;
}
