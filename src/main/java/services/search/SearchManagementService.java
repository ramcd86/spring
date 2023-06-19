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

	public String search(String textSearch, String postcode, String tagList)
		throws JsonMappingException, JsonProcessingException {
		// Sanitise the text string, adding %'s in spaces for the LIKE query.
		String sanitisedTextSearch = (textSearch != null)
			? textSearch
				.replaceAll("[^a-zA-Z0-9 ]", "")
				.replaceAll(" ", "%")
				.toLowerCase()
			: "";
		ArrayList<String> localPostcodes = new ArrayList<String>();
		ArrayList<String> localTagList = new ArrayList<String>();

		String sqlQuery = "";

		// If the tag list is not null, create a new arraylist of tags.
		if (tagList != null) {
			String[] tagListValues = tagList.split(",");
			localTagList = new ArrayList<>(Arrays.asList(tagListValues));
		}

		// If the postcode is populated, use Postcode.io to get a list of surrounding postcodes to make sure we cover a decent area in the search.
		if (postcode != null) {
			CompletableFuture<ArrayList<String>> getPostCodesCompletableFuture = CompletableFuture.supplyAsync(() -> {
					try {
						return gatherLocalPostCodes(postcode);
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
				}
			);
			localPostcodes = getPostCodesCompletableFuture.join();
		}

		// If the text serch is empty, assume we're going to search on tags and postcodes alone.
		if (sanitisedTextSearch.isEmpty()) {
			sqlQuery = "SELECT * FROM stores";

			// If the taglist isn't empty, append them to the SQL statement.
			if (!localTagList.isEmpty()) {
				String localTagsFormattedForQuery = localTagList
					.stream()
					.map(s -> "FIND_IN_SET('" + s + "', craftTags) > 0")
					.collect(Collectors.joining(" OR "));
				sqlQuery = sqlQuery + " WHERE " + localTagsFormattedForQuery;
			}

			// Same as above, but for postcodes. If the taglist is empty, we're just searching for postcodes.
			if (!localPostcodes.isEmpty()) {
				String andOrWhere = !localTagList.isEmpty()
					? " AND "
					: " WHERE ";
				String postcodesFormattedForQuery = localPostcodes
					.stream()
					.map(s ->
						"FIND_IN_SET('" +
						s
							.replaceAll("\\s+", "")
							.replaceAll("[^a-zA-Z0-9]", "")
							.toLowerCase() +
						"', postcode) > 0"
					)
					.collect(Collectors.joining(" OR "));
				sqlQuery = sqlQuery + andOrWhere + postcodesFormattedForQuery;
			}

			sqlQuery = sqlQuery + ";";
		} else {
			// If the search string isn't empty, we're going to soft search on store titles.
			sqlQuery =
				"SELECT * FROM stores WHERE storeTitle LIKE '%" +
				sanitisedTextSearch +
				"%'";

			// Same as previously searching through postcodes. We don't need to worry about this handling
			// a postcode search with an empty string because it'll default to the condition above which handles 'just postcode' searches.
			if (!localPostcodes.isEmpty()) {
				String postcodesFormattedForQuery = localPostcodes
					.stream()
					.map(s ->
						"FIND_IN_SET('" +
						s
							.replaceAll("\\s+", "")
							.replaceAll("[^a-zA-Z0-9]", "")
							.toLowerCase() +
						"', postcode) > 0"
					)
					.collect(Collectors.joining(" OR "));
				sqlQuery =
					sqlQuery + " AND ( " + postcodesFormattedForQuery + " )";
			}

			sqlQuery = sqlQuery + ";";
		}

		return sqlQuery;
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
