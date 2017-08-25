package entidades.blocos;

import interfaces.IBinary;

/**
 * Created by José Victor on 09/08/2017.
 */
public class BlocoControle implements IBinary{
    private BlocoContainerHeader blocoHeader;
    private BlocoDado[] blocoDados;

    public BlocoControle(int containerId) {
        this.blocoHeader = new BlocoContainerHeader(containerId);
    }

    public BlocoContainerHeader getHeader() {
        return blocoHeader;
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
