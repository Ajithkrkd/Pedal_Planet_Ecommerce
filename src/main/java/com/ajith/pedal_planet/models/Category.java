package com.ajith.pedal_planet.models;

import javax.persistence.*;

import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "Category")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name ="Category_id" , nullable = false)
	private int id;
	
	private String name;

	@OneToMany(mappedBy = "category")
	@ToString.Exclude
	private List<Product> product;
	  
	private String description;
	
	private boolean isAvailable;
		
		
		public boolean getIsAvailable() {
			return isAvailable;
		}
		public void setAvailable(boolean isAvailable) {
			this.isAvailable = isAvailable;
		}

	
     
}
