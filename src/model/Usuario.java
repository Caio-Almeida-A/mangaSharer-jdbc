package model;
import java.util.List;      
import java.util.ArrayList;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private List<String> telefones = new ArrayList<>();

    public Usuario() {}

    public Usuario(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }
    
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getNome(){
        return nome;
    }
    public void setNome(String nome){
        this.nome = nome;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    //Parte de telefones
    public List<String> getTelefones(){
        return telefones;
    }
    public void setTelefones(List<String> telefones){
        this.telefones = telefones;
    }

    public void addTelefone(String telefone){
        this.telefones.add(telefone);
    }

}
