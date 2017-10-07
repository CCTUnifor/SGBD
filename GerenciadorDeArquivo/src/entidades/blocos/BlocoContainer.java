package entidades.blocos;

import interfaces.IBinary;
import interfaces.IPrint;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

import java.util.ArrayList;

public class BlocoContainer implements IBinary, IPrint {
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
    public String print() {
        String parse = "";
        parse += this.blocoControle.print() + "\n";
        for (BlocoDado bloco : blocosDados) {
            parse += bloco.print();
        }
        return parse;
    }

    @Override
    public BlocoContainer fromByteArray(byte[] byteArray) {
        this.blocoControle.fromByteArray(byteArray);
        this.blocosDados.addAll(this.blocoDadosFromByteArray(byteArray));

        return this;
    }

    private ArrayList<BlocoDado> blocoDadosFromByteArray(byte[] byteArray) {
        ArrayList<BlocoDado> dados = new ArrayList<BlocoDado>();

        int indexOndeComecaOBlocoDeDados = blocoControle.toByteArray().length;
        for (int i = indexOndeComecaOBlocoDeDados; i < byteArray.length; i += this.blocoControle.getHeader().getTamanhoDosBlocos()) {
            BlocoDado bloco = new BlocoDado(ByteArrayUtils.subArray(byteArray, i, this.blocoControle.getHeader().getTamanhoDosBlocos()));
            dados.add(bloco);
        }

        return dados;
    }
    public void adicionarBlocos(ArrayList<BlocoDado> blocos) {
        this.blocosDados.addAll(blocos);
    }

    private byte[] bytesBlocosDados() {
        ByteArrayConcater bc = new ByteArrayConcater();
        this.blocosDados.stream().forEach(bloco -> bc.concat(bloco.toByteArray()));
        return bc.getFinalByteArray();
    }

    public BlocoDado getBloco(byte[] bytes, RowId rowId) {
        this.blocoControle.fromByteArray(bytes);

        int indexOndeComecaOBlocoDeDados = blocoControle.toByteArray().length;
        for (int i = indexOndeComecaOBlocoDeDados; i < bytes.length; i += this.blocoControle.getHeader().getTamanhoDosBlocos()) {
            BlocoDado bloco = new BlocoDado(ByteArrayUtils.subArray(bytes, i, this.blocoControle.getHeader().getTamanhoDosBlocos()));
            if (bloco.getHeader().getContainerId() == rowId.getContainerId() && bloco.getHeader().getBlocoId() == rowId.getBlocoId())
                return bloco;
        }

        return null;
    }

    public void adicionarBloco(BlocoDado bloco) {
        this.blocosDados.add(bloco);
    }
}
