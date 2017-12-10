package entidades.index.abstrations;

import entidades.blocos.TipoBloco;
import entidades.index.inner.InnerHeaderIndexBlock;
import entidades.index.inner.InnerIndexBlock;
import entidades.index.leaf.LeafHeaderIndexBlock;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

public abstract class HeaderIndexBlock implements IBinary {
    protected ContainerId containerId;
    protected BlocoId blockId;
    protected TipoBloco blockType;
    protected int byteHeaderLength;

    public HeaderIndexBlock(ContainerId containerId, BlocoId blockId, TipoBloco blockType) {
        this.containerId = containerId;
        this.blockId = blockId;
        this.blockType = blockType;
    }

    public HeaderIndexBlock(TipoBloco index) {
        this.containerId = ContainerId.create(-1);
        this.blockId = BlocoId.create(-1);
        blockType = index;
    }

    public HeaderIndexBlock(byte[] bytes) {
        this.containerId = ContainerId.create(-1);
        this.blockId = BlocoId.create(-1);
        fromByteArray(bytes);
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater byteConcater = new ByteArrayConcater(5);
        byteConcater
                .concat(this.containerId.toByteArray())
                .concat(this.blockId.toByteArray())
                .concat(ByteArrayUtils.intTo1Bytes(this.blockType.ordinal()));

        return byteConcater.getFinalByteArray();
    }

    @Override
    public HeaderIndexBlock fromByteArray(byte[] byteArray) {
        this.containerId = this.containerId.fromByteArray(ByteArrayUtils.subArray(byteArray, 0, 1));
        this.blockId =  this.blockId.fromByteArray(ByteArrayUtils.subArray(byteArray, 1, 3));
        this.blockType = ByteArrayUtils.byteArrayToEnum(ByteArrayUtils.subArray(byteArray, 4, 1), TipoBloco.values());

        return this;
    }

    public static HeaderIndexBlock fromByteArrayStatic(byte[] byteArray) {
        TipoBloco blockType = ByteArrayUtils.byteArrayToEnum(ByteArrayUtils.subArray(byteArray, 4, 1), TipoBloco.values());
        if (blockType == TipoBloco.INDEX)
            return new InnerHeaderIndexBlock(byteArray);

        return new LeafHeaderIndexBlock(byteArray);
    }

    public void setBlockId(int blockId) {
        this.blockId = BlocoId.create(blockId);
    }
    public void setContainerId(int containerId) {
        this.containerId = ContainerId.create(containerId);
    }
    public int getContainerId() { return containerId.getValue(); }
    public int getBlockId() { return blockId.getValue(); }
}
