package model;

public class Comentario {
    private int id;
    private int idLeitor;
    private int idManga;
    private String texto; // Campo 'comentario' no SQL
    private Integer idModerador; // Pode ser null, por isso usamos Integer

    public Comentario() {}

    public Comentario(int id, int idLeitor, int idManga, String texto, Integer idModerador) {
        this.id = id;
        this.idLeitor = idLeitor;
        this.idManga = idManga;
        this.texto = texto;
        this.idModerador = idModerador;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getIdLeitor() {
        return idLeitor;
    }
    public void setIdLeitor(int idLeitor) {
        this.idLeitor = idLeitor;
    }

    public int getIdManga() {
        return idManga;
    }
    public void setIdManga(int idManga) {
        this.idManga = idManga;
    }

    public String getTexto() {
        return texto;
    }
    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Integer getIdModerador() {
        return idModerador;
    }
    public void setIdModerador(Integer idModerador) {
        this.idModerador = idModerador;
    }
}
