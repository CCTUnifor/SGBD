package entidades.blocos;

import interfaces.IBinary;

public class Descritor implements IBinary {
    private TipoDado tipoDado;
    private String nome;
    private int tamanho;

    public Descritor(String coluna) {
        String[] x = coluna.split( "\\[");
        this.nome = x[0];
        this.tipoDado = x[1].contains("I") ? TipoDado.INTEIRO : TipoDado.STRING;
        String regex = "\\<(?<meuGrupo>.*?)\\>";

        this.tamanho = Integer.parseInt(x[1].split("\\(.*\\)")[0]);
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
