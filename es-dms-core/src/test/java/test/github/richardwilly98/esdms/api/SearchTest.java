package test.github.richardwilly98.esdms.api;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.SearchResultImpl;
import com.github.richardwilly98.esdms.VersionImpl;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.Version;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

public class SearchTest {

	private static Logger log = Logger.getLogger(SearchTest.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private Set<Document> getTestDocuments(int number){
		
		LinkedHashSet<Document> docs = new LinkedHashSet<Document>();
		
		for (int i = 0; i < number; i++){
			String attributeKey = "attribute: " + i;
			String attributeValue = "value: " + i;
			String id = "id-" + i + " @time: "+ System.currentTimeMillis();
			String name = "name-" + i + " @time: " + System.currentTimeMillis();
			String html = "<html><body><h1>This is document number: " + i + "</h1></body></html>";
			byte[] content = html.getBytes();
			Map<String, Object> attributes = newHashMap();
			attributes.put(attributeKey, attributeValue);
//			DocumentTest document = new DocumentTest(new DocumentImpl(id, name, new FileImpl(content, "test.html", "text/html"), attributes));
			Set<Version> versions = newHashSet();
			versions.add(new VersionImpl.Builder().documentId(id).file(new FileImpl.Builder().content(content).name("test" + i + ".html").contentType("text/html").build()).current(true).versionId(1).build());
			DocumentTest document = new DocumentTest(new DocumentImpl.Builder().versions(versions).id(id).name(name).attributes(attributes).roles(null));
//			DocumentTest document = new DocumentTest(new DocumentImpl.Builder().file(new FileImpl.Builder().content(content).name("test" + i + ".html").contentType("text/html").build()).id(id).name(name).attributes(attributes).roles(null));
			docs.add(document);
		}
		return docs;
	}
	
	@Test
	public void testSearchResultAPI() throws Throwable {
		log.debug("*** testSearchResultAPI tests: starting ***");
		
		long stime = System.currentTimeMillis();
		String sTime = "time: " + stime;
		
		Set<Document> documents = getTestDocuments(10);				//documentService.search("*", 0, 10);
		log.info(String.format("Document count: %s", documents.size()));
		
		
		SearchResult<Document> sr = new SearchResultImpl<Document>();
		sr.setPageSize(3);
		sr.addAll(documents);
		
		log.debug("Search Result created, size: " + sr.getTotalHits() + " Page size: " + sr.getPageSize());

		Assert.assertEquals(sr.getTotalHits(), 10);
		sr.first();
		
		Set<Document> page;
		int position = 0;
		log.debug("test on: sequential paging.......");
		while (sr.hasData()){
			
			page = sr.getNextPage();
			for(Document d: page){
				log.debug(">>>>> Document: " + d.getName() + " @position: " + position++);
			}
		}
		
		log.debug("test on: moving cursor to page.......");
		sr.setPageIndex(2);
		position = sr.getPageIndex() * sr.getPageSize();
		
		while (sr.hasData()){
			
			page = sr.getNextPage();
			for(Document d: page){
				log.debug(">>>>> Document: " + d.getName() + " @position: " + position++);
			}
		}
		
		log.debug("test on: random paging.......");
		page = sr.getPage(0);
		Assert.assertEquals(page.size(), 3);
		for(Document d: page){
			log.debug("Page 0 Document: " + d.getName());
		}
		
		page = sr.getPage(1);
		Assert.assertEquals(page.size(), 3);
		for(Document d: page){
			log.debug("Page 1 Document: " + d.getName());
		}
		
		page = sr.getPage(2);
		Assert.assertEquals(page.size(), 3);
		for(Document d: page){
			log.debug("Page 2 Document: " + d.getName());
		}
		
		page = sr.getPage(3);
		Assert.assertEquals(page.size(), 1);
		for(Document d: page){
			log.debug("Page 3 Document: " + d.getName());
		}
		
		page = sr.getPage(4);
		log.debug("Page 4 ");
		Assert.assertNull(page);
		
		log.debug("*** testSearchResultAPI tests: completed successfully ***");
	}
}
