package entidades.index.inner;

import entidades.GerenciadorDeIO;
import entidades.blocos.RowId;
import entidades.blocos.TipoBloco;
import entidades.index.IndexContainer;
import entidades.index.IndexFileManager;
import entidades.index.abstrations.HeaderIndexBlock;
import entidades.index.abstrations.IndexBlock;
import exceptions.ContainerNoExistent;
import exceptions.IncorrectTypeToPushPointerException;
import exceptions.innerBlock.*;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.io.IOException;
import java.util.ArrayList;

public class InnerIndexBlock extends IndexBlock implements IBinary {

    private InnerIndexBlock() {
        super();
        this.header = new InnerHeaderIndexBlock();
    }

    public InnerIndexBlock(int ordem) {
        super();
        this.header = new InnerHeaderIndexBlock(ordem);
    }

    public InnerIndexBlock(ContainerId containerId, BlocoId blockId, int ordem) {
        super();
        this.header = new InnerHeaderIndexBlock(containerId, blockId, ordem);
    }

    public InnerIndexBlock(byte[] blockBytes) {
        super();
        this.header = new InnerHeaderIndexBlock(blockBytes);
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater byteConcater = new ByteArrayConcater(GlobalVariables.TAMANHO_BLOCO);
        byteConcater
                .concat(super.toByteArray());

        return byteConcater.getFinalByteArray();
    }

    @Override
    public InnerIndexBlock fromByteArray(byte[] byteArray) {
        super.fromByteArray(byteArray);
        if (byteArray.length > InnerHeaderIndexBlock.HEADER_LENGTH)
            System.out.println("fromByteArray para o conteudo do InnerIndexBlock");

        return this;
    }

    public InnerHeaderIndexBlock getHeader() {
        return (InnerHeaderIndexBlock) this.header;
    }

    public static InnerIndexBlock getJustWithHeader(byte[] blockBytes) {
        return new InnerIndexBlock().fromByteArray(blockBytes);
    }

    public void pushColumnValue(String value) throws ContainerNoExistent, IOException, InnerIndexBlockFullCollumnValueException {
        if (this.header.getContainerId() == 0 || this.header.getContainerId() == -1)
            throw new ContainerNoExistent();

        this.pushColumnValue(new CollumnValue(value));
    }

    public void pushColumnValue(CollumnValue col) throws IOException, ContainerNoExistent, InnerIndexBlockFullCollumnValueException {
        String indexPath = IndexFileManager.getDiretorio(header.getContainerId());
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + this.getHeader().getBytesUsedByCollumnValue();

        if (!this.getHeader().existsSpaceForNewValueCollumn(col))
            throw new InnerIndexBlockFullCollumnValueException();

        GerenciadorDeIO.atualizarBytes(indexPath, offset, col.toByteArray());
        this.incrementCollumnCount(col);

        this.switchInOrder(col);
    }

    private void switchInOrder(CollumnValue col) throws IOException, ContainerNoExistent {
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + this.getHeader().getBytesUsedByChildren() + 1;
        String indexPath = IndexFileManager.getDiretorio(this.header.getContainerId());

        int offsetMax = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + this.getHeader().getBytesUsedByCollumnValue();

        CollumnValue colToSwitch = null;

        while (offset < offsetMax) {
            int length = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH));
            CollumnValue currentCol = new CollumnValue(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH + length));


            offset += CollumnValue.LENGTH + length;

            if (col.compareTo(currentCol) < 0) {
                colToSwitch = currentCol;
                break;
            }
        }

        if (colToSwitch != null) {
            GerenciadorDeIO.atualizarBytes(indexPath, offset, col.toByteArray());

            while (offset < offsetMax) {
                int length = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH));
                CollumnValue currentCol = new CollumnValue(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH + length));

                GerenciadorDeIO.atualizarBytes(indexPath, offset, colToSwitch.toByteArray());
                colToSwitch = currentCol;

                offset += CollumnValue.LENGTH + length;
            }

        }
    }

    private void incrementCollumnCount(CollumnValue col) throws IOException, ContainerNoExistent {
        int newIncrementedValue = this.getHeader().getBytesUsedByCollumnValue() + col.getFullLength();
        this.getHeader().setLastByteUsedByCollumnValue(newIncrementedValue);
        String indexPath = IndexFileManager.getDiretorio(this.getHeader().getContainerId());

        int offset = this.getHeader().getBlockPosition() + 5;
        GerenciadorDeIO.atualizarBytes(indexPath, offset, ByteArrayUtils.intTo3Bytes(newIncrementedValue));
    }


    public CollumnValue loadCollumnValue(int i) throws IOException, ContainerNoExistent, InnerIndexBlockValueCollumnNotFoundException {
        int offset = this.getCollumnValueOffset(i);
        if (offset < 0)
            throw new InnerIndexBlockValueCollumnNotFoundException();
        String path = IndexFileManager.getDiretorio(this.getHeader().getContainerId());
        int length = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(path, offset, CollumnValue.LENGTH));

        return new CollumnValue(GerenciadorDeIO.getBytes(path, offset, CollumnValue.LENGTH + length));
    }

    private int getCollumnValueOffset(int i) throws IOException, ContainerNoExistent {
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + this.getHeader().getBytesUsedByChildren() + 1;
        String indexPath = IndexFileManager.getDiretorio(this.header.getContainerId());

        int offsetMax = offset + this.getHeader().getBytesUsedByCollumnValue();
        int count = 0;

        while (offset < offsetMax && count <= i) {
            int length = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH));
            if (length != 0 && count == i)
                return offset;
            offset += CollumnValue.LENGTH + length;
            count++;
        }

        return -1;
    }

    public void pushPointerToChild(BlocoId refToChild) throws IOException, ContainerNoExistent, IndexLeafBlockCannotPushPointerChildException, InnerIndexBlockPointerToChildIsFullException, IndexBlockNotFoundException, IncorrectTypeToPushPointerException {
        if (this.getHeader().isLeaf())
            throw new IndexLeafBlockCannotPushPointerChildException();

        RowId newChild = RowId.create(this.getHeader().getContainerId(), refToChild.getValue());
        String indexPath = IndexFileManager.getDiretorio(this.header.getContainerId());
        int blockPosition = HeaderIndexBlock.getBlockPosition(newChild);
        int blockTipoOffset = blockPosition + 4;

        TipoBloco tipoBloco = ByteArrayUtils.byteArrayToEnum(ByteArrayUtils.subArray(GerenciadorDeIO.getBytes(indexPath), blockTipoOffset, 1), TipoBloco.values());

        IndexBlock block;
        if (tipoBloco == TipoBloco.INDEX_INNER)
            block = IndexContainer.loadInnerIndexBlock(newChild);
        else if (tipoBloco == TipoBloco.INDEX_LEAF)
            block = IndexContainer.loadLeafIndexBlock(newChild);
        else
            throw new IncorrectTypeToPushPointerException();

        int offset = lastPointerChildFree();
        if (offset < 0)
            throw new InnerIndexBlockPointerToChildIsFullException();

        GerenciadorDeIO.atualizarBytes(indexPath, offset, ByteArrayUtils.intToBytes(refToChild.getValue()));
    }

    private int lastPointerChildFree() throws IOException, ContainerNoExistent {
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH;
        String indexPath = IndexFileManager.getDiretorio(this.header.getContainerId());

        int offsetMax = offset + this.getHeader().getBytesUsedByChildren();

        while (offset < offsetMax) {
            int blockIdFromCurrentChild = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, InnerHeaderIndexBlock.POINTER_LENGTH));
            if (blockIdFromCurrentChild == 0)
                return offset;
            offset += InnerHeaderIndexBlock.POINTER_LENGTH;
        }

        return -1;
    }

    public IndexBlock getChildren(int i) {
        int offset = 0;
        try {
            offset = getPointerChildOffset(i);

            if (offset < 0)
                throw new IndexBlockNotFoundException();
            String path = IndexFileManager.getDiretorio(this.getHeader().getContainerId());
            int blockIdFinded = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(path, offset, InnerHeaderIndexBlock.POINTER_LENGTH));

            BlocoId block = BlocoId.create(blockIdFinded);
            return IndexContainer.loadInnerIndexBlock(RowId.create(this.getHeader().getContainerId(), block.getValue()));
        } catch (IOException | ContainerNoExistent | IndexBlockNotFoundException e) {
            return null;
        }
    }

    private int getPointerChildOffset(int i) throws IOException, ContainerNoExistent {
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH;
        String indexPath = IndexFileManager.getDiretorio(this.header.getContainerId());

        int offsetMax = offset + this.getHeader().getBytesUsedByChildren();
        int count = 0;

        while (offset < offsetMax && count <= i) {
            int blockIdFromCurrentChild = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, InnerHeaderIndexBlock.POINTER_LENGTH));
            if (blockIdFromCurrentChild != 0 && count == i)
                return offset;
            offset += InnerHeaderIndexBlock.POINTER_LENGTH;
            count++;
        }

        return -1;
    }

    public ArrayList<CollumnValue> getCollumns() {
        ArrayList<CollumnValue> cols = new ArrayList<CollumnValue>();

        int offset = 0;
        try {
            offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + this.getHeader().getBytesUsedByChildren() + 1;
            String indexPath = IndexFileManager.getDiretorio(this.header.getContainerId());
            int offsetMax = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + this.getHeader().getBytesUsedByCollumnValue();

            while (offset < offsetMax) {
                int length = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH));
                CollumnValue currentCol = new CollumnValue(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH + length));

                cols.add(currentCol);

                offset += CollumnValue.LENGTH + length;
            }
        } catch (IOException | ContainerNoExistent e) {
            e.printStackTrace();
        }

        return cols;
    }

    public String getCollumnValues() {
        StringBuilder x = new StringBuilder();
        for (CollumnValue col :
                this.getCollumns()) {
            x.append(col.getCollumnValue());
        }
        return x.toString();
    }

    public int getNumberMaxChildren() {
        return this.getHeader().getMaxChildren();
    }
}
