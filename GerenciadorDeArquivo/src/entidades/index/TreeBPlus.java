package entidades.index;

import entidades.blocos.RowId;
import entidades.index.abstrations.HeaderIndexBlock;
import entidades.index.inner.CollumnValue;
import entidades.index.inner.InnerIndexBlock;
import entidades.index.leaf.LeafIndexBlock;
import exceptions.ContainerNoExistent;
import exceptions.innerBlock.IndexBlockNotFoundException;
import factories.ContainerId;

import java.io.IOException;

public class TreeBPlus {
    private ContainerId indexContainer;
    private IndexFileManager indexFileManager;
    private int ordem = 5;

    public TreeBPlus(ContainerId indexContainer, String indexName) throws IOException, ContainerNoExistent {
        this.indexContainer = indexContainer;
        indexFileManager = new IndexFileManager();

        indexFileManager.createIndex(indexContainer, indexName);
    }

    public String getPath() throws IOException, ContainerNoExistent {
        return IndexFileManager.getDiretorio(this.indexContainer.getValue());
    }

    public IndexContainer getContainerHeader() throws IOException, ContainerNoExistent {
        return IndexContainer.getJustContainer(this.indexContainer);
    }

    public boolean isEmpty() {
        try {
            return this.getContainerHeader().getBlocoControle().getHeader().getTamanhoDescritor() == 0;
        } catch (IOException | ContainerNoExistent e) {
            return false;
        }
    }


    public void insert(CollumnValue col, RowId rowId) throws IOException, ContainerNoExistent, IndexBlockNotFoundException {
        if (this.isEmpty()) {
            LeafIndexBlock block = new LeafIndexBlock(rowId);
            this.indexFileManager.createRoot(this.indexContainer, block);
        }
    }
}
