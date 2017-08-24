package utils;

public class ByteArrayConcater {

    private byte[] array;
    private int tamanhoArray;

    public ByteArrayConcater() { }

    public ByteArrayConcater(int tamanhoArray) {
        this.tamanhoArray = tamanhoArray;
//        this.array = new byte[tamanhoArray];
    }

    public ByteArrayConcater concat(byte[] arrayFromConcat) {
        if (this.array == null){
            this.array = arrayFromConcat;
            return this;
        }

        if (this.array.length + arrayFromConcat.length > this.tamanhoArray)
            throw new Error("Ultrapassou o array.");

        int tamanhoNovoArray = this.array.length + arrayFromConcat.length;
        byte[] novoArray = new byte[tamanhoNovoArray];

        for (int i = 0; i < this.array.length; i++) {
            novoArray[i] = this.array[i];
        }

        for (int i = 0; i < arrayFromConcat.length; i++) {
            novoArray[this.array.length + i] = arrayFromConcat[i];
        }

        this.array = novoArray;
        return this;
    }

    public byte[] getByteArray() {
        return this.array;
    }
}
