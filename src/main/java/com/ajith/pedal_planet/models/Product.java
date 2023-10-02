package com.ajith.pedal_planet.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

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

	public boolean getIsAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}



}