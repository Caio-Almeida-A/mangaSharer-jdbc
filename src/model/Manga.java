package model;

public class Manga {
    private int id;
    private String nome;
    private int idArtista;
    private Integer idModerador; //idAdmin_moderador

    public Manga(){}

    public Manga(int id, String nome, int idArtista, Integer idModerador) {
        this.id = id;
        this.nome = nome;
        this.idArtista = idArtista;
        this.idModerador = idModerador;
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
    public int getIdArtista(){
        return idArtista;
    }
    public void setIdArtista(int idArtista){
        this.idArtista = idArtista;
    }

    public Integer getIdModerador(){
        return idModerador;
    }

    public void setIdModerador(Integer idModerador){
        this.idModerador = idModerador;
    }
}
