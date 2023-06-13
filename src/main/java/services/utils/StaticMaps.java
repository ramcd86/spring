package services.utils;

import java.util.HashMap;

public class StaticMaps {

  public static HashMap<String, String> countryMap;
  public static HashMap<String, String> searchType;
  public static HashMap<String, String> storeThemes;
  public static HashMap<String, String> storeTags;
  public static HashMap<String, RegistrationFailureEnums> registrationFailures;

  static {
    registrationFailures = new HashMap<String, RegistrationFailureEnums>();
    registrationFailures.put(
      "userName",
      RegistrationFailureEnums.USERNAME_EMPTY
    );
    registrationFailures.put(
      "firstName",
      RegistrationFailureEnums.FIRSTNAME_EMPTY
    );
    registrationFailures.put(
      "lastName",
      RegistrationFailureEnums.LASTNAME_EMPTY
    );
    registrationFailures.put("email", RegistrationFailureEnums.EMAIL_EMPTY);
    registrationFailures.put(
      "password",
      RegistrationFailureEnums.PASSWORD_EMPTY
    );
    registrationFailures.put("dob", RegistrationFailureEnums.DOB_EMPTY);

    storeTags = new HashMap<String, String>();
    storeTags.put("METAL_WORKING", "metal_working");
    storeTags.put("WOOD_WORKING", "wood_working");
    storeTags.put("PAPER_WORKING", "paper_working");
    storeTags.put("JEWELRY", "jewelry");
    storeTags.put("STONE_WORKING", "stone_working");
    storeTags.put("FOODSTUFFS", "foodstuffs");
    storeTags.put("KNITTING", "knitting");
    storeTags.put("NEEDLE_WORKING", "needle_working");
    storeTags.put("SEWING", "sewing");
    storeTags.put("TAILORING", "tailoring");
    storeTags.put("PAINTING", "painting");
    storeTags.put("DRAWING", "drawing");
    storeTags.put("SCULPTING", "sculpting");
    storeTags.put("CLAY_WORKING", "clay_working");
    storeTags.put("FLORAL_WORKING", "floral_working");
    storeTags.put("GARDENING", "gardening");
    storeTags.put("LIGHT_WORKING", "light_working");
    storeTags.put("LEATHER_WORKING", "leather_working");
    storeTags.put("HERBAL_WORKING", "herbal_working");
    storeTags.put("TRADITIONAL_SUPPLEMENTS", "traditional_supplements");
    storeTags.put("JOINERY", "joinery");
    storeTags.put("INLAY", "inlay");
    storeTags.put("MARKETRY", "marketry");
    storeTags.put("POTTERY", "pottery");
    storeTags.put("WEAVING", "weaving");
    storeTags.put("CROCHET", "crochet");
    storeTags.put("EMBROIDERY", "embroidery");
    storeTags.put("WOOD_CARVING", "wood_carving");
    storeTags.put("CALLIGRAPHY", "calligraphy");
    storeTags.put("BOOKBINDING", "bookbinding");
    storeTags.put("QUILTING", "quilting");
    storeTags.put("ORIGAMI", "origami");
    storeTags.put("DOLL_MAKING", "doll_making");
    storeTags.put("BEAD_WORK", "bead_work");
    storeTags.put("MACRAME", "macrame");
    storeTags.put("CANDLE_MAKING", "candle_making");
    storeTags.put("FELTING", "felting");
    storeTags.put("TABLETOP", "tabletop");
    storeTags.put("RELIGIOUS", "religious");
    storeTags.put("SEASONAL", "seasonal");

    storeThemes = new HashMap<String, String>();
    storeThemes.put("DEFAULT", "default"); // White
    storeThemes.put("DARK", "dark"); // Dark #37393A
    storeThemes.put("SANDALWOOD", "sandalwood"); // Sandy brown #DBA159
    storeThemes.put("EARTH", "earth"); // Darker browns #322214
    storeThemes.put("MEADOW", "meadow"); // Light green #8DE969
    storeThemes.put("FOREST", "forest"); // Dark Green #1C5253
    storeThemes.put("SKY", "sky"); // Light blue #3581B8
    storeThemes.put("OCEAN", "ocean"); // Dark blue #0D2149
    storeThemes.put("CUPCAKE", "cupcake"); // Light red/Pink #E0607E
    storeThemes.put("SCARLET", "scarlet"); // Dark reds #322214

    searchType = new HashMap<String, String>();
    searchType.put("STRING", "store_search_string");
    searchType.put("LOCATION", "store_tags");
    searchType.put("TAGS", "store_location");

    countryMap = new HashMap<String, String>();
    countryMap.put("AFG", "Afghanistan");
    countryMap.put("ALB", "Albania");
    countryMap.put("DZA", "Algeria");
    countryMap.put("ASM", "American Samoa");
    countryMap.put("AND", "Andorra");
    countryMap.put("AGO", "Angola");
    countryMap.put("AIA", "Anguilla");
    countryMap.put("ATA", "Antarctica");
    countryMap.put("ATG", "Antigua and Barbuda");
    countryMap.put("ARG", "Argentina");
    countryMap.put("ARM", "Armenia");
    countryMap.put("ABW", "Aruba");
    countryMap.put("AUS", "Australia");
    countryMap.put("AUT", "Austria");
    countryMap.put("AZE", "Azerbaijan");
    countryMap.put("BHS", "Bahamas (the)");
    countryMap.put("BHR", "Bahrain");
    countryMap.put("BGD", "Bangladesh");
    countryMap.put("BRB", "Barbados");
    countryMap.put("BLR", "Belarus");
    countryMap.put("BEL", "Belgium");
    countryMap.put("BLZ", "Belize");
    countryMap.put("BEN", "Benin");
    countryMap.put("BMU", "Bermuda");
    countryMap.put("BTN", "Bhutan");
    countryMap.put("BOL", "Bolivia (Plurinational State of)");
    countryMap.put("BES", "Bonaire, Sint Eustatius and Saba");
    countryMap.put("BIH", "Bosnia and Herzegovina");
    countryMap.put("BWA", "Botswana");
    countryMap.put("BVT", "Bouvet Island");
    countryMap.put("BRA", "Brazil");
    countryMap.put("IOT", "British Indian Ocean Territory (the)");
    countryMap.put("BRN", "Brunei Darussalam");
    countryMap.put("BGR", "Bulgaria");
    countryMap.put("BFA", "Burkina Faso");
    countryMap.put("BDI", "Burundi");
    countryMap.put("CPV", "Cabo Verde");
    countryMap.put("KHM", "Cambodia");
    countryMap.put("CMR", "Cameroon");
    countryMap.put("CAN", "Canada");
    countryMap.put("CYM", "Cayman Islands (the)");
    countryMap.put("CAF", "Central African Republic (the)");
    countryMap.put("TCD", "Chad");
    countryMap.put("CHL", "Chile");
    countryMap.put("CHN", "China");
    countryMap.put("CXR", "Christmas Island");
    countryMap.put("CCK", "Cocos (Keeling) Islands (the)");
    countryMap.put("COL", "Colombia");
    countryMap.put("COM", "Comoros (the)");
    countryMap.put("COD", "Congo (the Democratic Republic of the)");
    countryMap.put("COG", "Congo (the)");
    countryMap.put("COK", "Cook Islands (the)");
    countryMap.put("CRI", "Costa Rica");
    countryMap.put("HRV", "Croatia");
    countryMap.put("CUB", "Cuba");
    countryMap.put("CUW", "Curaçao");
    countryMap.put("CYP", "Cyprus");
    countryMap.put("CZE", "Czechia");
    countryMap.put("CIV", "Côte d'Ivoire");
    countryMap.put("DNK", "Denmark");
    countryMap.put("DJI", "Djibouti");
    countryMap.put("DMA", "Dominica");
    countryMap.put("DOM", "Dominican Republic (the)");
    countryMap.put("ECU", "Ecuador");
    countryMap.put("EGY", "Egypt");
    countryMap.put("SLV", "El Salvador");
    countryMap.put("GNQ", "Equatorial Guinea");
    countryMap.put("ERI", "Eritrea");
    countryMap.put("EST", "Estonia");
    countryMap.put("SWZ", "Eswatini");
    countryMap.put("ETH", "Ethiopia");
    countryMap.put("FLK", "Falkland Islands (the) [Malvinas]");
    countryMap.put("FRO", "Faroe Islands (the)");
    countryMap.put("FJI", "Fiji");
    countryMap.put("FIN", "Finland");
    countryMap.put("FRA", "France");
    countryMap.put("GUF", "French Guiana");
    countryMap.put("PYF", "French Polynesia");
    countryMap.put("ATF", "French Southern Territories (the)");
    countryMap.put("GAB", "Gabon");
    countryMap.put("GMB", "Gambia (the)");
    countryMap.put("GEO", "Georgia");
    countryMap.put("DEU", "Germany");
    countryMap.put("GHA", "Ghana");
    countryMap.put("GIB", "Gibraltar");
    countryMap.put("GRC", "Greece");
    countryMap.put("GRL", "Greenland");
    countryMap.put("GRD", "Grenada");
    countryMap.put("GLP", "Guadeloupe");
    countryMap.put("GUM", "Guam");
    countryMap.put("GTM", "Guatemala");
    countryMap.put("GGY", "Guernsey");
    countryMap.put("GIN", "Guinea");
    countryMap.put("GNB", "Guinea-Bissau");
    countryMap.put("GUY", "Guyana");
    countryMap.put("HTI", "Haiti");
    countryMap.put("HMD", "Heard Island and McDonald Islands");
    countryMap.put("VAT", "Holy See (the)");
    countryMap.put("HND", "Honduras");
    countryMap.put("HKG", "Hong Kong");
    countryMap.put("HUN", "Hungary");
    countryMap.put("ISL", "Iceland");
    countryMap.put("IND", "India");
    countryMap.put("IDN", "Indonesia");
    countryMap.put("IRN", "Iran (Islamic Republic of)");
    countryMap.put("IRQ", "Iraq");
    countryMap.put("IRL", "Ireland");
    countryMap.put("IMN", "Isle of Man");
    countryMap.put("ISR", "Israel");
    countryMap.put("ITA", "Italy");
    countryMap.put("JAM", "Jamaica");
    countryMap.put("JPN", "Japan");
    countryMap.put("JEY", "Jersey");
    countryMap.put("JOR", "Jordan");
    countryMap.put("KAZ", "Kazakhstan");
    countryMap.put("KEN", "Kenya");
    countryMap.put("KIR", "Kiribati");
    countryMap.put("PRK", "Korea (the Democratic People's Republic of)");
    countryMap.put("KOR", "Korea (the Republic of)");
    countryMap.put("KWT", "Kuwait");
    countryMap.put("KGZ", "Kyrgyzstan");
    countryMap.put("LAO", "Lao People's Democratic Republic (the)");
    countryMap.put("LVA", "Latvia");
    countryMap.put("LBN", "Lebanon");
    countryMap.put("LSO", "Lesotho");
    countryMap.put("LBR", "Liberia");
    countryMap.put("LBY", "Libya");
    countryMap.put("LIE", "Liechtenstein");
    countryMap.put("LTU", "Lithuania");
    countryMap.put("LUX", "Luxembourg");
    countryMap.put("MAC", "Macao");
    countryMap.put("MDG", "Madagascar");
    countryMap.put("MWI", "Malawi");
    countryMap.put("MYS", "Malaysia");
    countryMap.put("MDV", "Maldives");
    countryMap.put("MLI", "Mali");
    countryMap.put("MLT", "Malta");
    countryMap.put("MHL", "Marshall Islands (the)");
    countryMap.put("MTQ", "Martinique");
    countryMap.put("MRT", "Mauritania");
    countryMap.put("MUS", "Mauritius");
    countryMap.put("MYT", "Mayotte");
    countryMap.put("MEX", "Mexico");
    countryMap.put("FSM", "Micronesia (Federated States of)");
    countryMap.put("MDA", "Moldova (the Republic of)");
    countryMap.put("MCO", "Monaco");
    countryMap.put("MNG", "Mongolia");
    countryMap.put("MNE", "Montenegro");
    countryMap.put("MSR", "Montserrat");
    countryMap.put("MAR", "Morocco");
    countryMap.put("MOZ", "Mozambique");
    countryMap.put("MMR", "Myanmar");
    countryMap.put("NAM", "Namibia");
    countryMap.put("NRU", "Nauru");
    countryMap.put("NPL", "Nepal");
    countryMap.put("NLD", "Netherlands (the)");
    countryMap.put("NCL", "New Caledonia");
    countryMap.put("NZL", "New Zealand");
    countryMap.put("NIC", "Nicaragua");
    countryMap.put("NER", "Niger (the)");
    countryMap.put("NGA", "Nigeria");
    countryMap.put("NIU", "Niue");
    countryMap.put("NFK", "Norfolk Island");
    countryMap.put("MNP", "Northern Mariana Islands (the)");
    countryMap.put("NOR", "Norway");
    countryMap.put("OMN", "Oman");
    countryMap.put("PAK", "Pakistan");
    countryMap.put("PLW", "Palau");
    countryMap.put("PSE", "Palestine, State of");
    countryMap.put("PAN", "Panama");
    countryMap.put("PNG", "Papua New Guinea");
    countryMap.put("PRY", "Paraguay");
    countryMap.put("PER", "Peru");
    countryMap.put("PHL", "Philippines (the)");
    countryMap.put("PCN", "Pitcairn");
    countryMap.put("POL", "Poland");
    countryMap.put("PRT", "Portugal");
    countryMap.put("PRI", "Puerto Rico");
    countryMap.put("QAT", "Qatar");
    countryMap.put("MKD", "Republic of North Macedonia");
    countryMap.put("ROU", "Romania");
    countryMap.put("RUS", "Russian Federation (the)");
    countryMap.put("RWA", "Rwanda");
    countryMap.put("REU", "Réunion");
    countryMap.put("BLM", "Saint Barthélemy");
    countryMap.put("SHN", "Saint Helena, Ascension and Tristan da Cunha");
    countryMap.put("KNA", "Saint Kitts and Nevis");
    countryMap.put("LCA", "Saint Lucia");
    countryMap.put("MAF", "Saint Martin (French part)");
    countryMap.put("SPM", "Saint Pierre and Miquelon");
    countryMap.put("VCT", "Saint Vincent and the Grenadines");
    countryMap.put("WSM", "Samoa");
    countryMap.put("SMR", "San Marino");
    countryMap.put("STP", "Sao Tome and Principe");
    countryMap.put("SAU", "Saudi Arabia");
    countryMap.put("SEN", "Senegal");
    countryMap.put("SRB", "Serbia");
    countryMap.put("SYC", "Seychelles");
    countryMap.put("SLE", "Sierra Leone");
    countryMap.put("SGP", "Singapore");
    countryMap.put("SXM", "Sint Maarten (Dutch part)");
    countryMap.put("SVK", "Slovakia");
    countryMap.put("SVN", "Slovenia");
    countryMap.put("SLB", "Solomon Islands");
    countryMap.put("SOM", "Somalia");
    countryMap.put("ZAF", "South Africa");
    countryMap.put("SGS", "South Georgia and the South Sandwich Islands");
    countryMap.put("SSD", "South Sudan");
    countryMap.put("ESP", "Spain");
    countryMap.put("LKA", "Sri Lanka");
    countryMap.put("SDN", "Sudan (the)");
    countryMap.put("SUR", "Suriname");
    countryMap.put("SJM", "Svalbard and Jan Mayen");
    countryMap.put("SWE", "Sweden");
    countryMap.put("CHE", "Switzerland");
    countryMap.put("SYR", "Syrian Arab Republic");
    countryMap.put("TWN", "Taiwan");
    countryMap.put("TJK", "Tajikistan");
    countryMap.put("TZA", "Tanzania, United Republic of");
    countryMap.put("THA", "Thailand");
    countryMap.put("TLS", "Timor-Leste");
    countryMap.put("TGO", "Togo");
    countryMap.put("TKL", "Tokelau");
    countryMap.put("TON", "Tonga");
    countryMap.put("TTO", "Trinidad and Tobago");
    countryMap.put("TUN", "Tunisia");
    countryMap.put("TUR", "Turkey");
    countryMap.put("TKM", "Turkmenistan");
    countryMap.put("TCA", "Turks and Caicos Islands (the)");
    countryMap.put("TUV", "Tuvalu");
    countryMap.put("UGA", "Uganda");
    countryMap.put("UKR", "Ukraine");
    countryMap.put("ARE", "United Arab Emirates (the)");
    countryMap.put(
      "GBR",
      "United Kingdom of Great Britain and Northern Ireland (the)"
    );
    countryMap.put("UMI", "United States Minor Outlying Islands (the)");
    countryMap.put("USA", "United States of America (the)");
    countryMap.put("URY", "Uruguay");
    countryMap.put("UZB", "Uzbekistan");
    countryMap.put("VUT", "Vanuatu");
    countryMap.put("VEN", "Venezuela (Bolivarian Republic of)");
    countryMap.put("VNM", "Viet Nam");
    countryMap.put("VGB", "Virgin Islands (British)");
    countryMap.put("VIR", "Virgin Islands (U.S.)");
    countryMap.put("WLF", "Wallis and Futuna");
    countryMap.put("ESH", "Western Sahara");
    countryMap.put("YEM", "Yemen");
    countryMap.put("ZMB", "Zambia");
    countryMap.put("ZWE", "Zimbabwe");
    countryMap.put("ALA", "Åland Islands");
  }

  public static enum RegistrationFailureEnums {
    USERNAME_EMPTY,
    FIRSTNAME_EMPTY,
    LASTNAME_EMPTY,
    EMAIL_EMPTY,
    PASSWORD_EMPTY,
    DOB_EMPTY,
  }
}
