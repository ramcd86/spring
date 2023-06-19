package services.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradr.springboot.view.searchclasses.ResponseObject;
import com.tradr.springboot.view.searchclasses.ResultObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class SearchManagementService {

	public String generateSearchSqlQuery(
		String textSearch,
		String postcode,
		String tagList
	) throws JsonMappingException, JsonProcessingException {
		String sanitisedTextSearch = sanitizeTextSearch(textSearch);

		List<String> localPostcodes = new ArrayList<>();
		List<String> localTagList = createTagList(tagList);

		StringBuilder sqlQuery = new StringBuilder();

		if (sanitisedTextSearch.isEmpty()) {
			sqlQuery.append("SELECT * FROM stores");

			if (!localTagList.isEmpty()) {
				String localTagsFormattedForQuery = formatTagsForQuery(
					localTagList
				);
				sqlQuery.append(" WHERE ").append(localTagsFormattedForQuery);
			}

			if (!localPostcodes.isEmpty()) {
				String andOrWhere = localTagList.isEmpty()
					? " WHERE "
					: " AND ";
				String postcodesFormattedForQuery = formatPostcodesForQuery(
					localPostcodes
				);
				sqlQuery.append(andOrWhere).append(postcodesFormattedForQuery);
			}

			sqlQuery.append(";");
		} else {
			sqlQuery
				.append("SELECT * FROM stores WHERE storeTitle LIKE '%")
				.append(sanitisedTextSearch)
				.append("%'");

			if (!localPostcodes.isEmpty()) {
				String postcodesFormattedForQuery = formatPostcodesForQuery(
					localPostcodes
				);
				sqlQuery
					.append(" AND (")
					.append(postcodesFormattedForQuery)
					.append(")");
			}

			sqlQuery.append(";");
		}

		return sqlQuery.toString();
	}

	private String sanitizeTextSearch(String textSearch) {
		if (textSearch != null) {
			return textSearch
				.replaceAll("[^a-zA-Z0-9 ]", "")
				.replaceAll(" ", "%")
				.toLowerCase();
		}
		return "";
	}

	private List<String> createTagList(String tagList) {
		List<String> localTagList = new ArrayList<>();
		if (tagList != null) {
			localTagList = Arrays.asList(tagList.split(","));
		}
		return localTagList;
	}

	private String formatTagsForQuery(List<String> tagList) {
		return tagList
			.stream()
			.map(s -> "FIND_IN_SET('" + s + "', craftTags) > 0")
			.collect(Collectors.joining(" OR "));
	}

	private String formatPostcodesForQuery(List<String> postcodes) {
		return postcodes
			.stream()
			.map(s -> "FIND_IN_SET('" + sanitizePostcode(s) + "', postcode) > 0"
			)
			.collect(Collectors.joining(" OR "));
	}

	private String sanitizePostcode(String postcode) {
		return postcode
			.replaceAll("\\s+", "")
			.replaceAll("[^a-zA-Z0-9]", "")
			.toLowerCase();
	}

	public ArrayList<String> gatherLocalPostCodes(String postcode)
		throws JsonMappingException, JsonProcessingException {
		String formattedPostCode = postcode
			.replaceAll("\\s+", "")
			.replaceAll("[^a-zA-Z0-9]", "")
			.toLowerCase();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(
			Collections.singletonList(MediaType.APPLICATION_JSON)
		);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				"http://api.postcodes.io/postcodes/" +
				formattedPostCode +
				"/nearest",
				HttpMethod.GET,
				null,
				String.class
			);

			String responseBody = response.getBody();
			ObjectMapper objectMapper = new ObjectMapper();

			ResponseObject responseObject = objectMapper.readValue(
				responseBody,
				ResponseObject.class
			);

			if (responseObject.getStatus() == 200) {
				List<ResultObject> resultDataWithPostCodes = responseObject.getResult();
				ArrayList<String> postCodes = new ArrayList<String>();

				for (ResultObject itemWithPostCode : resultDataWithPostCodes) {
					postCodes.add(itemWithPostCode.getPostcode());
				}

				return postCodes;
			}

			if (responseObject.getStatus() == 404) {
				return new ArrayList<String>();
			}
		} catch (HttpClientErrorException e) {
			return new ArrayList<String>();
		}

		return new ArrayList<String>();
	}
}
