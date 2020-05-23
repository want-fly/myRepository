package com.lhr.es.util;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class HtmlParseUtil {

	
	
	public List<product> parseJD(String keyword) throws MalformedURLException, IOException{
		
		String url="https://search.jd.com/Search?keyword="+keyword;
		Document document = Jsoup.parse(new URL(url),3000);
		Element elementById = document.getElementById("J_goodsList");
		Elements elementsByTag = elementById.getElementsByTag("li");
		
		
		
		
		ArrayList<product> arrayList = new ArrayList<product>();
		
		
		
		for(Element  Elements:elementsByTag) {
			
			String img = Elements.getElementsByTag("img").eq(0).attr("source-data-lazy-img");
			String price = Elements.getElementsByClass("p-price").eq(0).text();
			String pname = Elements.getElementsByClass("p-name").eq(0).text();
			 product product = new product();
			 
			product.setTitle(pname);
			product.setImg(img);
			product.setPrice(price);
			arrayList.add(product);
			
		}
		return arrayList;
	}

	

}
