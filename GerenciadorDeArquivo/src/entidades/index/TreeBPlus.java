package entidades.index;

import entidades.blocos.BlocoControle;
import entidades.blocos.Descritor;
import entidades.blocos.RowId;
import entidades.blocos.TipoDado;
import entidades.index.abstrations.IndexBlock;
import entidades.index.inner.CollumnValue;
import entidades.index.inner.InnerHeaderIndexBlock;
import entidades.index.inner.InnerIndexBlock;
import exceptions.ContainerNoExistent;
import exceptions.IncorrectTypeToPushPointerException;
import exceptions.innerBlock.*;
import factories.ContainerId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeBPlus {
    private ContainerId indexContainer;
    private IndexFileManager indexFileManager;

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
            return IndexContainer.getIndexDescritorsByType(this.indexContainer, TipoDado.ROOT).size() == 0;
        } catch (IOException | ContainerNoExistent e) {
            return false;
        }
    }


    public void insert(CollumnValue col, RowId rowId) throws IOException, ContainerNoExistent, IndexBlockNotFoundException, InnerIndexBlockFullCollumnValueException, InnerIndexBlockPointerToChildIsFullException, IndexLeafBlockCannotPushPointerChildException, IncorrectTypeToPushPointerException {
        if (this.isEmpty()) {
            InnerIndexBlock block = new InnerIndexBlock(this.getOrder());
            this.indexFileManager.createRoot(this.indexContainer, block, col, rowId);
        } else {
            IndexContainer indexContainer = IndexContainer.getJustContainer(this.indexContainer);
            this.findInsertion(indexContainer.getRoot(), -1, col, rowId);
        }
    }

    public RowId find() {
        return null;
    }

    private void findInsertion(IndexBlock node, int positionNodeChildren, CollumnValue col, RowId rowId) throws IndexBlockNotFoundException, ContainerNoExistent, IOException, InnerIndexBlockFullCollumnValueException {
        // TODO leaf
        //        LeafIndexBlock leafIndexBlock = (LeafIndexBlock) node;
        InnerIndexBlock innerIndexBlock = (InnerIndexBlock) node;

        if (positionNodeChildren != -1) {
            if (innerIndexBlock.getChildren(positionNodeChildren) == null) {
                if (node.getIndexInsertionKeys() == node.getNumberMaxKeys())
                    indexFileManager.split(node, null, null, col, rowId);
                else
                    innerIndexBlock.pushColumnValue(col);
            } else
                findInsertion(innerIndexBlock.getChildren(positionNodeChildren), -1, col, rowId);
        } else {
            int i = 0;
            int position = -1;
            int count = InnerHeaderIndexBlock.HEADER_LENGTH + innerIndexBlock.getHeader().getBytesUsedByChildren() + 1;

            try {
                while (count < innerIndexBlock.getHeader().getBytesUsedByCollumnValue()) {
                    CollumnValue key = innerIndexBlock.loadCollumnValue(i);
                    if (col.compareTo(key) < 0) {
                        position = i;
                        break;
                    }

                    count += key.getFullLength();
                    i++;
                }
                if (position != -1)
                    findInsertion(node, position, col, rowId);
                else
                    findInsertion(node, i, col, rowId);

            } catch (InnerIndexBlockValueCollumnNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public int getOrder() throws IOException, ContainerNoExistent {
        BlocoControle controllerBlock = BlocoControle.getControllerBlock(this.indexContainer);

        int blockPageLength = controllerBlock.getHeader().getTamanhoDosBlocos();

        List<Integer> valores = controllerBlock.getDescritores().stream().map(Descritor::getTamanho).collect(Collectors.toList());
        List<Integer> collumns = IndexContainer.getIndexDescritorsByType(this.indexContainer, TipoDado.COLLUMN).stream()
                .map(x -> Integer.parseInt(x.getNome())).collect(Collectors.toList());

        int valueLength = 0;
        for (int c : collumns) {
            valueLength += valores.get(c);
        }

        int order = (int) Math.floor((blockPageLength + valueLength) / (valueLength + ((int) Math.ceil(Math.log10(blockPageLength) / Math.log10(2)))));
        return order;
    }
}
