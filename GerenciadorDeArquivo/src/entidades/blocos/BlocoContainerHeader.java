package entidades.blocos;

import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.GlobalVariables;

import java.awt.*;

public class BlocoContainerHeader implements IBinary{

    private ContainerId containerId;
    private int tamanhoDosBlocos = GlobalVariables.TAMANHO_BLOCO;
    private char statusContainer;
    private int proximoBlocoLivre = 0;
    private int tamanhoDescritor;

    public BlocoContainerHeader(int containerId) {
        this.containerId = ContainerId.create(containerId);
    }

    public int getContainerId() {
        return this.containerId.getValue();
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater concater = new ByteArrayConcater(11);
        concater
                .concat(this.containerId.toByteArray());

        return concater.getFinalByteArray();
    }

    @Override
    public BlocoContainerHeader fromByteArray(byte[] byteArray) {
        return null;
    }
}
