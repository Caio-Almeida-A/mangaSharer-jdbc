package model;

public class Pagina {
    // Pagina é algo que o usuário visualiza, tem interface
    private int id;
    private int idCapitulo;
    private int idManga;
    private String url;

    public Pagina() {}

    public Pagina(int id, int idCapitulo , int idManga, String url){
        this.id = id;
        this.idCapitulo = idCapitulo;
        this.idManga = idManga;
        this.url = url;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getIdCapitulo(){
        return idCapitulo;
    }
    public void setIdCapitulo(int idCapitulo){
        this.idCapitulo = idCapitulo;
    }
    public int getIdManga(){
        return idManga;
    }
    public void setIdManga(int idManga){
        this.idManga = idManga;
    }
    public String getUrl(){
        return url;
    }
    public void setUrl(String url){
        this.url = url;
    }
}
