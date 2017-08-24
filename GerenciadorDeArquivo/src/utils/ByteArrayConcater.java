package utils;

public class ByteArrayConcater {

    private byte[] array;
    private int tamanhoArray;

    public ByteArrayConcater() { }

    public ByteArrayConcater(int tamanhoArray) {
        this.tamanhoArray = tamanhoArray;
        this.array = new byte[tamanhoArray];
    }

    public ByteArrayConcater concat(byte[] arrayFromConcat) {
        if (this.array == null){
            this.array = arrayFromConcat;
            return this;
        }

        int tamanhoNovoArray = this.array.length + arrayFromConcat.length;
        byte[] novoArray = new byte[tamanhoNovoArray];

        for (int i = 0; i < this.array.length; i++) {
            novoArray[i] = this.array[i];
        }

        for (int i = 0; i < arrayFromConcat.length; i++) {
            if (novoArray.length + 1 > i){
                novoArray[i + this.array.length + 1] = arrayFromConcat[i];
            }
        }

//        System.arraycopy(this.array, 0, novoArray, 0, this.array.length);
//        System.arraycopy(arrayFromConcat, 0, novoArray, this.array.length + 1, arrayFromConcat.length);

        this.array = novoArray;
        return this;
    }

    public byte[] getByteArray() {
        return this.array;
    }
}
