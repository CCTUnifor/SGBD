package interfaces;

import entidades.blocos.BlocoContainer;
import entidades.index.IndexContainer;
import entidades.index.abstrations.IndexBlock;
import entidades.index.inner.InnerIndexBlock;
import entidades.index.leaf.LeafIndexBlock;
import exceptions.ContainerNoExistent;
import exceptions.innerBlock.IndexBlockNotFoundException;
import factories.ContainerId;

import java.io.IOException;
import java.util.List;

public interface IIndexFileManager {
    IndexContainer createIndex(ContainerId containerId, String indexName) throws IOException, ContainerNoExistent;
    void createRoot(ContainerId indexContainerId, LeafIndexBlock block) throws IOException, ContainerNoExistent, IndexBlockNotFoundException;
    boolean existIndice(ContainerId containerId, String indexName);

    List<String> getIndicesPath(ContainerId containerIdSelecionado) throws IOException;

    void createBlock(ContainerId indexContainerId, IndexBlock block) throws IOException, ContainerNoExistent;
}
