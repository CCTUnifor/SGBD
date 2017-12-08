package interfaces;

import entidades.blocos.BlocoContainer;
import factories.ContainerId;

import java.io.IOException;

public interface IIndexFileManager {
    BlocoContainer createIndex(ContainerId containerId, String indexName) throws IOException;
    boolean existIndice(ContainerId containerId, String indexName);
}
