package com.tradr.springboot.view.searchclasses;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResultObject {

	private String postcode;
	private int quality;
	private int eastings;
	private int northings;
	private String country;
	private String nhs_ha;
	private double longitude;
	private double latitude;
	private String european_electoral_region;
	private String primary_care_trust;
	private String region;
	private String lsoa;
	private String msoa;
	private String incode;
	private String outcode;
	private String parliamentary_constituency;
	private String admin_district;
	private String parish;
	private String admin_county;
	private String date_of_introduction;
	private String admin_ward;
	private String ced;
	private String ccg;
	private String nuts;
	private String pfa;
	private CodesObject codes;
	private double distance;
}
