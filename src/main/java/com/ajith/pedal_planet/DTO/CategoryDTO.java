package com.ajith.pedal_planet.DTO;

import lombok.Data;

@Data
public class CategoryDTO {
	 private int id;
	    private String name;
	    private String description;
	    private boolean isAvailable;
	    
	    public boolean getIsAvailable() {
			return isAvailable;
		}

		public void setAvailable(boolean isAvailable) {
			this.isAvailable = isAvailable;
		}
}
