package com.lhr.es.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.lhr.es.util.ElasticsearchConfig;
import com.lhr.es.util.HtmlParseUtil;
import com.lhr.es.util.product;

import ch.qos.logback.core.pattern.color.BlueCompositeConverter;

@Service
public class ProductService {
	
	
	 @Autowired
	   private RestHighLevelClient RestHighLevelClient;
	
	
	public Boolean parseProduct(String keywords) throws MalformedURLException, IOException {
		List<product> parseJD = new HtmlParseUtil().parseJD(keywords);
		
		System.out.println(parseJD);
		System.out.println(parseJD.size());
		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.timeout("2m");
		for (int i = 0; i < parseJD.size(); i++) {
			bulkRequest.add(new IndexRequest("jd_pro1")
					.source(JSON.toJSONString(parseJD.get(i)),XContentType.JSON)
					);
			
		}
		BulkResponse bulk = RestHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
		
		return !bulk.hasFailures();
		
	}
	
	
	

	public List<Map<String, Object>> searchPage(String keyword ,int pageNo,int pageZize) throws IOException{
		if (pageNo<=1) {
			pageNo=1;
		}
		SearchRequest searchRequest = new SearchRequest("jd_pro1");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		
		searchSourceBuilder.from(pageNo);
		searchSourceBuilder.size(pageZize);
		
		

		 MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("title", keyword);
		 TermQueryBuilder termQuery = QueryBuilders.termQuery("title", keyword);
		searchSourceBuilder.query(termQuery);
		searchSourceBuilder.timeout(new TimeValue(60,TimeUnit.SECONDS));
		
		
		
		
		//高亮
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.field("title");
		highlightBuilder.preTags("<span style='color:red'>");
		highlightBuilder.postTags("</span>");
		highlightBuilder.requireFieldMatch(false);
		
		searchSourceBuilder.highlighter(highlightBuilder);
		
		
		
		searchRequest.source(searchSourceBuilder); 
		
		
		SearchResponse search = RestHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		
		ArrayList<Map<String,Object>> arrayList = new ArrayList<Map<String, Object>>();
		
		for (SearchHit hit:search.getHits().getHits()) {
			
			//解析高亮
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			HighlightField title = highlightFields.get("title");
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			if (title!=null) {
				Text[] fragments = title.fragments();
				String nname = "";
				for (Text fragment:fragments) {
					nname += fragment;
				}
				sourceAsMap.put("title", nname);
			}
			
			arrayList.add(sourceAsMap);
		}
		
		
		return arrayList;
		
	}
	

}
