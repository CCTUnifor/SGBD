package entidades.index.inner;

import entidades.GerenciadorDeIO;
import entidades.blocos.RowId;
import entidades.index.IndexContainer;
import entidades.index.IndexFileManager;
import entidades.index.abstrations.IndexBlock;
import exceptions.ContainerNoExistent;
import exceptions.innerBlock.*;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.io.IOException;

public class InnerIndexBlock extends IndexBlock implements IBinary {
    private InnerIndexBlock() {
        super();
        this.header = new InnerHeaderIndexBlock();
    }

    public InnerIndexBlock(int numberOfChildrens) {
        super();
        this.header = new InnerHeaderIndexBlock(numberOfChildrens);
    }

    public InnerIndexBlock(ContainerId containerId, BlocoId blockId, int numberOfChildrens) {
        super();
        this.header = new InnerHeaderIndexBlock(containerId, blockId, numberOfChildrens);
    }

    public InnerIndexBlock(byte[] blockBytes) {
        super();
        this.header = new InnerHeaderIndexBlock(blockBytes);
    }

    @Override
    public byte[] toByteArray() {
        // TODO
        ByteArrayConcater byteConcater = new ByteArrayConcater(GlobalVariables.TAMANHO_BLOCO);
        byteConcater
                .concat(super.toByteArray());

        return byteConcater.getFinalByteArray();
    }

    @Override
    public InnerIndexBlock fromByteArray(byte[] byteArray) {
        // TODO
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

        String indexPath = IndexFileManager.getDiretorio(header.getContainerId());
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + this.getHeader().getBytesUsedByCollumnValue();

        CollumnValue col = new CollumnValue(value);
        if (!this.getHeader().existsSpaceForNewValueCollumn(col))
            throw new InnerIndexBlockFullCollumnValueException();

        GerenciadorDeIO.atualizarBytes(indexPath, offset, col.toByteArray());
        this.incrementCollumnCount(col);
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

    public void pushPointerToChild(BlocoId refToChild) throws IOException, ContainerNoExistent, IndexLeafBlockCannotPushPointerChildException, InnerIndexBlockPointerToChildIsFullException, IndexBlockNotFoundException {
        if (this.getHeader().isLeaf())
            throw new IndexLeafBlockCannotPushPointerChildException();

        IndexBlock block = IndexContainer.loadIndexBlock(RowId.create(this.getHeader().getContainerId(), refToChild.getValue()));
        int offset = lastPointerChildFree();
        if (offset < 0)
            throw new InnerIndexBlockPointerToChildIsFullException();
        String path = IndexFileManager.getDiretorio(this.getHeader().getContainerId());
        GerenciadorDeIO.atualizarBytes(path, offset, ByteArrayUtils.intToBytes(refToChild.getValue()));

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

    public IndexBlock loadPointerToChild(int i) throws IOException, ContainerNoExistent, IndexBlockNotFoundException {
        int offset = getPointerChildOffset(i);
        if (offset < 0)
            throw new IndexBlockNotFoundException();
        String path = IndexFileManager.getDiretorio(this.getHeader().getContainerId());
        int blockIdFinded = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(path, offset, InnerHeaderIndexBlock.POINTER_LENGTH));

        BlocoId block = BlocoId.create(blockIdFinded);
        return IndexContainer.loadIndexBlock(RowId.create(this.getHeader().getContainerId(), block.getValue()));
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

}
