package com.tradr.springboot.view.searchclasses;

import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResponseObject {

	private int status;
	private List<ResultObject> result;
}
