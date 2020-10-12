package com.sum.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sum.service.SumContractService;

@RestController
public class SumContractController {

	@Autowired
	private SumContractService sumContractService;

	@RequestMapping("/getMessage")
	public ResponseEntity<String> getQuestion() {
		return ResponseEntity.ok(SumContractService.CLIENT_MESSAGE);
	}

	@RequestMapping("/getQuestion")
	public ResponseEntity<String> getNumbers() {
		return ResponseEntity.ok(sumContractService.getQuestion());
	}

	@RequestMapping("/execute")
	public ResponseEntity execute(@RequestParam("values") List<Integer> values,
			@RequestParam(name = "sumTotal") int sumTotal) {
		return sumContractService.executeSum(values, sumTotal);

	}

}
