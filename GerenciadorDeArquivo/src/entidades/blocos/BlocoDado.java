package entidades.blocos;

import exceptions.BlocoSemEspacoException;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.util.ArrayList;

/**
 * Created by José Victor on 09/08/2017.
 */
public class BlocoDado implements IBinary{

    private BlocoDadoHeader header;
    private ArrayList<Linha> tuples;


    public BlocoDado(int containerId, int blocoId) {
        this.header = new BlocoDadoHeader(containerId, blocoId);
        this.tuples = new ArrayList<Linha>();
    }

    public BlocoDado(int containerId, int blocoId, ArrayList<Linha> dados) {
        this.header = new BlocoDadoHeader(containerId, blocoId);
        this.tuples = dados;
    }

    public BlocoDado(byte[] bytes) {
        this.header = new BlocoDadoHeader();
        this.tuples = new ArrayList<Linha>();
        this.fromByteArray(bytes);
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
        this.header = this.header.fromByteArray(ByteArrayUtils.subArray(byteArray, 0, 8));
        // TODO
        // TUPLES
        
        return  this;
    }

    public boolean adicionarTupla(Linha tupla) {
        if (!ByteArrayUtils.aindaTemEspaco(this, tupla))
            return false;

        this.tuples.add(tupla);
        this.header.incrementarTamanhoUsado(tupla.getTamanho());

        return true;
    }
}
