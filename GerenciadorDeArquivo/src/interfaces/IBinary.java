package interfaces;

public interface IBinary<T> {
    public byte[] toByteArray();
    public T fromByteArray(byte[] byteArray);
}
