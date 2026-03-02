package clinica.model;

public class Funcionario {
    private int codf;
    private String nome;
    private int idade;
    private Long rg;
    private Double salario;
    private String departamento;
    private Integer tempoServico;

    public Funcionario() {}
    public Funcionario(int codf, String nome, int idade, Long rg, Double salario, String departamento, Integer tempoServico) {
        this.codf = codf; this.nome = nome; this.idade = idade; this.rg = rg;
        this.salario = salario; this.departamento = departamento; this.tempoServico = tempoServico;
    }

    public int getCodf()               { return codf; }
    public void setCodf(int v)         { codf = v; }
    public String getNome()            { return nome; }
    public void setNome(String v)      { nome = v; }
    public int getIdade()              { return idade; }
    public void setIdade(int v)        { idade = v; }
    public Long getRg()                { return rg; }
    public void setRg(Long v)          { rg = v; }
    public Double getSalario()         { return salario; }
    public void setSalario(Double v)   { salario = v; }
    public String getDepartamento()    { return departamento; }
    public void setDepartamento(String v){ departamento = v; }
    public Integer getTempoServico()   { return tempoServico; }
    public void setTempoServico(Integer v){ tempoServico = v; }

    @Override public String toString() { return codf + " - " + nome; }
}
