package model;

public class Unit {
    private int unitId;
    private String unitName;

    public Unit() {
    }

    public Unit(String unitName) {
        this.unitName = unitName;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public String toString() {
        return this.unitName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit)) return false;

        Unit unit = (Unit) o;

        if (unitId != unit.unitId) return false;
        return unitName.equals(unit.unitName);
    }

    @Override
    public int hashCode() {
        int result = unitId;
        result = 31 * result + unitName.hashCode();
        return result;
    }
}
