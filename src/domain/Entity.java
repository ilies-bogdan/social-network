package domain;

public interface Entity<ID> {
    ID getID();
    void setID(ID id);
}
