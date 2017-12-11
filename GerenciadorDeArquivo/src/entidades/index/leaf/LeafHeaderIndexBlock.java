package entidades.index.leaf;

import entidades.blocos.RowId;
import entidades.blocos.TipoBloco;
import entidades.index.abstrations.HeaderIndexBlock;
import exceptions.ContainerNoExistent;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

import java.io.IOException;

public class LeafHeaderIndexBlock extends HeaderIndexBlock implements IBinary {
    public static final int HEADER_LENGTH = 15;
    private BlocoId leafPrev;
    private BlocoId leafNext;
    private RowId rowIdData;

    LeafHeaderIndexBlock(RowId rowId) {
        super(TipoBloco.INDEX_LEAF);
        super.byteHeaderLength = HEADER_LENGTH;
        this.leafPrev  = BlocoId.create(-1);
        this.leafNext  = BlocoId.create(-1);
        this.rowIdData = rowId;
    }

    public LeafHeaderIndexBlock(byte[] blockBytes) {
        super(blockBytes);
        super.byteHeaderLength = HEADER_LENGTH;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater byteConcater = new ByteArrayConcater(HEADER_LENGTH);
        byteConcater
                .concat(super.toByteArray())
                .concat(this.leafPrev.toByteArray())
                .concat(this.leafNext.toByteArray())
                .concat(ContainerId.create(this.rowIdData.getContainerId()).toByteArray())
                .concat(BlocoId.create(this.rowIdData.getBlocoId()).toByteArray());

        return byteConcater.getFinalByteArray();
    }

    @Override
    public HeaderIndexBlock fromByteArray(byte[] byteArray) {
        super.fromByteArray(byteArray);
        this.leafPrev = BlocoId.create(ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 5, 3)));
        this.leafNext = BlocoId.create(ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 8, 3)));

        ContainerId containerId = ContainerId.create(ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 11, 1)));
        BlocoId blocoId = BlocoId.create(ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 12, 3)));
        this.rowIdData = RowId.create(containerId.getValue(), blocoId.getValue());

        return this;
    }

    public RowId getRowIdData() {
        return rowIdData;
    }
}
