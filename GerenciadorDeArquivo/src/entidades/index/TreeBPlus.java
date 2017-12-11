package entidades.index;

import entidades.blocos.RowId;
import entidades.blocos.TipoDado;
import entidades.index.inner.CollumnValue;
import entidades.index.inner.InnerIndexBlock;
import entidades.index.leaf.LeafIndexBlock;
import exceptions.ContainerNoExistent;
import exceptions.IncorrectTypeToPushPointerException;
import exceptions.innerBlock.IndexBlockNotFoundException;
import exceptions.innerBlock.IndexLeafBlockCannotPushPointerChildException;
import exceptions.innerBlock.InnerIndexBlockFullCollumnValueException;
import exceptions.innerBlock.InnerIndexBlockPointerToChildIsFullException;
import factories.ContainerId;

import java.io.IOException;
import java.util.ArrayList;

public class TreeBPlus {
    private ContainerId indexContainer;
    private IndexFileManager indexFileManager;
    private int ordem = 5;

    public TreeBPlus(ContainerId indexContainer, String indexName, ArrayList<Integer> columns) throws IOException, ContainerNoExistent {
        this.indexContainer = indexContainer;
        indexFileManager = new IndexFileManager();

        indexFileManager.createIndex(indexContainer, indexName, columns);
    }

    public String getPath() throws IOException, ContainerNoExistent {
        return IndexFileManager.getDiretorio(this.indexContainer.getValue());
    }

    public IndexContainer getContainerHeader() throws IOException, ContainerNoExistent {
        return IndexContainer.getJustContainer(this.indexContainer);
    }

    public boolean isEmpty() {
        try {

            return IndexContainer.getIndexDescritorsByType(this.indexContainer, TipoDado.ROOT).get(0) != null;
        } catch (IOException | ContainerNoExistent e) {
            return false;
        }
    }


    public void insert(CollumnValue col, RowId rowId) throws IOException, ContainerNoExistent, IndexBlockNotFoundException, InnerIndexBlockFullCollumnValueException, InnerIndexBlockPointerToChildIsFullException, IndexLeafBlockCannotPushPointerChildException, IncorrectTypeToPushPointerException {
        if (this.isEmpty()) {
            InnerIndexBlock block = new InnerIndexBlock(this.ordem);
            this.indexFileManager.createRoot(this.indexContainer, block, col, rowId);
        } else {
            IndexContainer indexContainer = IndexContainer.getJustContainer(this.indexContainer);
            this.findInsertion(indexContainer.getRoot(), -1, col, rowId);
        }
    }

    public RowId find() {
        return null;
    }

    private void findInsertion(LeafIndexBlock root, int positionNodeChildren, CollumnValue col, RowId rowId) {

    }

}
