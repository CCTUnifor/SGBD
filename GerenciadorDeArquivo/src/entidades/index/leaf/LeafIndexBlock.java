package entidades.index.leaf;

import entidades.blocos.RowId;
import entidades.index.abstrations.IndexBlock;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.GlobalVariables;

public class LeafIndexBlock extends IndexBlock implements IBinary {

    public LeafIndexBlock(RowId ref) {
        super();
        this.header = new LeafHeaderIndexBlock(ref);
    }

    public LeafIndexBlock(byte[] blockBytes) {
        super();
        this.header = new LeafHeaderIndexBlock(blockBytes);
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater byteConcater = new ByteArrayConcater(GlobalVariables.TAMANHO_BLOCO);
        byteConcater
                .concat(super.toByteArray());

        return byteConcater.getFinalByteArray();
    }

    @Override
    public LeafIndexBlock fromByteArray(byte[] byteArray) {
        return null;
    }

    public LeafHeaderIndexBlock getHeader() {
        return (LeafHeaderIndexBlock) this.header;
    }
}
