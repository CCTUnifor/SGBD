package entidades.index.inner;

import entidades.index.IndexBlock;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;

public class InnerIndexBlock extends IndexBlock implements IBinary {
    private Tuple valueRef;
    private Pointer pointerRef;

    InnerIndexBlock(ContainerId containerId, BlocoId blockId) {
        super();
        this.header = new InnerHeaderIndexBlock(containerId, blockId);
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public <T> T fromByteArray(byte[] byteArray) {
        return null;
    }
}
