package entidades.index.inner;

import entidades.blocos.TipoBloco;
import entidades.index.HeaderIndexBlock;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

class InnerHeaderIndexBlock extends HeaderIndexBlock implements IBinary {

    private int lastValueRef;
    private boolean isLeaf;
    private int lastPointerRef;

    InnerHeaderIndexBlock(ContainerId containerId, BlocoId blockId) {
        this.containerId = containerId;
        this.blockId = blockId;
        this.blockType = TipoBloco.INDEX;
        this.lastValueRef = -1;
        this.isLeaf = false;
        this.lastPointerRef = -1;
    }


    @Override
    public byte[] toByteArray() {
        ByteArrayConcater byteConcater = new ByteArrayConcater(11);
        byteConcater
                .concat(this.containerId.toByteArray())
                .concat(this.blockId.toByteArray())
                .concat(ByteArrayUtils.intTo1Bytes(this.blockType.ordinal()))
                .concat(ByteArrayUtils.intTo3Bytes(this.lastValueRef))
                .concat(ByteArrayUtils.booleanToByte(this.isLeaf))
                .concat(ByteArrayUtils.intTo3Bytes(this.lastPointerRef));

        return byteConcater.getFinalByteArray();
    }

    @Override
    public InnerHeaderIndexBlock fromByteArray(byte[] byteArray) {
        this.containerId = this.containerId.fromByteArray(ByteArrayUtils.subArray(byteArray, 0, 1));
        this.blockId =  this.blockId.fromByteArray(ByteArrayUtils.subArray(byteArray, 1, 3));
        this.blockType = ByteArrayUtils.byteArrayToEnum(ByteArrayUtils.subArray(byteArray, 4, 1), TipoBloco.values());
        this.lastValueRef = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 5, 3));
        this.isLeaf = ByteArrayUtils.byteArrayToBoolean(ByteArrayUtils.subArray(byteArray, 6, 1));
        this.lastPointerRef = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 9, 3));

        return this;
    }
}