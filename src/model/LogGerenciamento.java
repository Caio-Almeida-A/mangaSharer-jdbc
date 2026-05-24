package model;
import java.util.Date;

public class LogGerenciamento {
    private int idAdmin;
    private int idPadrao;
    private Date dataAcao;
    public LogGerenciamento() {} // Vazio

    public LogGerenciamento(int idAdmin, int idPadrao, Date dataAcao) {
        this.idAdmin = idAdmin;
        this.idPadrao = idPadrao;
        this.dataAcao = dataAcao;
    }

    public int getIdAdmin(){
        return idAdmin;
    }
    public void setIdAdmin(int idAdmin){
        this.idAdmin = idAdmin;
    }
    public int getIdPadrao(){
        return idPadrao;
    }
    public void setIdPadrao(int idPadrao){
        this.idPadrao = idPadrao;
    }
    public Date getDataAcao(){
        return dataAcao;
    }
    public void setDataAcao(Date dataAcao){
        this.dataAcao = dataAcao;
    }
}