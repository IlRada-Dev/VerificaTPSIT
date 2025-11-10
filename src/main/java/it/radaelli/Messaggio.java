package it.radaelli;

public class Messaggio {
    public String testo;
    static public int idnum = 1;
    public int id;
    public String autore;

    public Messaggio(String testo, String autore) {
        this.testo = testo;
        this.autore = autore;
        this.id = idnum++;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public int getId() {
        return id;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

}
