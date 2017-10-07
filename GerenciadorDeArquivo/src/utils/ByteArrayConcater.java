package utils;

public class ByteArrayConcater {

    private byte[] array;
    private byte[] finalArray;
    private int tamanhoArray;

    public ByteArrayConcater() {
        this.finalArray = new byte[0];
    }

    public ByteArrayConcater(int tamanhoArray) {
        this.tamanhoArray = tamanhoArray;
        this.finalArray = new byte[tamanhoArray];
    }

    public ByteArrayConcater concat(byte[] arrayFromConcat) {
        if (this.array == null){
            this.array = arrayFromConcat;
            return this;
        }

        byte[] aux  = new byte[this.finalArray.length + arrayFromConcat.length];
        System.arraycopy(this.finalArray, 0, aux, 0, this.finalArray.length);
        System.arraycopy(arrayFromConcat, 0, aux, this.finalArray.length, arrayFromConcat.length);

        this.finalArray = aux;
        return this;
    }

    public ByteArrayConcater concat(byte byteToConcat) {
        if (this.array == null){
            this.array = new byte[1];
            this.array[0] = byteToConcat;
            return this;
        }

        if (this.tamanhoArray > 0 && this.array.length + 1 > this.tamanhoArray)
            throw new Error("Ultrapassou o array.");

        int tamanhoNovoArray = this.array.length + 1;
        byte[] novoArray = new byte[tamanhoNovoArray];

        for (int i = 0; i < this.array.length; i++) {
            novoArray[i] = this.array[i];
        }
        novoArray[this.array.length] = byteToConcat;

        this.array = novoArray;
        return this;
    }

        public byte[] getByteArray() {
        return this.array;
    }

    public byte[] getFinalByteArray() {
        if (this.finalArray == null)
            return this.array;

        return this.finalArray;
    }
}