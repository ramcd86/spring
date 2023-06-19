package com.tradr.springboot.view.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tradr.springboot.view.storeclasses.StoreSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import services.search.SearchManagementService;
import services.storemanagement.StoreManagementService;
import services.utils.StoreEnums;

@Controller
public class SearchController {

	private SearchManagementService searchManagementService;
	private StoreManagementService storeManagementService;

	public SearchController(
		SearchManagementService searchManagementService,
		StoreManagementService storeManagementService
	) {
		this.searchManagementService = searchManagementService;
		this.storeManagementService = storeManagementService;
	}

	@PostMapping("search")
	public ResponseEntity<StoreSummaryResponse> search(
		@RequestParam(value = "textsearch", required = false) String textSearch,
		@RequestParam(value = "postcode", required = false) String postcode,
		@RequestParam(value = "taglist", required = false) String tagList
	) throws JsonMappingException, JsonProcessingException {
		StoreSummaryResponse storeSummaryResponseFromSearch = new StoreSummaryResponse();

		if (textSearch == null && postcode == null & tagList == null) {
			storeSummaryResponseFromSearch.setStoreSummaryQueryStatus(
				StoreEnums.SEARCH_FAILED_NO_PARAMS
			);
			return ResponseEntity
				.badRequest()
				.body(storeSummaryResponseFromSearch);
		}
		String sqlStatement = searchManagementService.search(
			textSearch,
			postcode,
			tagList
		);
		storeSummaryResponseFromSearch =
			storeManagementService.getStoresListSummaryFromDatabase(
				sqlStatement
			);
		return ResponseEntity.ok().body(storeSummaryResponseFromSearch);
	}
}
