package entidades;

import interfaces.IBinary;

/**
 * Created by José Victor on 09/08/2017.
 */
public class BlocoDado implements IBinary<BlocoDado>{

    private BlocoDadoHeader header;

    public BlocoDado() {
        this.header = new BlocoDadoHeader();
    }

    public BlocoDadoHeader getHeader() {
        return header;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public BlocoDado fromByteArray(byte[] byteArray) {
        return null;
    }
}
