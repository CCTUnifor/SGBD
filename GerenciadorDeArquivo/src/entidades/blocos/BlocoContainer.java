package entidades.blocos;

import exceptions.BlocoSemEspacoException;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

import java.util.ArrayList;

public class BlocoContainer implements IBinary {
    private BlocoControle blocoControle;
    private ArrayList<BlocoDado> blocosDados;

    public BlocoContainer(int containerId) {
        this.blocoControle = new BlocoControle(containerId);
        this.blocosDados = new ArrayList<BlocoDado>();
    }

    public int getContainerId () {
        return this.blocoControle.getHeader().getContainerId();
    }

    public BlocoControle getBlocoControle() {
        return blocoControle;
    }

    public ArrayList<BlocoDado> getBlocosDados() {
        return this.blocosDados;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater();
        bc.concat(blocoControle.toByteArray())
                .concat(bytesBlocosDados());

        return bc.getFinalByteArray();
    }

    @Override
    public BlocoContainer fromByteArray(byte[] byteArray) {
        this.blocoControle.fromByteArray(byteArray);
        int indexOndeComecaOBlocoDeDados = 11 + blocoControle.toByteArray().length;

        // TODO
        // blocosDados.fromByteArray();

        for (int i = indexOndeComecaOBlocoDeDados; i < 11 + this.blocoControle.getHeader().getTamanhoDescritor(); i += this.blocoControle.getHeader().getTamanhoDosBlocos()) {
            BlocoDado bloco = new BlocoDado(ByteArrayUtils.subArray(byteArray, i, 1 + this.blocoControle.getHeader().getTamanhoDosBlocos()));
            this.blocosDados.add(bloco);
        }

        return this;
    }

    public void adicionarBlocos(ArrayList<BlocoDado> blocos) {
        this.blocosDados.addAll(blocos);
    }

    private byte[] bytesBlocosDados() {
        ByteArrayConcater bc = new ByteArrayConcater();
        for (BlocoDado bloco : this.blocosDados) {
            bc.concat(bloco.toByteArray());
        }
        return bc.getFinalByteArray();
    }
}
