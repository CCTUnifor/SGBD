package entidades.blocos;

import interfaces.IPrint;

import java.util.ArrayList;

public class RowId implements IPrint {
    private int containerId;
    private int blocoId;

    private RowId() {
    }

    public RowId(int _containerId, int _blocoId) {
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
        return this.print().get(0);
    }

    @Override
    public ArrayList<String> print() {
        ArrayList<String> parse = new ArrayList<String>();
        parse.add(this.containerId + "." + this.blocoId + "\n");

        return parse;
    }

    @Override
    public boolean equals(Object rowId) {
        return rowId instanceof RowId && this.getContainerId() == ((RowId) rowId).getContainerId() && this.getBlocoId() == ((RowId) rowId).getBlocoId();
    }

    public static RowId create(int containerId, int blocoId) {
        return new RowId(containerId, blocoId);
    }
}
