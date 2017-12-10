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

    public void addColumnValue(String value) throws ContainerNoExistent, IOException, InnerIndexBlockFullCollumnValueException {
        if (this.header.getContainerId() == 0 || this.header.getContainerId() == -1)
            throw new ContainerNoExistent();

        String indexPath = IndexFileManager.getDiretorio(header.getContainerId());
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + this.getHeader().getBytesUsedByCollumnValue();

        ValueColumn col = new ValueColumn(value);
        if (!this.getHeader().existsSpaceForNewValueCollumn(col))
            throw new InnerIndexBlockFullCollumnValueException();

        GerenciadorDeIO.atualizarBytes(indexPath, offset, col.toByteArray());
        this.incrementCollumnCount(col);
    }

    private void incrementCollumnCount(ValueColumn col) throws IOException, ContainerNoExistent {
        int newIncrementedValue = this.getHeader().getBytesUsedByCollumnValue() + col.getFullLength();
        this.getHeader().setLastByteUsedByCollumnValue(newIncrementedValue);
        String indexPath = IndexFileManager.getDiretorio(this.getHeader().getContainerId());

        int offset = this.getHeader().getBlockPosition() + 5;
        GerenciadorDeIO.atualizarBytes(indexPath, offset, ByteArrayUtils.intTo3Bytes(newIncrementedValue));
    }

    private int havePositionFreeForNewCollumnValue() throws IOException, ContainerNoExistent {
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH;
        String indexPath = IndexFileManager.getDiretorio(this.header.getContainerId());
        boolean continueWhile = true;

        int x = offset + this.getHeader().getBytesUsedByCollumnValue();

        while (continueWhile && offset < x) {
            int length = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, ValueColumn.LENGTH));
            if (length == 0)
                return offset;
            offset += ValueColumn.LENGTH + length;
        }

        return -1;
    }

    public void addChildren(BlocoId blockId) {

    }


    public ValueColumn getValueCollumn(int i) throws IOException, ContainerNoExistent {
        String indexPath = IndexFileManager.getDiretorio(this.getHeader().getContainerId());
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + ((InnerHeaderIndexBlock) this.header).getBytesUsedByCollumnValue();
        int length = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, 4));

        return new ValueColumn(GerenciadorDeIO.getBytes(indexPath, offset, length));
    }

    public InnerHeaderIndexBlock getHeader() {
        return (InnerHeaderIndexBlock) this.header;
    }
}
