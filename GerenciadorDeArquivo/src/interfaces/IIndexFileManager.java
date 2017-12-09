package interfaces;

import entidades.blocos.BlocoContainer;
import exceptions.IndexNoExistentException;
import factories.ContainerId;

import java.io.IOException;
import java.util.List;

public interface IIndexFileManager {
    BlocoContainer createIndex(ContainerId containerId, String indexName) throws IOException, IndexNoExistentException;
    boolean existIndice(ContainerId containerId, String indexName);

    List<String> getIndicesPath(ContainerId containerIdSelecionado) throws IOException;
}
