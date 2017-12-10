package interfaces;

import entidades.blocos.BlocoContainer;
import entidades.index.IndexContainer;
import entidades.index.inner.InnerIndexBlock;
import exceptions.ContainerNoExistent;
import factories.ContainerId;

import java.io.IOException;
import java.util.List;

public interface IIndexFileManager {
    IndexContainer createIndex(ContainerId containerId, String indexName) throws IOException, ContainerNoExistent;
    boolean existIndice(ContainerId containerId, String indexName);

    List<String> getIndicesPath(ContainerId containerIdSelecionado) throws IOException;

    void createBlock(int indexContainerId, InnerIndexBlock block) throws IOException, ContainerNoExistent;
}
