package com.sum.service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.sum.util.SumContractMemoryCache;

@Service
public class SumContractService {

	public static final String CLIENT_MESSAGE = "Hey Service, can you provide me a question with numbers to add?";
	public static final String CLIENT_QUESTION = "CLIENT_QUESTION";
	public static final String CLIENT_INFO_QUESTION = "Here you go, solve the question: “Please sum the numbers ";
	public static final String SUCCESS_MESSAGE = "Thats Great";
	public static final String FAILURE_MESSAGE = "That’s wrong. Please try again.";

	SumContractMemoryCache<String, Object> cache = new SumContractMemoryCache<String, Object>(50, 50, 1);

	public SumContractMemoryCache<String, Object> getCache() {
		return this.cache;
	}

	public String getQuestion() {
		List<Object> question = getRandomNumbers();
		// List<Object> numbers = generateRandomArray();
		cache.put(CLIENT_QUESTION, question);
		List<Integer> listOfQuestion = (List<Integer>) cache.get(CLIENT_QUESTION);
		System.out.println(Arrays.deepToString(listOfQuestion.toArray()));
		return CLIENT_INFO_QUESTION + Arrays.deepToString(listOfQuestion.toArray());
	}

	public ResponseEntity<String> executeSum(List<Integer> values, int sumTotal) {
		Integer sum = getSumOfNumbers(values);
		System.out.println(sum);
		List<Integer> intListOne = (List<Integer>) cache.get(CLIENT_QUESTION);
		if (sum == sumTotal && values.equals(intListOne)) {
			cache.cleanup();
			List<Integer> intListTwo = (List<Integer>) cache.get(CLIENT_QUESTION);
			System.out.println("************************" + Arrays.deepToString(intListTwo.toArray()));
			return ResponseEntity.ok(SUCCESS_MESSAGE);
		} else {
			cache.cleanup();
			return new ResponseEntity(FAILURE_MESSAGE, new HttpHeaders(), HttpStatus.BAD_REQUEST);

		}
	}

	public Integer getSumOfNumbers(List<Integer> values) {
		return values.stream().reduce(0, (a, b) -> a + b);
	}

	public List<Object> getRandomNumbers() {
		return new Random().ints(0, 350).limit(10).boxed().collect(Collectors.toList());
	}

}
