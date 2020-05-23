package com.lhr.es.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.lhr.es.service.ProductService;

@RestController
public class ProductController {
	@Autowired
	private ProductService productService;


	@GetMapping("/parse/{keyword}")
	public Boolean parse(@PathVariable("keyword") String keyword) throws MalformedURLException, IOException {
		Boolean parseProduct = productService.parseProduct(keyword);
		
		return parseProduct;
		
	}
	
	
	@GetMapping("/search/{keyword}/{pageNo}/{pageZize}")
	public List<Map<String, Object>> search(@PathVariable("keyword")String keyword ,
			@PathVariable("pageNo")int pageNo,
			@PathVariable("pageZize")int pageZize) throws IOException {
		System.out.println(keyword);
		
		List<Map<String,Object>> searchPage = productService.searchPage(keyword, pageNo, pageZize);
		
		
		
		return searchPage;
		
		
		
		
	}
}
