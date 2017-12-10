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

    private int numberOfChildrens;

    private int lastByteUsedByCollumnValue;

    InnerHeaderIndexBlock() {
        super(TipoBloco.INDEX_INNER);
        super.byteHeaderLength = HEADER_LENGTH;
    }

    InnerHeaderIndexBlock(ContainerId containerId, BlocoId blockId, int _numberOfChildrens) {
        super(containerId, blockId, TipoBloco.INDEX_INNER);
        super.byteHeaderLength = HEADER_LENGTH;

        this.numberOfChildrens = _numberOfChildrens;
        this.lastByteUsedByCollumnValue = this.getBytesUsedByChildren() + 1;
    }

    public InnerHeaderIndexBlock(byte[] blockBytes) {
        super(blockBytes);
        super.byteHeaderLength = HEADER_LENGTH;
        this.lastByteUsedByCollumnValue = this.getBytesUsedByChildren() + 1;
    }

    public InnerHeaderIndexBlock(int numberOfChildrens) {
        super(TipoBloco.INDEX_INNER);
        this.numberOfChildrens = numberOfChildrens;
        super.byteHeaderLength = HEADER_LENGTH;
        this.lastByteUsedByCollumnValue = this.getBytesUsedByChildren() + 1;
    }

    public int getBytesUsedByChildren() { return this.numberOfChildrens * POINTER_LENGTH; }

    public int getBytesUsedByCollumnValue() throws IOException, ContainerNoExistent {
        String path = IndexFileManager.getDiretorio(this.getContainerId());
        int offset = this.getBlockPosition() + 5;

        this.lastByteUsedByCollumnValue = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(path, offset, 3));
        return lastByteUsedByCollumnValue;
    }

    public void setLastByteUsedByCollumnValue(int lastByteUsedByCollumnValue) {
        this.lastByteUsedByCollumnValue = lastByteUsedByCollumnValue;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater byteConcater = new ByteArrayConcater(this.byteHeaderLength);
        byteConcater
                .concat(super.toByteArray())
                .concat(ByteArrayUtils.intTo3Bytes(this.lastByteUsedByCollumnValue))
                .concat(ByteArrayUtils.intTo3Bytes(this.getBytesUsedByChildren()));

        return byteConcater.getFinalByteArray();
    }

    @Override
    public InnerHeaderIndexBlock fromByteArray(byte[] byteArray) {
        super.fromByteArray(byteArray);
        this.lastByteUsedByCollumnValue = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 5, 3));
        this.numberOfChildrens = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 8, 3)) / POINTER_LENGTH;

        return this;
    }

    public boolean existsSpaceForNewValueCollumn(CollumnValue col) throws IOException, ContainerNoExistent {
        IndexContainer i = IndexContainer.getJustContainer(ContainerId.create(this.getContainerId()));
        return col.toByteArray().length + this.getBytesUsedByCollumnValue() <= i.getBlocoControle().getHeader().getTamanhoDosBlocos();
    }

}