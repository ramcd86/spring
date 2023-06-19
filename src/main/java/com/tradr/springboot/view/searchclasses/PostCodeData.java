package com.tradr.springboot.view.searchclasses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostCodeData {

	@JsonProperty("postcode")
	private String postcode;

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
}
