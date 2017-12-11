package interfaces;

import entidades.blocos.RowId;
import entidades.index.IndexContainer;
import entidades.index.abstrations.IndexBlock;
import entidades.index.inner.CollumnValue;
import entidades.index.inner.InnerIndexBlock;
import exceptions.ContainerNoExistent;
import exceptions.IncorrectTypeToPushPointerException;
import exceptions.innerBlock.IndexBlockNotFoundException;
import exceptions.innerBlock.IndexLeafBlockCannotPushPointerChildException;
import exceptions.innerBlock.InnerIndexBlockFullCollumnValueException;
import exceptions.innerBlock.InnerIndexBlockPointerToChildIsFullException;
import factories.ContainerId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface IIndexFileManager {
    IndexContainer createIndex(ContainerId containerId, String indexName, ArrayList<Integer> columns) throws IOException, ContainerNoExistent;
    void createRoot(ContainerId indexContainerId, InnerIndexBlock root, CollumnValue col, RowId rowId) throws IOException, ContainerNoExistent, IndexBlockNotFoundException, InnerIndexBlockPointerToChildIsFullException, IndexLeafBlockCannotPushPointerChildException, IncorrectTypeToPushPointerException, InnerIndexBlockFullCollumnValueException;
    boolean existIndice(ContainerId containerId, String indexName);

    List<String> getIndicesPath(ContainerId containerIdSelecionado) throws IOException;

    void createBlock(ContainerId indexContainerId, IndexBlock block) throws IOException, ContainerNoExistent;
}
