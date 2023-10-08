package com.ajith.pedal_planet.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@Entity

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	@Lob
	private String shortDescription;

	@Lob
	private String longDescription;

	private float price;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;


	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<Image> images;


	@OneToMany(mappedBy = "product")
	@ToString.Exclude
	private List<Variant> variant;

	public boolean isAvailable;

	private int quantity;

	private boolean inWishList;

	 public boolean getInWishList() {
		 return inWishList;
	 }
	 public void setInWishList(boolean inWishList){
		 this.inWishList = inWishList;
	 }
	public boolean getIsAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}



}
