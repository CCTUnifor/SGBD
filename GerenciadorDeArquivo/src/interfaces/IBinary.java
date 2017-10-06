package interfaces;

public interface IBinary {
    public byte[] toByteArray();
    public <T> T fromByteArray(byte[] byteArray);

}
