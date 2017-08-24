package entidades;

import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

public class BlocoDadoHeader implements IBinary {

    private ContainerId containerId;
    private BlocoId blocoId;
    private TipoBloco tipoBloco;
    private int tamanhoUsado;

    public BlocoDadoHeader(int containerId, int blocoId) {
        this.containerId = ContainerId.create(containerId);
        this.blocoId = BlocoId.create(blocoId);
        this.tipoBloco = TipoBloco.DADOS;
        this.tamanhoUsado = 0;
    }

    @Override
    public byte[] toByteArray() {

        byte[] containerIdBytes = this.containerId.toByteArray();
        byte[] blocoIdBytes = this.blocoId.toByteArray();

        ByteArrayConcater byteConcater = new ByteArrayConcater();
        byteConcater
                .concat(containerIdBytes)
                .concat(blocoIdBytes);

        return byteConcater.getByteArray();
    }

    @Override
    public BlocoDadoHeader fromByteArray(byte[] byteArray) {

        byte[] containerIdBytes = ByteArrayUtils.subArray(byteArray, 0, 1);  // 0   ContainerId
        byte[] blocoIdBytes = ByteArrayUtils.subArray(byteArray, 1, 3);      // 1-3 BlocoId
        byte[] tipoBlocoBytes = ByteArrayUtils.subArray(byteArray, 4, 1);    // 4   TipoBloco
        byte[] tamanhoUsadoBytes = ByteArrayUtils.subArray(byteArray, 5, 3); // 5-7 Tamanho Utilizado

        this.containerId = this.containerId.fromByteArray(containerIdBytes);
        this.blocoId =  this.blocoId.fromByteArray(blocoIdBytes);
        this.tipoBloco = ByteArrayUtils.byteArrayToEnum(tipoBlocoBytes, TipoBloco.values());
        this.tamanhoUsado = ByteArrayUtils.byteArrayToInt(tamanhoUsadoBytes);

        return this;
    }
}
