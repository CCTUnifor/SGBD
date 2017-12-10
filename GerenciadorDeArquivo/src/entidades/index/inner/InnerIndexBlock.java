package entidades.index.inner;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorDeIO;
import entidades.index.IndexContainer;
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

    public void addColumnValue(String value) throws ContainerNoExistent, IOException {
        if (this.header.getContainerId() == 0 || this.header.getContainerId() == -1)
            throw new ContainerNoExistent();

        String indexPath = IndexFileManager.getDiretorio(header.getContainerId());
        int offset = InnerHeaderIndexBlock.HEADER_LENGTH + ((InnerHeaderIndexBlock) header).getBytesUsedByCollumnValue();

        ValueColumn col = new ValueColumn(value);
        GerenciadorDeIO.atualizarBytes(indexPath, offset, col.toByteArray());
        this.incrementCollumnCount(col);
    }

    private void incrementCollumnCount(ValueColumn col) throws IOException, ContainerNoExistent {
        int newIncrementedValue = ((InnerHeaderIndexBlock)this.header).getBytesUsedByCollumnValue() + col.getFullLength();
        ((InnerHeaderIndexBlock)this.header).setLastByteUsedByCollumnValue(newIncrementedValue);
        IndexContainer container = IndexContainer.getJustContainer(this.getHeader().getContainerId());
        String indexPath = IndexFileManager.getDiretorio(this.getHeader().getContainerId());

        int offset = InnerHeaderIndexBlock.HEADER_LENGTH + (this.getHeader().getBlockId() - 1) * container.getBlocoControle().getHeader().getTamanhoDosBlocos();
        GerenciadorDeIO.atualizarBytes(indexPath, offset, ByteArrayUtils.intToBytes(newIncrementedValue));
    }

    public void addChildren(BlocoId blockId) {

    }

    public ValueColumn getValueCollumn(int i) {
        
    }
}
