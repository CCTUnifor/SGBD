package entidades.blocos;

import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.GlobalVariables;

import java.util.ArrayList;

/**
 * Created by José Victor on 09/08/2017.
 */
public class BlocoDado implements IBinary{

    private BlocoDadoHeader header;
    private ArrayList<Object> tuples;

    public BlocoDado(int containerId, int blocoId) {
        this.header = new BlocoDadoHeader(containerId, blocoId);
    }

    public BlocoDado(int containerId, int blocoId, ArrayList<Object> dados) {
        this.header = new BlocoDadoHeader(containerId, blocoId);
        this.tuples = dados;
    }

    public BlocoDadoHeader getHeader() {
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
    public BlocoDado fromByteArray(byte[] byteArray) {
        return  null;
    }

}
