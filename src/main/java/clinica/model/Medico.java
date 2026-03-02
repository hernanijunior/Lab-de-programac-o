package clinica.model;

public class Medico {
    private int codm;
    private String nome;
    private int idade;
    private String especialidade;
    private Long rg;
    private String cidade;
    private Integer nroa;

    public Medico() {}
    public Medico(int codm, String nome, int idade, String especialidade, Long rg, String cidade, Integer nroa) {
        this.codm = codm; this.nome = nome; this.idade = idade;
        this.especialidade = especialidade; this.rg = rg; this.cidade = cidade; this.nroa = nroa;
    }

    public int getCodm()              { return codm; }
    public void setCodm(int v)        { codm = v; }
    public String getNome()           { return nome; }
    public void setNome(String v)     { nome = v; }
    public int getIdade()             { return idade; }
    public void setIdade(int v)       { idade = v; }
    public String getEspecialidade()  { return especialidade; }
    public void setEspecialidade(String v){ especialidade = v; }
    public Long getRg()               { return rg; }
    public void setRg(Long v)         { rg = v; }
    public String getCidade()         { return cidade; }
    public void setCidade(String v)   { cidade = v; }
    public Integer getNroa()          { return nroa; }
    public void setNroa(Integer v)    { nroa = v; }

    @Override public String toString() { return codm + " - " + nome; }
}
