package entidades.index;

import entidades.blocos.TipoBloco;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

public abstract class HeaderIndexBlock implements IBinary {
    protected ContainerId containerId;
    protected BlocoId blockId;
    protected TipoBloco blockType;

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater byteConcater = new ByteArrayConcater(11);
        byteConcater
                .concat(this.containerId.toByteArray())
                .concat(this.blockId.toByteArray())
                .concat(ByteArrayUtils.intTo1Bytes(this.blockType.ordinal()));
//                .concat(ByteArrayUtils.intTo3Bytes(this.lastValueRef))
//                .concat(ByteArrayUtils.booleanToByte(this.isLeaf))
//                .concat(ByteArrayUtils.intTo3Bytes(this.lastPointerRef));

        return byteConcater.getFinalByteArray();
    }

    @Override
    public HeaderIndexBlock fromByteArray(byte[] byteArray) {
        this.containerId = this.containerId.fromByteArray(ByteArrayUtils.subArray(byteArray, 0, 1));
        this.blockId =  this.blockId.fromByteArray(ByteArrayUtils.subArray(byteArray, 1, 3));
        this.blockType = ByteArrayUtils.byteArrayToEnum(ByteArrayUtils.subArray(byteArray, 4, 1), TipoBloco.values());
//        this.lastValueRef = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 5, 3));
//        this.isLeaf = ByteArrayUtils.byteArrayToBoolean(ByteArrayUtils.subArray(byteArray, 6, 1));
//        this.lastPointerRef = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 9, 3));

        return this;
        
    }
}
