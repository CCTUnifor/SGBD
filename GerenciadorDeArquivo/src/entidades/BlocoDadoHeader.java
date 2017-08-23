package entidades;

import interfaces.IBinary;
import services.IntExtensionMethod;

public class BlocoDadoHeader implements IBinary<BlocoDadoHeader> {

    private int containerId = 1000232;
    private int blocoId;
    private TipoBloco tipoBloco;
    private int tamanhoUsado;

    public BlocoDadoHeader() {
        this.tipoBloco = TipoBloco.DADOS;
    }

    @Override
    public byte[] toByteArray() {
        byte[] blocoDadoHeader = new byte[11];
        byte[] containerIdBytes = IntExtensionMethod.toBytes(this.containerId);

        return blocoDadoHeader;
    }

    @Override
    public BlocoDadoHeader fromByteArray(byte[] byteArray) {
        byte[] containerIdBytes = IntExtensionMethod.subArray(byteArray, 0, 1);
        byte[] blocoIdBytes = IntExtensionMethod.subArray(byteArray, 1, 3);
        byte[] tipoBlocoBytes = IntExtensionMethod.subArray(byteArray, 4, 1);
        byte[] tamanhoUsadoBytes = IntExtensionMethod.subArray(byteArray, 5, 3);

        int _containerId = IntExtensionMethod.byteArrayToInt(containerIdBytes);
        int _blocoId = IntExtensionMethod.byteArrayToInt(blocoIdBytes);
//        TipoBloco _tipoBloco = IntExtensionMethod.byteArrayToInt(tipoBlocoBytes);
        int _tamanhoUsado = IntExtensionMethod.byteArrayToInt(tamanhoUsadoBytes);

        BlocoDadoHeader blocoDadoHeader = new BlocoDadoHeader();
        blocoDadoHeader.containerId = _containerId;
        blocoDadoHeader.blocoId = _blocoId;
//        blocoDadoHeader.tipoBloco = _tipoBloco;
        blocoDadoHeader.tamanhoUsado = _tamanhoUsado;

        return blocoDadoHeader;
    }
}
