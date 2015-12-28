package slimer.utils.ES;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PreDestroy;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.GeoDistanceFilterBuilder;
import org.elasticsearch.index.query.GeoPolygonFilterBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;
import com.google.gson.Gson;

/**
 * Elasticsearch Proxy ES搜索代理类，2015-07-20增加眯客2.1新需求，支持多城市搜素酒店。
 * ES中根据不同城市创建与之对应的index，C端搜索酒店时从与之对应的index中查找酒店信息。
 */
@Repository
public class ElasticsearchProxy {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(ElasticsearchProxy.class);
	
	@Autowired
	private ElasticSearchConfiguration elasticSearchConfiguration;


	private Gson gson = new Gson();
	private Client client = null;
	private Properties prop;

	/**
	 *
	 */
	public ElasticsearchProxy() {
		try {
			this.initClient();
		} catch (IOException e) {
			this.logger.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	@PreDestroy
	public void close() {
		Client client = this.getClient();
		try {
			if (client != null) {
				client.close();
			}
		} catch (Exception e) {
			this.logger.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
	}

	/**
	 * 批量添加文档
	 *
	 * @param objList
	 * @return
	 */
	public BulkResponse batchAddDocument(Collection<Object> objList) {
		BulkResponse bulkResponse = null;
		try {
			BulkRequestBuilder requestBuilder = this.getClient().prepareBulk();
			for (Object obj : objList) {
				requestBuilder.add(this.prepareIndex(obj));
			}
			bulkResponse = requestBuilder.execute().actionGet();
		} catch (Exception e) {
			this.logger.error("batchAddDocument method error:\n" + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
		return bulkResponse;
	}

	/**
	 * 指定index和type做批量添加文档操作
	 *
	 * @param indexName
	 *            参数：index
	 * @param typeName
	 *            参数：type
	 * @param objList
	 *            参数：文档列表
	 * @return BulkResponse
	 */
	public BulkResponse batchAddDocument(String indexName, String typeName, Collection<Object> objList) {
		BulkResponse bulkResponse = null;
		try {
			BulkRequestBuilder requestBuilder = this.getClient().prepareBulk();
			for (Object obj : objList) {
				requestBuilder.add(this.prepareIndex(indexName, typeName, obj));
			}
			bulkResponse = requestBuilder.execute().actionGet();
		} catch (Exception e) {
			this.logger.error("batchAddDocument method error:\n" + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
		return bulkResponse;
	}

	/**
	 * 添加单个文档
	 *
	 * @param obj
	 * @return
	 */
	public IndexResponse signleAddDocument(Object obj) {
		IndexResponse indexResponse = null;
		try {
			indexResponse = this.prepareIndex(obj).execute().actionGet();
		} catch (Exception e) {
			this.logger.error("signleAddDocument method error:\n" + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
		return indexResponse;
	}

	/**
	 * 指定index和type添加文档。
	 *
	 * @param indexName
	 *            参数：index
	 * @param typeName
	 *            参数：type
	 * @param obj
	 *            参数：文档对象
	 * @return IndexResponse
	 */
	public IndexResponse signleAddDocument(String indexName, String typeName, Object obj) {
		IndexResponse indexResponse = null;
		try {
			indexResponse = this.prepareIndex(indexName, typeName, obj).execute().actionGet();
		} catch (Exception e) {
			this.logger.error("signleAddDocument method error:\n" + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
		return indexResponse;
	}

	/**
	 *
	 */
	public void deleteAllDocument() {
		try {
			DeleteByQueryResponse deleteByQueryResponse = this.getClient().prepareDeleteByQuery(elasticSearchConfiguration.index).setTypes(elasticSearchConfiguration.type)
					.setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
			int status = deleteByQueryResponse.status().getStatus();
			this.logger.info("deleteAllDocument from index: {}, type: {}, state is {}", elasticSearchConfiguration.index, elasticSearchConfiguration.type, status);
		} catch (Exception e) {
			this.logger.error("deleteAllDocument method error:\n" + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
	}

	/**
	 * 删除指定index和type的所有文档.
	 *
	 * @param indexName
	 *            参数：index
	 * @param typeName
	 *            参数：type
	 */
	public void deleteAllDocument(String indexName, String typeName) {
		try {
			DeleteByQueryResponse deleteByQueryResponse = this.getClient().prepareDeleteByQuery(indexName).setTypes(typeName).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
			int status = deleteByQueryResponse.status().getStatus();
			this.logger.info("deleteAllDocument from index: {}, type: {}, state is {}", indexName, typeName, status);
		} catch (Exception e) {
			this.logger.error("deleteAllDocument method error:\n" + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public DeleteResponse deleteDocument(String id) {
		DeleteResponse deleteResponse = null;
		try {
			deleteResponse = this.prepareDelete().setId(id).execute().actionGet();
		} catch (Exception e) {
			this.logger.error("deleteDocument method error:\n" + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
		return deleteResponse;
	}

	/**
	 * 删除指定index和type中指定id的文档数据。
	 *
	 * @param indexName
	 *            参数：index
	 * @param typeName
	 *            参数：type
	 * @param id
	 *            参数：文档id
	 * @return DeleteResponse
	 */
	public DeleteResponse deleteDocument(String indexName, String typeName, String id) {
		DeleteResponse deleteResponse = null;
		try {
			deleteResponse = this.prepareDelete(indexName, typeName).setId(id).execute().actionGet();
			this.logger.info("deleteDocument id: {} from index: {}, type: {}", id, indexName, typeName);
		} catch (Exception e) {
			this.logger.error("deleteDocument method error:\n" + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
		return deleteResponse;
	}

	/**
	 *
	 * @param id
	 * @param obj
	 * @return
	 */
	public UpdateResponse updateDocument(String id, Object obj) {
		UpdateResponse updateResponse = null;
		try {
			String docs = this.getGson().toJson(obj);
			updateResponse = this.prepareUpdate().setId(id).setDoc(docs).execute().actionGet();
			this.logger.info("updateDocument id: {}, use object: {}.", id, docs);
		} catch (Exception e) {
			this.logger.error("updateDocument method error:\n" + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
		return updateResponse;
	}

	/**
	 * 指定index和type更新指定id的文档。
	 *
	 * @param indexName
	 *            参数：index
	 * @param typeName
	 *            参数：type
	 * @param id
	 *            参数：文档id
	 * @param obj
	 *            参数：更新数据对象
	 * @return UpdateResponse
	 */
	public UpdateResponse updateDocument(String indexName, String typeName, String id, Object obj) {
		UpdateResponse updateResponse = null;
		try {
			String docs = this.getGson().toJson(obj);
			updateResponse = this.prepareUpdate(indexName, typeName).setId(id).setDoc(docs).execute().actionGet();
			this.logger.info("updateDocument id: {} from index: {}, type: {}, use object: {}.", id, indexName, typeName, docs);
		} catch (Exception e) {
			this.logger.error("updateDocument method error:\n" + e.getLocalizedMessage(), e);
			e.printStackTrace();
		}
		return updateResponse;
	}

	/**
	 *
	 * @param id
	 * @param obj
	 * @return
	 */
	public UpdateResponse updateDocument(String id, String field, Object value) {
		UpdateResponse updateResponse = null;
		try {
			updateResponse = this.prepareUpdate().setId(id).setDoc(field, value).execute().actionGet();
		} catch (Exception e) {
			this.logger.error("updateDocument method error:\n{}, field: {}, value: {}", e.getLocalizedMessage(), field, value);
			e.printStackTrace();
		}
		return updateResponse;
	}

	/**
	 * 指定index和type更新文档中指定字段。
	 *
	 * @param indexName
	 * @param typeName
	 * @param id
	 * @param field
	 * @param value
	 * @param obj
	 * @return
	 */
	public UpdateResponse updateDocument(String indexName, String typeName, String id, String field, Object value) {
		UpdateResponse updateResponse = null;
		try {
			updateResponse = this.prepareUpdate(indexName, typeName).setId(id).setDoc(field, value).execute().actionGet();
			this.logger.info("updateDocument id: {}, field: {}, value: {} from index: {}, type: {}.", id, field, value, indexName, typeName);
		} catch (Exception e) {
			this.logger.error("updateDocument method error:\n{}, field: {}, value: {}", e.getLocalizedMessage(), field, value);
			e.printStackTrace();
		}
		return updateResponse;
	}

	/**
	 *
	 * @param obj
	 * @return
	 */
	private IndexRequestBuilder prepareIndex(Object obj) {
		return this.getClient().prepareIndex(elasticSearchConfiguration.index, elasticSearchConfiguration.type).setSource(this.getGson().toJson(obj));
	}

	/**
	 *
	 * @param obj
	 * @return
	 */
	private IndexRequestBuilder prepareIndex(String indexName, String typeName, Object obj) {
		return this.getClient().prepareIndex(indexName, typeName).setSource(this.getGson().toJson(obj));
	}

	/**
	 * 默认删除：上海的index和type
	 *
	 * @return
	 */
	private DeleteRequestBuilder prepareDelete() {
		return this.getClient().prepareDelete().setIndex(elasticSearchConfiguration.index).setType(elasticSearchConfiguration.type);
	}

	/**
	 * 指定index和type删除
	 *
	 * @param indexName
	 *            参数：index
	 * @param typeName
	 *            参数：type
	 * @return DeleteRequestBuilder
	 */
	private DeleteRequestBuilder prepareDelete(String indexName, String typeName) {
		return this.getClient().prepareDelete().setIndex(indexName).setType(typeName);
	}

	/**
	 * 默认更新：上海的index和type
	 *
	 * @return
	 */
	private UpdateRequestBuilder prepareUpdate() {
		return this.getClient().prepareUpdate().setIndex(elasticSearchConfiguration.index).setType(elasticSearchConfiguration.type);
	}

	/**
	 * 指定更新
	 *
	 * @param indexName
	 *            参数：index
	 * @param typeName
	 *            参数：type
	 * @return UpdateRequestBuilder
	 */
	private UpdateRequestBuilder prepareUpdate(String indexName, String typeName) {
		return this.getClient().prepareUpdate().setIndex(indexName).setType(typeName);
	}

	/**
	 * 默认搜索：上海的index和type
	 *
	 * @return
	 */
	public SearchRequestBuilder prepareSearch() {
		return this.getClient().prepareSearch(elasticSearchConfiguration.index).setTypes(elasticSearchConfiguration.type);
	}

	/**
	 * 指定搜索
	 *
	 * @param indexName
	 *            参数：index
	 * @param typeName
	 *            参数：type
	 * @return SearchRequestBuilder
	 */
	public SearchRequestBuilder prepareSearch(String indexName, String typeName) {
		return this.getClient().prepareSearch(indexName).setTypes(typeName);
	}

	/**
	 *
	 * @throws IOException
	 */
	private void initClient() throws IOException {
		String cityIndexs = "";
		try {
			elasticSearchConfiguration.initClient();

			// 默认上海index，没有index的话自动创建
			if (!this.indexExists(elasticSearchConfiguration.index)) {
				// auto create the index if it not exists.
				final CreateIndexRequestBuilder createIndexRequestBuilder = this.client.admin().indices().prepareCreate(elasticSearchConfiguration.index);
				final XContentBuilder mappingBuilder = this.createGeoMappingBuilder();
				createIndexRequestBuilder.addMapping(elasticSearchConfiguration.type, mappingBuilder);
				createIndexRequestBuilder.execute().actionGet();
				this.logger.info("auto create the index: {}, type: {}.", elasticSearchConfiguration.index, elasticSearchConfiguration.type);
			}

			// 其它城市的index
			if (StringUtils.isBlank(cityIndexs)) {
				return;
			}
			String[] indexs = cityIndexs.split(",");
			for (int i = 0; i < indexs.length; i++) {
				String _index = indexs[i];
				if (!this.indexExists(_index)) {
					// auto create the index if it not exists.
					final CreateIndexRequestBuilder createIndexRequestBuilder = this.client.admin().indices().prepareCreate(_index);
					final XContentBuilder mappingBuilder = this.createGeoMappingBuilder();
					createIndexRequestBuilder.addMapping(elasticSearchConfiguration.type, mappingBuilder);
					createIndexRequestBuilder.execute().actionGet();
					this.logger.info("auto create the index: {}, type: {}.", _index, elasticSearchConfiguration.type);
				}
			}
		} catch (Exception e) {
			this.logger.error("init elasticsearch client is error:\n" + e.getMessage(), e);
		}
	}

	/**
	 * 指定index是否存在
	 *
	 * @param indexName
	 *            参数：index
	 * @return boolean
	 */
	private boolean indexExists(String indexName) {
		this.logger.info(String.format("Verifying existence of index \"%s\"", indexName));
		IndicesExistsRequest request = new IndicesExistsRequest(indexName);
		IndicesExistsResponse response = this.client.admin().indices().exists(request).actionGet();
		if (response.isExists()) {
			this.logger.info("Index {} has exists.", indexName);
			return true;
		}
		this.logger.info("No such index name: {}", indexName);
		return false;
	}

	/**
	 *
	 * @return
	 * @throws IOException
	 */
	private XContentBuilder createGeoMappingBuilder() throws IOException {
		XContentBuilder xBuilder = XContentFactory.jsonBuilder();

		xBuilder.startObject();
		xBuilder.startObject("properties");
		xBuilder.startObject("pin");

		xBuilder.field("type", "geo_point");

		xBuilder.endObject();
		xBuilder.endObject();
		xBuilder.endObject();

		return xBuilder;
	}

	/**
	 *
	 * @return
	 */
	private Client getClient() {
		return this.client;
	}

	/**
	 *
	 * @return
	 */
	private Gson getGson() {
		return this.gson;
	}

	
	private GeoPolygonFilterBuilder createGeoFilterBuilder(List<GeoPoint> gpList) {
		GeoPolygonFilterBuilder filterBuilder = FilterBuilders.geoPolygonFilter("pin");
		for (GeoPoint gp : gpList) {
			filterBuilder.addPoint(gp);
		}
		return filterBuilder;
	}
	
	public List<String> searchHotelWithFields(Map<String, String> fields,int page,int size) {
		SearchHit[] hits = null;
		List<String> list=new ArrayList<String>();
		try {
			SearchRequestBuilder searchBuilder = this.prepareSearch();
			// 设置查询类型 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询 2.SearchType.SCAN = 扫描查询,无序
            searchBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            GeoPoint geoPoint=null;
			List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
			Iterator<String > iterator=fields.keySet().iterator();
			//遍历查询条件，并进行过滤
			while (iterator.hasNext()) {
				String keyString=iterator.next();
				String valueString=fields.get(keyString);
				//对于坐标单独处理
				if (!Strings.isNullOrEmpty(valueString)) {
					if (keyString.equals("pin")) {
						GeoDistanceFilterBuilder geoFilter = FilterBuilders.geoDistanceFilter("pin");
						geoPoint=GeoPoint.parseFromLatLon(valueString);
						geoFilter.point(geoPoint.getLat(), geoPoint.getLon()).distance(Double.parseDouble(fields.get("range")), DistanceUnit.METERS).optimizeBbox("memory").geoDistance(GeoDistance.ARC);
		                filterBuilders.add(geoFilter);
					}else if(!keyString.equals("range")){
						//queryfilter是模糊查询
						filterBuilders.add(FilterBuilders
		                        .queryFilter(QueryBuilders.matchQuery(keyString,
		                        		valueString).operator(Operator.AND)));
						//termfilter是精确查询
						//FilterBuilder termFilter = FilterBuilders.termFilter("id", "");
					}
	                
	            }
			}
			
			
			FilterBuilder[] builders = new FilterBuilder[] {};
			BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));
			searchBuilder.setPostFilter(boolFilter);
			// 距离排序
			this.sortByDistance(searchBuilder, geoPoint);
			
			 // 分页应用
            searchBuilder.setFrom((page - 1) * size).setSize(size);

			SearchResponse searchResponse = searchBuilder.execute().actionGet();
			SearchHits searchHits = searchResponse.getHits();
			hits = searchHits.getHits();
			//对返回结果进行封装
			if (null !=hits &&hits.length>0) {
				//list=dataTransfer(hits);
			}
			
		
		} catch (Exception e) {
			this.logger.error("searchHotelWithFields method error:\n" + e.getMessage());
		}
		return list;
	}
	
	/**
     * 按距离排序,其中pin是在定义es是设定的，
     * @param searchBuilder
     * 参数：查询请求builder
     * @param geopoint
     * 参数：屏幕坐标对象
     */
	private void sortByDistance(SearchRequestBuilder searchBuilder, GeoPoint geopoint) {
        searchBuilder.addSort(SortBuilders.geoDistanceSort("pin").point(geopoint.getLat(), geopoint.getLon()).order(SortOrder.ASC));
    }


}
