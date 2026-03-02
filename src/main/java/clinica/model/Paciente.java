package clinica.model;

public class Paciente {
    private int codp;
    private String nome;
    private int idade;
    private String cidade;
    private Long rg;
    private String problema;

    public Paciente() {}
    public Paciente(int codp, String nome, int idade, String cidade, Long rg, String problema) {
        this.codp = codp; this.nome = nome; this.idade = idade;
        this.cidade = cidade; this.rg = rg; this.problema = problema;
    }

    public int getCodp()           { return codp; }
    public void setCodp(int v)     { codp = v; }
    public String getNome()        { return nome; }
    public void setNome(String v)  { nome = v; }
    public int getIdade()          { return idade; }
    public void setIdade(int v)    { idade = v; }
    public String getCidade()      { return cidade; }
    public void setCidade(String v){ cidade = v; }
    public Long getRg()            { return rg; }
    public void setRg(Long v)      { rg = v; }
    public String getProblema()    { return problema; }
    public void setProblema(String v){ problema = v; }

    @Override public String toString() { return codp + " - " + nome; }
}
