package entidades.index.inner;

import entidades.GerenciadorDeIO;
import entidades.index.IndexFileManager;
import entidades.index.abstrations.IndexBlock;
import exceptions.ContainerNoExistent;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.io.IOException;

public class InnerIndexBlock extends IndexBlock implements IBinary {
    private Tuple valueRef;
    private Pointer pointerRef;

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

    public InnerHeaderIndexBlock getHeader() {
        return (InnerHeaderIndexBlock) this.header;
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
        boolean continueWhile = true;

        int offsetMax = offset + this.getHeader().getBytesUsedByCollumnValue();
        int count = 0;

        while (continueWhile && offset < offsetMax && count <= i) {
            int length = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH));
            if (length != 0 && count == i)
                return offset;
            offset += CollumnValue.LENGTH + length;
            count++;
        }

        return -1;
    }
}
