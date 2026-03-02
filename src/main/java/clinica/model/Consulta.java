package clinica.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Consulta {
    private int codm;
    private int codp;
    private LocalDate dataConsulta;
    private LocalTime hora;
    // Campos extras para exibição
    private String nomeMedico;
    private String nomePaciente;

    public Consulta() {}
    public Consulta(int codm, int codp, LocalDate dataConsulta, LocalTime hora) {
        this.codm = codm; this.codp = codp;
        this.dataConsulta = dataConsulta; this.hora = hora;
    }

    public int getCodm()                  { return codm; }
    public void setCodm(int v)            { codm = v; }
    public int getCodp()                  { return codp; }
    public void setCodp(int v)            { codp = v; }
    public LocalDate getDataConsulta()    { return dataConsulta; }
    public void setDataConsulta(LocalDate v){ dataConsulta = v; }
    public LocalTime getHora()            { return hora; }
    public void setHora(LocalTime v)      { hora = v; }
    public String getNomeMedico()         { return nomeMedico; }
    public void setNomeMedico(String v)   { nomeMedico = v; }
    public String getNomePaciente()       { return nomePaciente; }
    public void setNomePaciente(String v) { nomePaciente = v; }
}
