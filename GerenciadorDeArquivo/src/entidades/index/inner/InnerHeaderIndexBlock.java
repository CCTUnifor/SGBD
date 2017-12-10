package entidades.index.inner;

import entidades.blocos.TipoBloco;
import entidades.index.abstrations.HeaderIndexBlock;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

public class InnerHeaderIndexBlock extends HeaderIndexBlock implements IBinary {
    public static final int HEADER_LENGTH = 12;
    public static final int POINTER_LENGTH = 4;

    private int numberOfChildrens;

    private int lastByteUsedByCollumnValue;
    private boolean isLeaf;
//    private int bytesUsedByChildren;

    InnerHeaderIndexBlock() {
        super(TipoBloco.INDEX);
        super.byteHeaderLength = HEADER_LENGTH;
    }

    InnerHeaderIndexBlock(ContainerId containerId, BlocoId blockId, int _numberOfChildrens) {
        super(containerId, blockId, TipoBloco.INDEX);
        super.byteHeaderLength = HEADER_LENGTH;

        this.numberOfChildrens = _numberOfChildrens;
        this.lastByteUsedByCollumnValue = this.getBytesUsedByChildren() + 1;
        this.isLeaf = false;
    }

    public InnerHeaderIndexBlock(byte[] blockBytes) {
        super(blockBytes);
        super.byteHeaderLength = HEADER_LENGTH;
        this.lastByteUsedByCollumnValue = this.getBytesUsedByChildren() + 1;

        if (blockBytes.length > HEADER_LENGTH)
            fromByteArray(blockBytes);
    }

    public InnerHeaderIndexBlock(int numberOfChildrens) {
        super(TipoBloco.INDEX);
        this.numberOfChildrens = numberOfChildrens;
        super.byteHeaderLength = HEADER_LENGTH;
        this.lastByteUsedByCollumnValue = this.getBytesUsedByChildren() + 1;
    }

    public int getBytesUsedByChildren() { return this.numberOfChildrens * POINTER_LENGTH; }
    public int getBytesUsedByCollumnValue() { return lastByteUsedByCollumnValue; }

    public void setLastByteUsedByCollumnValue(int lastByteUsedByCollumnValue) {
        this.lastByteUsedByCollumnValue = lastByteUsedByCollumnValue;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater byteConcater = new ByteArrayConcater(this.byteHeaderLength);
        byteConcater
                .concat(super.toByteArray())
                .concat(ByteArrayUtils.intTo3Bytes(this.getBytesUsedByCollumnValue()))
                .concat(ByteArrayUtils.booleanToByte(this.isLeaf))
                .concat(ByteArrayUtils.intTo3Bytes(this.getBytesUsedByChildren()));

        return byteConcater.getFinalByteArray();
    }

    @Override
    public InnerHeaderIndexBlock fromByteArray(byte[] byteArray) {
        super.fromByteArray(byteArray);
        this.isLeaf = ByteArrayUtils.byteArrayToBoolean(ByteArrayUtils.subArray(byteArray, 6, 1));
        this.numberOfChildrens = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 9, 3)) / POINTER_LENGTH;

        return this;
    }

}