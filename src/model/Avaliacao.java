package model;

public class Avaliacao {
    //alterar nota tem interface
    private int idLeitor;
    private int idManga;
    private int pontuacao;

    public Avaliacao(){}

    public Avaliacao(int idLeitor, int idManga, int pontuacao) {
        this.idLeitor = idLeitor;
        this.idManga = idManga;
        this.pontuacao = pontuacao;
    }
    
    public int getIdLeitor(){
        return idLeitor;
    }
    public void setIdLeitor(int idLeitor){
        this.idLeitor = idLeitor;
    }
    public int getIdManga(){
        return idManga;
    }
    public void setIdManga(int idManga){
        this.idManga = idManga;
    }
    public int getPontuacao(){
        return pontuacao;
    }
    public void setPontuacao(int pontuacao){
        this.pontuacao = pontuacao;
    }
}
