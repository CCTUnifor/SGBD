package entidades.blocos;

import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

public class BlocoDadoHeader implements IBinary {
    public static final int TABLE_BLOCK_LENGTH = 1024;

    private ContainerId containerId;
    private BlocoId blocoId;
    private TipoBloco tipoBloco;
    private int tamanhoUsado;

    public BlocoDadoHeader() {
        this.containerId = ContainerId.create();
        this.blocoId = BlocoId.create();
        this.tipoBloco = TipoBloco.DADOS;
        this.tamanhoUsado = 0;
    }

    public BlocoDadoHeader(int containerId, int blocoId) {
        this.containerId = ContainerId.create(containerId);
        this.blocoId = BlocoId.create(blocoId);
        this.tipoBloco = TipoBloco.DADOS;
        this.tamanhoUsado = 0;
    }

    public int getTamanhoUsado() {
        return this.tamanhoUsado;
    }

    public int getContainerId() {
        return containerId.getValue();
    }

    public int getBlocoId() {
        return blocoId.getValue();
    }

    public TipoBloco getTipoBloco() {
        return tipoBloco;
    }

    @Override
    public byte[] toByteArray() {

        ByteArrayConcater byteConcater = new ByteArrayConcater(8);
        byteConcater
                .concat(this.containerId.toByteArray())
                .concat(this.blocoId.toByteArray())
                .concat(ByteArrayUtils.intTo1Bytes(this.tipoBloco.ordinal()))
                .concat(ByteArrayUtils.intTo3Bytes(this.tamanhoUsado));

        return byteConcater.getFinalByteArray();
    }

    @Override
    public BlocoDadoHeader fromByteArray(byte[] byteArray) {

        this.containerId = this.containerId.fromByteArray(ByteArrayUtils.subArray(byteArray, 0, 1));
        this.blocoId =  this.blocoId.fromByteArray(ByteArrayUtils.subArray(byteArray, 1, 3));
        this.tipoBloco = ByteArrayUtils.byteArrayToEnum(ByteArrayUtils.subArray(byteArray, 4, 1), TipoBloco.values());
        this.tamanhoUsado = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 5, 3));

        return this;
    }

    public void incrementarTamanhoUsado(int tamanho) {
        this.tamanhoUsado += tamanho;
    }
}
