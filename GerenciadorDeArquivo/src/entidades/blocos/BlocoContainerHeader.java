package entidades.blocos;

import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.awt.*;

public class BlocoContainerHeader implements IBinary{

    private ContainerId containerId;
    private int tamanhoDosBlocos = GlobalVariables.TAMANHO_BLOCO;
    private int statusContainer;
    private int proximoBlocoLivre = 0;
    private int tamanhoDescritor;

    public BlocoContainerHeader(int containerId) {
        this.containerId = ContainerId.create(containerId);
    }

    public int getContainerId() {
        return this.containerId.getValue();
    }

    public int getTamanhoDosBlocos() {
        return this.tamanhoDosBlocos;
    }

    public int getStatusContainer() {
        return this.statusContainer;
    }

    public int getProximoBlocoLivre() {
        return this.proximoBlocoLivre;
    }

    public void adicionarProximoBlocoLivre() {
        this.proximoBlocoLivre++;
    }

    public int getTamanhoDescritor() {
        return  this.tamanhoDescritor;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater concater = new ByteArrayConcater(11);
        concater
                .concat(this.containerId.toByteArray())
                .concat(ByteArrayUtils.intTo3Bytes(this.tamanhoDosBlocos))
                .concat(ByteArrayUtils.intTo1Bytes(this.statusContainer))
                .concat(ByteArrayUtils.intToBytes(this.proximoBlocoLivre))
                .concat(ByteArrayUtils.intTo2Bytes(this.tamanhoDescritor));

        return concater.getFinalByteArray();
    }

    @Override
    public BlocoContainerHeader fromByteArray(byte[] byteArray) {
        return null;
    }
}
