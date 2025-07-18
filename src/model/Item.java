package model;

public abstract class Item {
    protected int id;
    protected String ownerId;
    protected ItemType type;
    protected String color;
    protected String size;
    protected String storeOfOrigin;
    protected String imagePath;
    protected ConservationLevel conservation;

    // Construtor
    public Item(int id, String ownerId, ItemType type, String color, String size,
            String storeOfOrigin, String imagePath, ConservationLevel conservation) {
        this.id = id;
        this.ownerId = ownerId;
        this.type = type;
        this.color = color;
        this.size = size;
        this.storeOfOrigin = storeOfOrigin;
        this.imagePath = imagePath;
        this.conservation = conservation;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStoreOfOrigin() {
        return storeOfOrigin;
    }

    public void setStoreOfOrigin(String storeOfOrigin) {
        this.storeOfOrigin = storeOfOrigin;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ConservationLevel getConservation() {
        return conservation;
    }

    public void setConservation(ConservationLevel conservation) {
        this.conservation = conservation;
    }

    @Override
    public String toString() {
        return getType() + " - " + getColor() + " - " + getSize();
    }

}
