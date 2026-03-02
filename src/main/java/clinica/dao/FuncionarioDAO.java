package clinica.dao;

import clinica.database.Database;
import clinica.model.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    public List<Funcionario> listarTodos() {
        List<Funcionario> lista = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM Funcionarios ORDER BY codf")) {
            while (rs.next())
                lista.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public Funcionario buscarPorCodigo(int codf) {
        try (PreparedStatement ps = Database.getConnection()
                .prepareStatement("SELECT * FROM Funcionarios WHERE codf=?")) {
            ps.setInt(1, codf);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return map(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void inserir(Funcionario f) {
        String sql = "INSERT INTO Funcionarios (codf,nome,idade,RG,salario,departamento,tempoServico) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            setAll(ps, f);
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Funcionario f) {
        String sql = "UPDATE Funcionarios SET nome=?,idade=?,RG=?,salario=?,departamento=?,tempoServico=? WHERE codf=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setInt(2, f.getIdade());
            if (f.getRg() != null)
                ps.setLong(3, f.getRg());
            else
                ps.setNull(3, Types.NUMERIC);
            if (f.getSalario() != null)
                ps.setDouble(4, f.getSalario());
            else
                ps.setNull(4, Types.NUMERIC);
            ps.setString(5, f.getDepartamento());
            if (f.getTempoServico() != null)
                ps.setInt(6, f.getTempoServico());
            else
                ps.setNull(6, Types.TINYINT);
            ps.setInt(7, f.getCodf());
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public void remover(int codf) {
        try (PreparedStatement ps = Database.getConnection()
                .prepareStatement("DELETE FROM Funcionarios WHERE codf=?")) {
            ps.setInt(1, codf);
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public List<String> listarDepartamentos() {
        List<String> deptos = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st
                        .executeQuery("SELECT DISTINCT departamento FROM Funcionarios ORDER BY departamento")) {
            while (rs.next())
                deptos.add(rs.getString(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return deptos;
    }

    // Relatório c: funcionários com salário > todos os salários de um departamento
    public List<String[]> funcionariosSalarioSuperior(String depto) {
        String sql = """
                SELECT nome, salario, departamento FROM Funcionarios
                WHERE salario > ALL (
                    SELECT salario FROM Funcionarios WHERE departamento = ?
                )
                ORDER BY nome
                """;
        List<String[]> res = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, depto);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                res.add(new String[] { rs.getString("nome"), String.format("R$ %.2f", rs.getDouble("salario")),
                        rs.getString("departamento") });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    // Relatório d: salários de todos ou de um departamento
    public List<String[]> salariosPorDepartamento(String depto) {
        String sql = depto == null
                ? "SELECT nome, salario, departamento FROM Funcionarios ORDER BY departamento, nome"
                : "SELECT nome, salario, departamento FROM Funcionarios WHERE departamento=? ORDER BY nome";
        List<String[]> res = new ArrayList<>();
        try {
            Statement st = null;
            PreparedStatement ps = null;
            ResultSet rs;
            if (depto == null) {
                st = Database.getConnection().createStatement();
                rs = st.executeQuery(sql);
            } else {
                ps = Database.getConnection().prepareStatement(sql);
                ps.setString(1, depto);
                rs = ps.executeQuery();
            }
            while (rs.next())
                res.add(new String[] { rs.getString("nome"), String.format("R$ %.2f", rs.getDouble("salario")),
                        rs.getString("departamento") });
            if (st != null)
                st.close();
            if (ps != null)
                ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    private void setAll(PreparedStatement ps, Funcionario f) throws SQLException {
        ps.setInt(1, f.getCodf());
        ps.setString(2, f.getNome());
        ps.setInt(3, f.getIdade());
        if (f.getRg() != null)
            ps.setLong(4, f.getRg());
        else
            ps.setNull(4, Types.NUMERIC);
        if (f.getSalario() != null)
            ps.setDouble(5, f.getSalario());
        else
            ps.setNull(5, Types.NUMERIC);
        ps.setString(6, f.getDepartamento());
        if (f.getTempoServico() != null)
            ps.setInt(7, f.getTempoServico());
        else
            ps.setNull(7, Types.TINYINT);
    }

    private Funcionario map(ResultSet rs) throws SQLException {
        Object rg = rs.getObject("RG");
        Object sal = rs.getObject("salario");
        Object ts = rs.getObject("tempoServico");
        return new Funcionario(
                rs.getInt("codf"), rs.getString("nome"), rs.getInt("idade"),
                rg != null ? ((Number) rg).longValue() : null,
                sal != null ? ((Number) sal).doubleValue() : null,
                rs.getString("departamento"),
                ts != null ? ((Number) ts).intValue() : null);
    }

    public int obterProximoCodigo() {
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(codf), 0) + 1 FROM Funcionarios")) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}
