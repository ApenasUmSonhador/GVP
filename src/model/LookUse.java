package model;

import java.time.LocalDate;

public class LookUse {
    private int id;
    private int lookId;
    private LocalDate data;
    private String turno;
    private String contexto;

    public LookUse(int id, int lookId, LocalDate data, String turno, String contexto) {
        this.id = id;
        this.lookId = lookId;
        this.data = data;
        this.turno = turno;
        this.contexto = contexto;
    }

    public int getId() {
        return id;
    }

    public int getLookId() {
        return lookId;
    }

    public LocalDate getData() {
        return data;
    }

    public String getTurno() {
        return turno;
    }

    public String getContexto() {
        return contexto;
    }

    @Override
    public String toString() {
        return getData().toString() + " (" + getTurno() + "): " + getContexto();
    }
}