package com.dilapp.radar.skinproducts.database;

public class SkinProduct {
	private long id;
	private String name;
	private float price;
	private String description;
	private String homepage;
	private int type;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "SkinProduct [id=" + id + ", name=" + name + ", price=" + price
				+ ", description=" + description + ", homepage=" + homepage
				+ ", type=" + type + "]";
	}
}
