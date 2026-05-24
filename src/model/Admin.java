package model;

public class Admin extends Usuario{
    private Integer idSupervisor;
    public Admin() {} // Vazio

    public Admin(int id, String nome, String email, Integer idSupervisor) {
        super(id, nome, email); // Envia dados para Usuario.java
        this.idSupervisor = idSupervisor; // Atribui supervisor no Admin
    }
    public Integer getIdSupervisor(){
        return idSupervisor;
    }
    public void setIdSupervisor(Integer idSupervisor){
        this.idSupervisor = idSupervisor;
    }
}
