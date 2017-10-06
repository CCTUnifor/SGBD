package entidades.blocos;

import interfaces.IPrint;

public class RowId implements IPrint{
    private int containerId;
    private int blocoId;

    public RowId(int _containerId, int _blocoId){
        this.containerId = _containerId;
        this.blocoId = _blocoId;
    }

    public int getBlocoId() {
        return blocoId;
    }

    public int getContainerId() {
        return containerId;
    }

    @Override
    public String toString() {
        return this.print();
    }

    @Override
    public String print() {
        return this.containerId + "." + this.blocoId + "\n";
    }
}
