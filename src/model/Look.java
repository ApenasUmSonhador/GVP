package model;

import java.time.LocalDate;

public class Look {
    private int id;
    private int userId;
    private String nome;
    private LocalDate dataCriacao;
    private int upperbodyId;
    private int lowerbodyId;
    private int footwearId;
    private Integer underwearId;
    private Integer accessoryId;

    public Look(int id, int userId, String nome, LocalDate dataCriacao,
            int upperbodyId, int lowerbodyId, int footwearId,
            Integer underwearId, Integer accessoryId) {
        this.id = id;
        this.userId = userId;
        this.nome = nome;
        this.dataCriacao = dataCriacao;
        this.upperbodyId = upperbodyId;
        this.lowerbodyId = lowerbodyId;
        this.footwearId = footwearId;
        this.underwearId = underwearId;
        this.accessoryId = accessoryId;
    }

    // Getters e Setters...
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getUpperbodyId() {
        return upperbodyId;
    }

    public void setUpperbodyId(int upperbodyId) {
        this.upperbodyId = upperbodyId;
    }

    public int getLowerbodyId() {
        return lowerbodyId;
    }

    public void setLowerbodyId(int lowerbodyId) {
        this.lowerbodyId = lowerbodyId;
    }

    public int getFootwearId() {
        return footwearId;
    }

    public void setFootwearId(int footwearId) {
        this.footwearId = footwearId;
    }

    public Integer getUnderwearId() {
        return underwearId;
    }

    public void setUnderwearId(Integer underwearId) {
        this.underwearId = underwearId;
    }

    public Integer getAccessoryId() {
        return accessoryId;
    }

    public void setAccessoryId(Integer accessoryId) {
        this.accessoryId = accessoryId;
    }

}
