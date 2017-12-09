package entidades.index;

import entidades.blocos.TipoBloco;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;

class IndexHeaderBlock implements IBinary{
    private ContainerId containerId;
    private BlocoId blocoId;
    private TipoBloco tipoBloco;
    private int tamanhoUsado;

    IndexHeaderBlock() {
        this.tipoBloco = TipoBloco.INDEX;
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
