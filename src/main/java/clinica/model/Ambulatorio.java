package clinica.model;

public class Ambulatorio {
    private int nroa;
    private int andar;
    private Integer capacidade;

    public Ambulatorio() {}
    public Ambulatorio(int nroa, int andar, Integer capacidade) {
        this.nroa = nroa; this.andar = andar; this.capacidade = capacidade;
    }

    public int getNroa()              { return nroa; }
    public void setNroa(int v)        { nroa = v; }
    public int getAndar()             { return andar; }
    public void setAndar(int v)       { andar = v; }
    public Integer getCapacidade()    { return capacidade; }
    public void setCapacidade(Integer v){ capacidade = v; }

    @Override public String toString() { return "Amb. " + nroa + " | Andar " + andar + " | Cap. " + capacidade; }
}
