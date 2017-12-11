package entidades.index.inner;

import entidades.GerenciadorDeIO;
import entidades.blocos.TipoBloco;
import entidades.index.IndexContainer;
import entidades.index.IndexFileManager;
import entidades.index.abstrations.HeaderIndexBlock;
import exceptions.ContainerNoExistent;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

import java.io.IOException;

public class InnerHeaderIndexBlock extends HeaderIndexBlock implements IBinary {
    public static final int HEADER_LENGTH = 11;
    public static final int POINTER_LENGTH = 4;

    private int ordem;
    private int lastByteUsedByCollumnValue;

    InnerHeaderIndexBlock() {
        super(TipoBloco.INDEX_INNER);
        super.byteHeaderLength = HEADER_LENGTH;
    }

    InnerHeaderIndexBlock(ContainerId containerId, BlocoId blockId, int ordem) {
        super(containerId, blockId, TipoBloco.INDEX_INNER);
        super.byteHeaderLength = HEADER_LENGTH;

        this.ordem = ordem;
        this.lastByteUsedByCollumnValue = this.getMaxLengthChildren() * POINTER_LENGTH;
    }

    public InnerHeaderIndexBlock(byte[] blockBytes) {
        super(blockBytes);
        super.byteHeaderLength = HEADER_LENGTH;
        this.lastByteUsedByCollumnValue = this.getMaxLengthChildren() * POINTER_LENGTH;
    }

    public InnerHeaderIndexBlock(int ordem) {
        super(TipoBloco.INDEX_INNER);
        this.ordem = ordem;
        super.byteHeaderLength = HEADER_LENGTH;
        this.lastByteUsedByCollumnValue = this.getMaxLengthChildren() * POINTER_LENGTH;
    }

    public int getMaxLengthChildren() { return this.ordem -1; }
    public int getNumberMaxKeys() {  return this.ordem -1; }

    public int getBytesUsedByCollumnValue() throws IOException, ContainerNoExistent {
        String path = IndexFileManager.getDiretorio(this.getContainerId());
        int offset = this.getBlockPosition() + 8;

        this.lastByteUsedByCollumnValue = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(path, offset, 3));
        return lastByteUsedByCollumnValue;
    }

    public void setLastByteUsedByCollumnValue(int lastByteUsedByCollumnValue) {
        this.lastByteUsedByCollumnValue = lastByteUsedByCollumnValue;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater byteConcater = new ByteArrayConcater();
        byteConcater
                .concat(super.toByteArray())
                .concat(ByteArrayUtils.intTo3Bytes(this.ordem))
                .concat(ByteArrayUtils.intTo3Bytes(this.lastByteUsedByCollumnValue));
        return byteConcater.getFinalByteArray();
    }

    @Override
    public InnerHeaderIndexBlock fromByteArray(byte[] byteArray) {
        super.fromByteArray(byteArray);
        this.ordem = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 5, 3));
        this.lastByteUsedByCollumnValue = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 8, 3));

        return this;
    }

    public boolean existsSpaceForNewValueCollumn(CollumnValue col) throws IOException, ContainerNoExistent {
        IndexContainer i = IndexContainer.getJustContainer(ContainerId.create(this.getContainerId()));
        return col.toByteArray().length + this.getBytesUsedByCollumnValue() <= i.getBlocoControle().getHeader().getTamanhoDosBlocos();
    }

}