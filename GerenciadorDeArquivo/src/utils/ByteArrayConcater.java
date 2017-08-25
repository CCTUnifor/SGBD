package utils;

public class ByteArrayConcater {

    private byte[] array;
    private byte[] finalArray;
    private int tamanhoArray;

    public ByteArrayConcater() { }

    public ByteArrayConcater(int tamanhoArray) {
        this.tamanhoArray = tamanhoArray;
        this.finalArray = new byte[tamanhoArray];
    }

    public ByteArrayConcater concat(byte[] arrayFromConcat) {
        if (this.array == null){
            this.array = arrayFromConcat;
            return this;
        }

        if (this.tamanhoArray > 0 && this.array.length + arrayFromConcat.length > this.tamanhoArray)
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

    public byte[] getFinalByteArray() {
        if (this.finalArray == null)
            return this.array;

        System.arraycopy(this.array, 0, this.finalArray, 0, this.array.length);
        return this.finalArray;
    }
}
