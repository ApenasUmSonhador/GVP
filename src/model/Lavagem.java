package model;

import java.time.LocalDate;
import java.util.List;

public class Lavagem {
    private int id;
    private LocalDate data;
    private List<Integer> itemIds; // IDs dos itens lavados

    public Lavagem(int id, LocalDate data, List<Integer> itemIds) {
        this.id = id;
        this.data = data;
        this.itemIds = itemIds;
    }

    public int getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public List<Integer> getItemIds() {
        return itemIds;
    }
}
