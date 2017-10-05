package entidades.blocos;

import interfaces.IBinary;
import interfaces.IPrint;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.awt.color.ICC_ProfileRGB;
import java.util.ArrayList;

public class BlocoDado implements IBinary, IPrint {

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
                .concat(this.header.toByteArray())
                .concat(this.bytesTuples());

        return byteConcater.getFinalByteArray();
    }

    @Override
    public String print() {
        String parse = "";
        for (Linha linha : tuples){
            parse += linha.print() + "\n";
        }
        return parse;
    }

    private byte[] bytesTuples() {
        if (this.tuples.size() == 0)
            return new byte[0];

        ByteArrayConcater bc = new ByteArrayConcater();
        for (Linha tuple : this.tuples) {
            bc.concat(tuple.toByteArray());
        }
        return bc.getFinalByteArray();
    }

    @Override
    public BlocoDado fromByteArray(byte[] byteArray) {
        this.header = this.header.fromByteArray(ByteArrayUtils.subArray(byteArray, 0, 8));
        this.tuples.addAll(this.linhasFromByteArray(byteArray));

        return  this;
    }

    private ArrayList<Linha> linhasFromByteArray(byte[] byteArray) {
        ArrayList<Linha> linhas = new ArrayList<Linha>();
        int indexOndeComecaOsDados = 8;

        while(indexOndeComecaOsDados < byteArray.length && indexOndeComecaOsDados < this.header.getTamanhoUsado()) {
            int tamanhoLinha = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, indexOndeComecaOsDados, 4));
            byte[] linhaBytes = ByteArrayUtils.subArray(byteArray, indexOndeComecaOsDados, tamanhoLinha);

            Linha tuple = new Linha(linhaBytes);
            indexOndeComecaOsDados += tuple.getTamanho();
            linhas.add(tuple);
        }

        return linhas;
    }

    public boolean adicionarTupla(Linha tupla) {

        this.tuples.add(tupla);
        this.header.incrementarTamanhoUsado(tupla.getTamanhoCompleto());

        return true;
    }

    public ArrayList<Linha> getTuples() {
        return this.tuples;
    }

    public String toString() {
        return "Row Id: " + this.getHeader().getContainerId() + "." + this.getHeader().getBlocoId() + " | Tipo: " + this.header.getTipoBloco().toString() + " | Tuplas: " + this.tuples.size();
    }
}
