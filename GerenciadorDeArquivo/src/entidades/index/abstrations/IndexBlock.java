package entidades.index.abstrations;

import entidades.blocos.TipoBloco;
import entidades.index.inner.CollumnValue;
import exceptions.innerBlock.InnerIndexBlockValueCollumnNotFoundException;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

public abstract class IndexBlock implements IBinary {
    protected HeaderIndexBlock header;

    public HeaderIndexBlock getHeader() {
        return header;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater byteConcater = new ByteArrayConcater(GlobalVariables.TAMANHO_BLOCO);
        byteConcater
                .concat(this.header.toByteArray());

        return byteConcater.getFinalByteArray();
    }

    @Override
    public IndexBlock fromByteArray(byte[] byteArray) {
        this.header = this.header.fromByteArray(ByteArrayUtils.subArray(byteArray, 0, header.byteHeaderLength));

        return this;
    }

    public boolean isLeaf() {
        return getHeader().getBlockType() == TipoBloco.INDEX_LEAF;
    }




}
