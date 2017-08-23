package services;

import java.nio.ByteBuffer;

public class IntExtensionMethod {
    public static byte[] toBytes(int a){
        byte[] retorno = new byte[4];

        retorno[0] = (byte) (a >> 24); // pegou o bYte mais significativo
        retorno[1] = (byte) (a >> 16);
        retorno[2] = (byte) (a >> 8);
        retorno[3] = (byte) (a >> 0); // pegou o bYte menos significativo

        return retorno;
    }

    public static int byteArrayToInt(byte[] bytes){
        if(bytes.length > 4)
            throw new RuntimeException("O tamanho do byte array é inválido: "+bytes.length);

        // move os 24 bytes mais singificativos para a esquerda.
        //depois compara o signed byte com os bytes 255 para que remover o
        //sinal e depois move os bits 16 bits para esquerda
        if(bytes.length == 4){
            return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
        }else if(bytes.length == 3){
            return bytes[0] << 16 | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF);
        }else if(bytes.length == 2){
            return bytes[0] << 8 | (bytes[1] & 0xFF) ;
        }else{
            return bytes[0] & 0xFF;
        }
    }

    public static byte[] subArray(byte[] objarray,int startIndex, int size) {
        byte[] array = new byte[size];

        for(int i = 0, j = startIndex; i < size; j++, i++){
            array[i] = objarray[j];
        }

        return array;
    }

}
