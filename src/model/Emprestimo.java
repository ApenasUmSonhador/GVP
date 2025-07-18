package model;

import java.time.LocalDate;

public class Emprestimo {
    private int id;
    private int itemId;
    private int deUsuarioId;
    private int paraUsuarioId;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao; // pode ser null

    public Emprestimo(int id, int itemId, int deUsuarioId, int paraUsuarioId, LocalDate dataEmprestimo,
            LocalDate dataDevolucao) {
        this.id = id;
        this.itemId = itemId;
        this.deUsuarioId = deUsuarioId;
        this.paraUsuarioId = paraUsuarioId;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
    }

    public int getId() {
        return id;
    }

    public int getItemId() {
        return itemId;
    }

    public int getDeUsuarioId() {
        return deUsuarioId;
    }

    public int getParaUsuarioId() {
        return paraUsuarioId;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }
}
