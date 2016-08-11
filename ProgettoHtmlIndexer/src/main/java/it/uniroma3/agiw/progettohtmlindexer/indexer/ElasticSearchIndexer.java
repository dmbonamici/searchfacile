package it.uniroma3.agiw.progettohtmlindexer.indexer;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.tika.io.IOUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

public class ElasticSearchIndexer {
	static Document doc;
	static BytesReference json;

	public ElasticSearchIndexer(){
		super();
	}

	private static Client getClient(){

		Settings settings = Settings.settingsBuilder()
				.put("cluster.name", "elasticsearch_agiw").build();
		Client transportClient = TransportClient.builder()
				.settings(settings)
				.build();

		try{

			InetSocketTransportAddress address = 
					new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300);
			((TransportClient) transportClient).addTransportAddress(address);

		}catch(Exception e){

			e.printStackTrace();
		}

		return (Client) transportClient;
	}


	public static void main(final String[] args) throws IOException, InterruptedException{

		final Client client = getClient();
		final String indexName = "agiw"; 
		final String indexNameSoc = "agiw_soc"; 
		final String indexNameAvv = "agiw_avv"; 
		final String documentType = "html";

		final IndicesExistsResponse res = 
				client.admin().indices().prepareExists(indexName).execute().actionGet();

		if (res.isExists()) {
			final DeleteIndexRequestBuilder delIdx = client.admin().indices().prepareDelete(indexName);
			delIdx.execute().actionGet();
		}

		final IndicesExistsResponse resSoc = 
				client.admin().indices().prepareExists(indexNameSoc).execute().actionGet();

		if (resSoc.isExists()) {
			final DeleteIndexRequestBuilder delSoc = client.admin().indices().prepareDelete(indexNameSoc);
			delSoc.execute().actionGet();
		}

		final IndicesExistsResponse resAvv = 
				client.admin().indices().prepareExists(indexNameAvv).execute().actionGet();

		if (resAvv.isExists()) {
			final DeleteIndexRequestBuilder delAvv = client.admin().indices().prepareDelete(indexNameAvv);
			delAvv.execute().actionGet();
		}


		final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);
		final CreateIndexRequestBuilder createIndexRequestBuilderSoc = client.admin().indices().prepareCreate(indexNameSoc);
		final CreateIndexRequestBuilder createIndexRequestBuilderAvv = client.admin().indices().prepareCreate(indexNameAvv);

		// MAPPING 

		final XContentBuilder mappingBuilder = jsonBuilder()
				.startObject()
				.startObject(documentType)
				.startObject("properties")
				.startObject("url")
				.field("type", "string")
				.field("store","yes")
				.endObject()
				.startObject("title")
				.field("type", "string")
				.field("store", "no")
				.field("analyzer", "standard")
				.endObject()
				.startObject("content")
				.field("type", "string")
				.field("store", "no")
				.field("analyzer", "CustomAnalyzer")
				.endObject()
				.endObject()
				.endObject()
				.endObject();

		System.out.println(mappingBuilder.string());
		createIndexRequestBuilder.addMapping(documentType, mappingBuilder);
		createIndexRequestBuilderSoc.addMapping(documentType, mappingBuilder);
		createIndexRequestBuilderAvv.addMapping(documentType, mappingBuilder);

		/* ANALYZER */

		final XContentBuilder settingsBuilder = XContentFactory.jsonBuilder()
				.startObject()
				.startObject("analysis")
				.startObject("char_filter") 
				.startObject("filter_html")
				.field("type", "html_strip") 
				.endObject()
				.endObject()
				.startObject("filter")
				.startObject("filter_shingle")
				.field("type","shingle")
				.field("max_shingle_size",2)
				.field("min_shingle_size",2)
				.field("output_unigrams",false)
				.endObject()
				.startObject("filter_elision")
				.field("type","elision")
				.array("articles",
						"c", "l", "all", "dall", "dell",
						"nell", "sull", "coll", "pell",
						"gl", "agl", "dagl", "degl", "negl",
						"sugl", "un", "m", "t", "s", "v", "d")
				.endObject()
				.startObject("filter_stemmer")
				.field("type","porter_stem")
				.field("language","italian")
				.endObject()
				.startObject("italian_stemmer")
				.field("type","stemmer")
				.field("language","italian")
				.endObject()
				.startObject("my_ascii_folding")
				.field("type","asciifolding")
				.field("preserve_original",true)
				.endObject()
				.startObject("filter_stop")
				.field("type","stop")
				.field("stopwords","_italian_")
				.endObject()
				.endObject()
				.startObject("tokenizer")
				.startObject("my_tokenizer")
				.field("type","letter")
				.endObject()
				.endObject()
				.startObject("analyzer")
				.startObject("CustomAnalyzer")
				.field("type","custom")
				.field("tokenizer","my_tokenizer")
				.field("filter",new String []{"standard","lowercase","my_ascii_folding","filter_stemmer","filter_shingle","filter_stop","filter_elision"})
				.field("char_filter", "filter_html")
				.endObject()
				.endObject()
				.endObject()
				.endObject();

		System.out.println(settingsBuilder.string());
		createIndexRequestBuilder.setSettings(settingsBuilder);
		createIndexRequestBuilderSoc.setSettings(settingsBuilder);
		createIndexRequestBuilderAvv.setSettings(settingsBuilder);



		final CreateIndexResponse createResponse = createIndexRequestBuilder.execute().actionGet();
		final CreateIndexResponse createResponseSoc = createIndexRequestBuilderSoc.execute().actionGet();
		final CreateIndexResponse createResponseAvv = createIndexRequestBuilderAvv.execute().actionGet();
		System.out.println(createResponse.toString());
		System.out.println(createResponseAvv.toString());
		System.out.println(createResponseSoc.toString());

		IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, documentType);
		IndexRequestBuilder indexRequestBuilderSoc = client.prepareIndex(indexNameSoc, documentType);
		IndexRequestBuilder indexRequestBuilderAvv = client.prepareIndex(indexNameAvv, documentType);


		//cambiare path
		Files.walk(Paths.get("/Users/federico/Documents/AGIW/workspace/ProgettoBingSearch/Output/URL")).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				File input = new File(filePath.toString());
				try {
					doc = Jsoup.parse(input, "UTF-8", "http://example.com/");


					Elements title = doc.getElementsByTag("title");
					String dirtyt = title.toString();
					String clean = Jsoup.clean(dirtyt, Whitelist.none());

					Elements body = doc.getElementsByTag("body");
					String dirtyb = body.toString();
					String cleanb = Jsoup.clean(dirtyb, Whitelist.none());




					json = jsonBuilder()
							.startObject()
							.startObject("file")
							.field("title", clean)
							.field("url", filePath.toString())
							.field("content", cleanb)
							.endObject()
							.endObject().bytes();
					System.out.println(clean);

					indexRequestBuilder.setSource(json);

					if(clean.contains("Facebook") && !(clean.contains("Twitter")) && !(clean.contains("LinkedIn")) || !(clean.contains("Facebook")) && clean.contains("Twitter") && !(clean.contains("LinkedIn")) || !(clean.contains("Facebook")) && !(clean.contains("Twitter")) && clean.contains("LinkedIn")){
						indexRequestBuilderSoc.setSource(json);
						System.out.println("social");
						IndexResponse responseSoc = indexRequestBuilderSoc.execute().actionGet();
						System.out.println(responseSoc.toString());
					}
					if(clean.contains("Avv.") || clean.contains("Avvocato") ){
						indexRequestBuilderAvv.setSource(json);
						System.out.println("lawyer");
						IndexResponse responseAvv = indexRequestBuilderAvv.execute().actionGet();
						System.out.println(responseAvv.toString());
					}

				}catch(Exception e){
					e.printStackTrace();
				}


				IndexResponse response = indexRequestBuilder.execute().actionGet();	

				System.out.println(response.toString());

			}
		});



	}


}



