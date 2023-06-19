package com.tradr.springboot.view.searchclasses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CodesObject {

	private String admin_district;
	private String admin_county;
	private String admin_ward;
	private String parish;
	private String parliamentary_constituency;
	private String ccg;
	private String ccg_id;
	private String ced;
	private String nuts;
	private String lsoa;
	private String msoa;
	private String lau2;
	private String pfa;
}
