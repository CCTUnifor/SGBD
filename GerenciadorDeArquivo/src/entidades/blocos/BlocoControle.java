package entidades.blocos;

import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

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
        bc.concat(this.blocoHeader.toByteArray())
                .concat(this.bytesDescritores());
        return bc.getFinalByteArray();
    }

    @Override
    public BlocoControle fromByteArray(byte[] byteArray) {

        this.blocoHeader.fromByteArray(ByteArrayUtils.subArray(byteArray, 0, 11));
        // todo
        // descritores

        return this;
    }

    public void adicionarDescritor(Descritor descritor){
        this.descritores.add(descritor);
    }

    public void adicionarDescritores(ArrayList<Descritor> descritores) {
        this.descritores.addAll(descritores);
    }

    private byte[] bytesDescritores() {
        ByteArrayConcater bc = new ByteArrayConcater();
        for (Descritor descritor : this.descritores) {
            bc.concat(descritor.toByteArray());
        }
        return bc.getFinalByteArray();
    }
}
