package entidades.blocos;

import interfaces.IBinary;
import utils.ByteArrayConcater;

import java.util.ArrayList;

/**
 * Created by José Victor on 09/08/2017.
 */
public class BlocoControle implements IBinary{
    private BlocoContainerHeader blocoHeader;
    private ArrayList<Descritor> descritores;

    public BlocoControle(int containerId) {
        this.descritores = new ArrayList<Descritor>();
        this.blocoHeader = new BlocoContainerHeader(containerId);
    }

    public BlocoContainerHeader getHeader() {
        return blocoHeader;
    }

    public int getContainerId() { return this.blocoHeader.getContainerId(); }
    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater();
        bc.concat(this.blocoHeader.toByteArray());
//                .concat(descritores)
        return bc.getFinalByteArray();
    }

    @Override
    public <T> T fromByteArray(byte[] byteArray) {
        return null;
    }

    public void adicionarDescritor(Descritor descritor){
        this.descritores.add(descritor);
    }

    public void adicionarDescritores(ArrayList<Descritor> descritores) {
        this.descritores.addAll(descritores);
    }
}
