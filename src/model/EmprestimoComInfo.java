package model;

import java.time.LocalDate;

public class EmprestimoComInfo {
    private Item item;
    private String nomeDe;
    private String nomePara;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao;

    public EmprestimoComInfo(Item item, String nomeDe, String nomePara, LocalDate dataEmprestimo,
            LocalDate dataDevolucao) {
        this.item = item;
        this.nomeDe = nomeDe;
        this.nomePara = nomePara;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
    }

    public Item getItem() {
        return item;
    }

    public String getNomeDe() {
        return nomeDe;
    }

    public String getNomePara() {
        return nomePara;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }
}
