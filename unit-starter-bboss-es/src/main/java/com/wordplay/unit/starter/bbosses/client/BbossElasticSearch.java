package com.wordplay.unit.starter.bbosses.client;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.bbosses.constant.ElasticSearchConstant;
import com.wordplay.unit.starter.bbosses.model.RangeObject;
import com.wordplay.unit.starter.bbosses.model.SearchVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.bulk.BulkCommand;
import org.frameworkset.elasticsearch.bulk.BulkInterceptor;
import org.frameworkset.elasticsearch.bulk.BulkProcessor;
import org.frameworkset.elasticsearch.bulk.BulkProcessorBuilder;
import org.frameworkset.elasticsearch.bulk.BulkRetryHandler;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESIndice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhuangpf
 * @date 2023-04-11
 */
public class BbossElasticSearch {

	private static final Logger LOGGER = LoggerFactory.getLogger(BbossElasticSearch.class);
	private static List<String> FILTER_INDEXS = Arrays.asList("filter_event");
	private BulkProcessor bulkProcessor;

	/**
	 * 初始化
	 */
	@PostConstruct
	public void init() {
		this.buildBulkProcessor();
	}

	/**
	 * @return ES RestClient
	 */
	public ClientInterface esClient() {
		return ElasticSearchHelper.getRestClientUtil();
	}

	/**
	 * @param esmapper mapper文件路径
	 * @return
	 */
	public ClientInterface esConfigClient(String esmapper) {
		return ElasticSearchHelper.getConfigRestClientUtil(esmapper);
	}

	public void buildBulkProcessor() {
		BulkProcessorBuilder bulkProcessorBuilder = new BulkProcessorBuilder();
		bulkProcessorBuilder.setBlockedWaitTimeout(0L).setBulkFailRetry(1).setBulkSizes(1000).setFlushInterval(5000L).setWarnMultsRejects(1000).setWorkThreads(10).setWorkThreadQueue(2048).setBulkProcessorName("sd_bulkprocessor").setBulkRejectMessage("Reject sd bulkprocessor").setElasticsearch("default").addBulkInterceptor(new BulkInterceptor() {
			public void beforeBulk(BulkCommand bulkCommand) {
			}

			public void afterBulk(BulkCommand bulkCommand, String result) {
				BbossElasticSearch.LOGGER.info("bulkProcessor totalSize:{}, totalFailedSize:{}", bulkCommand.getTotalSize(), bulkCommand.getTotalFailedSize());
			}

			public void exceptionBulk(BulkCommand bulkCommand, Throwable exception) {
				BbossElasticSearch.LOGGER.error("bulkProcessor exception:" + exception.getMessage(), exception);
			}

			public void errorBulk(BulkCommand bulkCommand, String result) {
				BbossElasticSearch.LOGGER.error("bulkProcessor errorBulk:{}", result);
			}
		}).setBulkRetryHandler(new BulkRetryHandler() {
			public boolean neadRetry(Exception exception, BulkCommand bulkCommand) {
				if (!(exception instanceof HttpHostConnectException) && !(exception instanceof ConnectTimeoutException) && !(exception instanceof UnknownHostException) && !(exception instanceof NoHttpResponseException)) {
					if (exception instanceof SocketException) {
						String message = exception.getMessage();
						if (message != null && message.trim().equals("Connection reset")) {
							return true;
						}
					}

					return false;
				} else {
					return true;
				}
			}
		}).setRetryTimes(3).setRetryInterval(1000L);
		this.bulkProcessor = bulkProcessorBuilder.build();
	}

	/**
	 * 获取节点信息
	 *
	 * @return 节点信息
	 */
	public String catNodes() {
		return this.esClient().executeHttp("/_cat/nodes?v", "get");
	}

	/**
	 * 获取健康信息
	 *
	 * @return 健康信息
	 */
	public String health() {
		return this.esClient().executeHttp("/_cat/health?v", "get");
	}

	/**
	 * 创建索引
	 *
	 * @param indexName 索引名称
	 * @param mapping   映射
	 * @return
	 */
	public ResponseResult createIndex(String indexName, String mapping) {
		ClientInterface clientUtil = this.esClient();
		if (!clientUtil.existIndice(indexName)) {
			String result = clientUtil.createIndiceMapping(indexName, mapping);
			return ResponseResult.success(result);
		} else {
			return ResponseResult.fail("创建索引失败。");
		}
	}

	/**
	 * 创建索引
	 *
	 * @param indexName 索引名称
	 * @param dslName   dsl名称
	 * @param esmapper  mapper xml文件路径
	 * @return
	 */
	public ResponseResult createIndex(String indexName, String dslName, String esmapper) {
		ClientInterface clientUtil = this.esConfigClient(esmapper);
		if (!clientUtil.existIndice(indexName)) {
			String result = clientUtil.createIndiceMapping(indexName, dslName);
			return ResponseResult.success(result);
		} else {
			return ResponseResult.fail("创建索引失败。");
		}
	}

	/**
	 * 删除索引
	 *
	 * @param indexName 索引名称
	 * @return
	 */
	public ResponseResult deleteIndex(String indexName) {
		ClientInterface clientUtil = this.esClient();
		if (clientUtil.existIndice(indexName)) {
			String result = clientUtil.dropIndice(indexName);
			return ResponseResult.success(result);
		} else {
			return ResponseResult.fail("删除索引失败，" + indexName + "索引不存在。");
		}
	}

	/**
	 * 关闭索引
	 *
	 * @param indexName 索引名称
	 * @return
	 */
	public ResponseResult closeIndex(String indexName) {
		ClientInterface clientUtil = this.esClient();
		if (clientUtil.existIndice(indexName)) {
			String result = clientUtil.closeIndex(indexName);
			return ResponseResult.success(result);
		} else {
			return ResponseResult.fail("关闭索引失败，" + indexName + "索引不存在。");
		}
	}

	/**
	 * 开启索引
	 *
	 * @param indexName 索引名称
	 * @return
	 */
	public ResponseResult openIndex(String indexName) {
		ClientInterface clientUtil = this.esClient();
		if (clientUtil.existIndice(indexName)) {
			String result = clientUtil.openIndex(indexName);
			return ResponseResult.success(result);
		} else {
			return ResponseResult.fail("打开索引失败，" + indexName + "索引不存在。");
		}
	}

	/**
	 * 索引是否存在
	 *
	 * @param indexName 索引名称
	 * @return
	 */
	public boolean existIndex(String indexName) {
		return this.esClient().existIndice(indexName);
	}

	/**
	 * 为索引添加别名
	 *
	 * @param indexName 索引名
	 * @param alias     别名
	 * @return
	 */
	public ResponseResult addAlias(String indexName, String alias) {
		ClientInterface clientUtil = this.esClient();
		if (clientUtil.existIndice(indexName)) {
			String result = clientUtil.addAlias(indexName, alias);
			return ResponseResult.success(result);
		} else {
			return ResponseResult.fail("建立别名失败，" + indexName + "索引不存在");
		}
	}

	/**
	 * 索引移除别名
	 *
	 * @param indexName 索引名
	 * @param alias     别名
	 * @return
	 */
	public ResponseResult removeAlias(String indexName, String alias) {
		ClientInterface clientUtil = this.esClient();
		if (clientUtil.existIndice(indexName)) {
			String result = clientUtil.removeAlias(indexName, alias);
			return ResponseResult.success(result);
		} else {
			return ResponseResult.fail("删除索引别名失败，" + indexName + "索引不存在");
		}
	}

	/**
	 * 索引是否存在别名
	 *
	 * @param indexName 索引名
	 * @param alias     别名
	 * @return
	 */
	public ResponseResult existAlias(String indexName, String alias) {
		ClientInterface clientUtil = this.esClient();
		if (clientUtil.existIndice(indexName)) {
			String aliasStr = clientUtil.executeHttp("/" + indexName + "/_alias", "get");
			return ResponseResult.success(aliasStr.contains(alias));
		} else {
			return ResponseResult.fail("判断索引别名是否存在失败，" + indexName + "索引不存在");
		}
	}

	/**
	 * 获取所有(open)索引
	 *
	 * @return 所有(open)索引
	 */
	public List<String> getIndices() {
		List<String> indices = new ArrayList();
		List<ESIndice> indiceList = this.esClient().getIndexes();
		if (indiceList != null && !indiceList.isEmpty()) {
			Iterator var3 = indiceList.iterator();
			while (var3.hasNext()) {
				ESIndice esIndice = (ESIndice) var3.next();
				boolean exist = "open".equals(esIndice.getStatus()) && !esIndice.getIndex().startsWith(".") && !FILTER_INDEXS.contains(esIndice.getIndex());
				if (exist) {
					indices.add(esIndice.getIndex());
				}
			}
		}
		return indices;
	}

	/**
	 * 根据正则获取索引
	 *
	 * @param indicePattern 索引正则格式
	 * @return
	 */
	public List<ESIndice> getIndexes(String indicePattern) {
		List<ESIndice> list = this.esClient().getIndexes(indicePattern);
		return list;
	}

	/**
	 * 保存文档
	 *
	 * @param indexName 索引名称
	 * @param object    对象(会被转JSON)
	 * @param id        文档ID
	 * @return
	 */
	public ResponseResult<String> addDocument(String indexName, Object object, String id) {
		String result = this.esClient().addDocumentWithId(indexName, object, id);
		return ResponseResult.success(result);
	}

	/**
	 * 保存文档
	 *
	 * @param indexName 索引名称
	 * @param object    对象(会被转JSON)
	 * @return
	 */
	public ResponseResult<String> addDocument(String indexName, Object object) {
		String result = this.esClient().addDocument(indexName, object);
		return ResponseResult.success(result);
	}

	/**
	 * 删除文档
	 *
	 * @param indexName 索引名称
	 * @param id        文档ID
	 * @return
	 */
	public ResponseResult<String> deleteDocument(String indexName, String id) {
		String result = this.esClient().deleteDocumentNew(indexName, id);
		return ResponseResult.success(result);
	}

	/**
	 * 删除文档
	 *
	 * @param indexName 索引名称
	 * @param ids       文档ID数组
	 * @return
	 */
	public ResponseResult<String> deleteDocuments(String indexName, String[] ids) {
		String result = this.esClient().deleteDocuments(indexName, ids);
		return ResponseResult.success(result);
	}

	/**
	 * 更新文档
	 *
	 * @param indexName 索引名称
	 * @param id        文档ID
	 * @param object    对象(会被转JSON)
	 * @return
	 */
	public ResponseResult<String> updateDocument(String indexName, String id, Object object) {
		String result = this.esClient().updateDocument(indexName, id, object);
		return ResponseResult.success(result);
	}

	/**
	 * 批量更新文档
	 *
	 * @param indexName 索引名称
	 * @param dataList  待更新数据
	 * @return
	 */
	public boolean batchUpdateData(String indexName, List<?> dataList) {
		ResponseResult<String> resultVO = this.updateDocuments(indexName, dataList);
		return ResponseResult.success().getCode().equals(resultVO.getCode());
	}

	/**
	 * 批量更新文档
	 *
	 * @param indexName 索引名称
	 * @param dataList  待更新数据
	 * @return
	 */
	public ResponseResult<String> updateDocuments(String indexName, List<?> dataList) {
		String result = this.esClient().updateDocuments(indexName, dataList);
		JSONObject jsonObject = JSONObject.parseObject(result);
		return jsonObject.getBoolean("errors") ? ResponseResult.fail(result) : ResponseResult.success(result);
	}

	/**
	 * 查询文档信息
	 *
	 * @param indexName 索引名称
	 * @param id        文档ID
	 * @return
	 */
	public String getDocument(String indexName, String id) {
		String result = this.esClient().getDocument(indexName, id);
		return result;
	}

	/**
	 * 查询文档信息，转换成指定类型
	 *
	 * @param indexName 索引名称
	 * @param id        文档ID
	 * @param clazz     转换的类型
	 * @param <T>
	 * @return
	 */
	public <T> T getDocument(String indexName, String id, Class<T> clazz) {
		return this.esClient().getDocument(indexName, id, clazz);
	}

	/**
	 * 批量保存数据
	 *
	 * @param indexName 索引名称
	 * @param dataList  待保存数据
	 * @return
	 */
	public ResponseResult<String> bulkInsertData(String indexName, List<?> dataList) {
		String result = this.esClient().addDocuments(indexName, dataList);
		JSONObject jsonObject = JSONObject.parseObject(result);
		return jsonObject.getBoolean("errors") ? ResponseResult.fail(result) : ResponseResult.success(result);
	}

	/**
	 * 根据模板删除数据
	 *
	 * @param indexName
	 * @param esmapper
	 * @param templateName
	 * @param params
	 * @param waitForCompletion
	 * @return
	 */
	public ResponseResult deleteByQueryTemplate(String indexName, String esmapper, String templateName, Map<String, Object> params, boolean waitForCompletion) {
		ClientInterface clientUtil = this.esConfigClient(esmapper);
		String path = indexName + "/_delete_by_query?&" + "scroll_size" + "=" + 10000 + "&" + "slices" + "=" + ElasticSearchConstant.DELETE_BY_QUERY_SCROLL_SLICES + "&" + "conflicts" + "=" + "proceed" + "&" + "wait_for_completion" + "=" + waitForCompletion;
		String result = clientUtil.deleteByQuery(path, templateName, params);
		JSONObject json = JSONObject.parseObject(result);
		boolean exist = json.getInteger("deleted") != null && json.getInteger("deleted") > 0 || !waitForCompletion;
		return exist ? ResponseResult.success(result) : ResponseResult.fail(result);
	}

	/**
	 * 根据模板删除数据
	 *
	 * @param searchVO
	 * @param esmapper
	 * @param templateName
	 * @param params
	 * @return
	 */
	public ResponseResult deleteByQueryTemplate(SearchVO searchVO, String esmapper, String templateName, Map params) {
		if (searchVO.getIndexNames().isEmpty()) {
			return ResponseResult.fail();
		} else {
			// searchVO转换成map条件
			Map map = this.translateSearchVO(searchVO);
			if (CollectionUtil.isNotEmpty(params)) {
				map.putAll(params);
			}
			ResponseResult<String> responseResult = this.deleteByQueryTemplate(this.getJoinedIndices(searchVO.getIndexNames()), esmapper, templateName, map, true);
			LOGGER.info("deleteByQueryTemplate code:{}", responseResult.getCode());
			return responseResult;
		}
	}

	/**
	 * 根据模板删除数据-异步
	 *
	 * @param indexName
	 * @param esmapper
	 * @param templateName
	 * @param params
	 * @return
	 */
	public ResponseResult deleteByQueryTemplateAsync(String indexName, String esmapper, String templateName, Map<String, Object> params) {
		return this.deleteByQueryTemplate(indexName, esmapper, templateName, params, false);
	}

	/**
	 * 根据模板更新
	 *
	 * @param indexName
	 * @param esmapper
	 * @param templateName
	 * @param params
	 * @param waitForCompletion
	 * @return
	 */
	public ResponseResult<String> updateByQueryTemplate(String indexName, String esmapper, String templateName, Map<String, Object> params, boolean waitForCompletion) {
		ClientInterface clientUtil = this.esConfigClient(esmapper);
		String path = indexName + "/_update_by_query?&" + "scroll_size" + "=" + 10000 + "&" + "slices" + "=" + ElasticSearchConstant.UPDATE_BY_QUERY_SCROLL_SLICES + "&" + "conflicts" + "=" + "proceed" + "&" + "wait_for_completion" + "=" + waitForCompletion;
		String result = clientUtil.updateByQuery(path, templateName, params);
		LOGGER.info("updateByQueryTemplate result:{}", result);
		JSONObject json = JSONObject.parseObject(result);
		boolean exist = json.getInteger("total") != null && json.getInteger("total") > 0 || !waitForCompletion;
		return exist ? ResponseResult.success(result) : ResponseResult.fail(result);
	}

	/**
	 * 根据模板更新
	 *
	 * @param indexName
	 * @param esmapper
	 * @param templateName
	 * @param params
	 * @param waitForCompletion
	 * @return
	 */
	public ResponseResult<String> updateByQueryTemplateNew(String indexName, String esmapper, String templateName, Map<String, Object> params, boolean waitForCompletion) {
		ClientInterface clientUtil = this.esConfigClient(esmapper);
		String path = indexName + "/_update_by_query?" + "wait_for_completion" + "=" + waitForCompletion;
		String result = clientUtil.updateByQuery(path, templateName, params);
		LOGGER.info("updateByQueryTemplate result:{}", result);
		JSONObject json = JSONObject.parseObject(result);
		boolean exist = json.getInteger("total") != null && json.getInteger("total") > 0 || !waitForCompletion;
		return exist ? ResponseResult.success(result) : ResponseResult.fail(result);
	}

	/**
	 * 根据模板更新&刷新
	 *
	 * @param indexName
	 * @param esmapper
	 * @param templateName
	 * @param params
	 * @param waitForCompletion
	 * @return
	 */
	public ResponseResult<String> updateByQueryTemplateRefresh(String indexName, String esmapper, String templateName, Map<String, Object> params, boolean waitForCompletion) {
		ClientInterface clientUtil = this.esConfigClient(esmapper);
		String path = indexName + "/_update_by_query?&" + "scroll_size" + "=" + 10000 + "&" + "slices" + "=" + ElasticSearchConstant.UPDATE_BY_QUERY_SCROLL_SLICES + "&" + "conflicts" + "=" + "proceed" + "&" + "wait_for_completion" + "=" + waitForCompletion + "&refresh";
		String result = clientUtil.updateByQuery(path, templateName, params);
		LOGGER.info("updateByQueryTemplate result:{}", result);
		return ResponseResult.success(result);
	}

	/**
	 * 根据模板更新
	 *
	 * @param searchVO
	 * @param esmapper
	 * @param templateName
	 * @param params
	 * @return
	 */
	public boolean updateByQueryTemplate(SearchVO searchVO, String esmapper, String templateName, Map params) {
		Map map = this.translateSearchVO(searchVO);
		if (searchVO.getIndexNames().isEmpty()) {
			return false;
		} else {
			if (CollectionUtil.isNotEmpty(params)) {
				map.putAll(params);
			}
			ResponseResult<String> result = this.updateByQueryTemplate(this.getJoinedIndices(searchVO.getIndexNames()), esmapper, templateName, map, true);
			LOGGER.info("updateByQueryTemplate code:{}, msg:{}", result.getCode(), result.getMessage());
			return result.isSuccess();
		}
	}

	/**
	 * 根据模板更新
	 *
	 * @param indexName
	 * @param esmapper
	 * @param templateName
	 * @param params
	 * @return
	 */
	public ResponseResult<String> updateByQueryTemplateAsync(String indexName, String esmapper, String templateName, Map<String, Object> params) {
		return this.updateByQueryTemplate(indexName, esmapper, templateName, params, false);
	}

	public BoolQueryBuilder packQueryBuilder(SearchVO vo) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		// TermMap
		if (CollectionUtil.isNotEmpty(vo.getTermMap())) {
			for (Map.Entry<String, Object> entry : vo.getTermMap().entrySet()) {
				if (entry.getValue() instanceof Collection) {
					if (vo.getFilterFlag()) {
						boolQueryBuilder.filter(QueryBuilders.termsQuery(entry.getKey(), (Collection) entry.getValue()));
					} else {
						boolQueryBuilder.must(QueryBuilders.termsQuery(entry.getKey(), (Collection) entry.getValue()));
					}
				} else if (entry.getValue() instanceof Object[]) {
					if (vo.getFilterFlag()) {
						boolQueryBuilder.filter(QueryBuilders.termsQuery(entry.getKey(), (Object[]) (entry.getValue())));
					} else {
						boolQueryBuilder.must(QueryBuilders.termsQuery(entry.getKey(), (Object[]) (entry.getValue())));
					}
				} else if (vo.getFilterFlag()) {
					boolQueryBuilder.filter(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
				} else {
					boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
				}
			}
		}
		// ShouldMap
		if (CollectionUtil.isNotEmpty(vo.getShouldMap())) {
			boolQueryBuilder.minimumShouldMatch(1);
			for (Map.Entry<String, Object> entry : vo.getShouldMap().entrySet()) {
				if (entry.getValue() instanceof Collection) {
					boolQueryBuilder.should(QueryBuilders.termsQuery(entry.getKey(), (Collection) entry.getValue()));
				} else if (entry.getValue() instanceof Object[]) {
					boolQueryBuilder.should(QueryBuilders.termsQuery(entry.getKey(), (Object[]) (entry.getValue())));
				} else {
					boolQueryBuilder.should(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
				}
			}
		}
		// NotContainField
		if (StringUtils.isNotBlank(vo.getNotContainField())) {
			boolQueryBuilder.mustNot(QueryBuilders.existsQuery(vo.getNotContainField()));
		}
		// ContainField
		if (StringUtils.isNotBlank(vo.getContainField())) {
			boolQueryBuilder.must(QueryBuilders.existsQuery(vo.getContainField()));
		}
		// WildcardMap
		if (CollectionUtil.isNotEmpty(vo.getWildcardMap())) {
			if (vo.getShouldFlag()) {
				for (Map.Entry<String, String> entry : vo.getWildcardMap().entrySet()) {
					boolQueryBuilder.should(QueryBuilders.wildcardQuery(entry.getKey(), entry.getValue()));
				}
			} else {
				for (Map.Entry<String, String> entry : vo.getWildcardMap().entrySet()) {
					boolQueryBuilder.must(QueryBuilders.wildcardQuery(entry.getKey(), entry.getValue()));
				}
			}
		}
		// TermNotMap
		if (CollectionUtil.isNotEmpty(vo.getTermNotMap())) {
			for (Map.Entry<String, Object> entry : vo.getTermNotMap().entrySet()) {
				if (entry.getValue() instanceof Collection) {
					boolQueryBuilder.mustNot(QueryBuilders.termsQuery((String) entry.getKey(), (Collection) entry.getValue()));
				} else if (entry.getValue() instanceof Object[]) {
					boolQueryBuilder.mustNot(QueryBuilders.termsQuery((String) entry.getKey(), (Object[]) ((Object[]) entry.getValue())));
				} else {
					boolQueryBuilder.mustNot(QueryBuilders.termQuery((String) entry.getKey(), entry.getValue()));
				}
			}
		}
		// MatchMap
		if (CollectionUtil.isNotEmpty(vo.getMatchMap())) {
			for (Map.Entry<String, Object> entry : vo.getMatchMap().entrySet()) {
				boolQueryBuilder.must(QueryBuilders.matchQuery((String) entry.getKey(), entry.getValue()));
			}
		}
		// RangeMap
		if (CollectionUtil.isNotEmpty(vo.getRangeMap())) {
			for (Map.Entry<String, RangeObject> entry : vo.getRangeMap().entrySet()) {
				RangeObject range = (RangeObject) entry.getValue();
				boolQueryBuilder.filter(QueryBuilders.rangeQuery((String) entry.getKey()).from(range.getStartNum()).to(range.getEndNum()));
			}
		}
		// QueryStr
		if (StringUtils.isNotBlank(vo.getQueryStr())) {
			String queryStr = vo.getQueryStr();
			try {
				String regex = "(\\s+|)\\S+:\\[\\S+-\\S+]";
				Pattern pattern = Pattern.compile(regex);
				String rangeStr;
				for (Matcher matcher = pattern.matcher(vo.getQueryStr()); matcher.find(); queryStr = queryStr.replace(rangeStr, "")) {
					rangeStr = matcher.group();
					RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(rangeStr.split(":", 2)[0].trim());
					String key = "\\[(.+?)\\]";
					Pattern p = Pattern.compile(key);
					Matcher m = p.matcher(rangeStr);
					while (m.find()) {
						String range = m.group(1);
						String rangeStart = range.split("-", 2)[0];
						String rangeEnd = range.split("-", 2)[1];
						rangeQueryBuilder.gte(rangeStart);
						rangeQueryBuilder.lte(rangeEnd);
					}
					int idx = queryStr.indexOf(rangeStr);
					if (idx > 3) {
						if (queryStr.substring(idx - 3, idx).toUpperCase().indexOf("OR") > -1) {
							boolQueryBuilder.should(rangeQueryBuilder);
						}
						if (queryStr.substring(idx - 3, idx).toUpperCase().indexOf("AND") > -1) {
							boolQueryBuilder.must(rangeQueryBuilder);
						}
						queryStr = queryStr.replace(queryStr.substring(idx - 3, idx + rangeStr.length()), "");
					}
					boolQueryBuilder.must(rangeQueryBuilder);
				}
			} catch (Exception var15) {
				var15.printStackTrace();
			}
			if (null != queryStr && StringUtils.isNotBlank(queryStr.trim())) {
				queryStr = this.decodeXss(queryStr);
				QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(queryStr);
				if (!vo.getQueryStrFields().isEmpty()) {
					queryBuilder.fields(vo.getQueryStrFields());
				}

				boolQueryBuilder.must(queryBuilder);
			}
		}
		// PrefixMap
		if (CollectionUtil.isNotEmpty(vo.getPrefixMap())) {
			for (Map.Entry<String, String> entry : vo.getPrefixMap().entrySet()) {
				boolQueryBuilder.must(QueryBuilders.prefixQuery((String) entry.getKey(), (String) entry.getValue()));
			}
		}

		Map map;
		BoolQueryBuilder mustBoolBuilder;
		List mustAndShouldMapList;
		Iterator var23;
		Iterator var24;
		Map.Entry entry;
		Collection c;
		boolean isIntType;
		Iterator iter;
		Object next;
		if (CollectionUtil.isNotEmpty(vo.getMustAndShouldMapList())) {
			mustAndShouldMapList = vo.getMustAndShouldMapList();

			label208:
			for (var23 = mustAndShouldMapList.iterator(); var23.hasNext(); boolQueryBuilder.must(mustBoolBuilder)) {
				map = (Map) var23.next();
				mustBoolBuilder = QueryBuilders.boolQuery();
				var24 = map.entrySet().iterator();

				while (true) {
					while (true) {
						if (!var24.hasNext()) {
							continue label208;
						}
						entry = (Map.Entry) var24.next();
						String key = ((String) entry.getKey()).split("-")[0];
						if (entry.getValue() instanceof Collection) {
							c = (Collection) entry.getValue();
							isIntType = false;
							if (CollectionUtil.isNotEmpty(c)) {
								iter = c.iterator();

								while (iter.hasNext()) {
									next = iter.next();
									if (next instanceof Integer) {
										isIntType = true;
									}
								}
							}

							if (isIntType) {
								mustBoolBuilder.should(QueryBuilders.termsQuery(key, (Collection) entry.getValue()));
							} else {
								mustBoolBuilder.should(QueryBuilders.termsQuery(key + "keyword", (Collection) entry.getValue()));
							}
						} else if (entry.getValue() instanceof Object[]) {
							mustBoolBuilder.should(QueryBuilders.termsQuery(key + "keyword", (Object[]) ((Object[]) entry.getValue())));
						} else if (entry.getValue() instanceof Integer) {
							mustBoolBuilder.should(QueryBuilders.termQuery(key, entry.getValue()));
						} else {
							mustBoolBuilder.should(QueryBuilders.termQuery(key + "keyword", entry.getValue()));
						}
					}
				}
			}
		}

		if (CollectionUtil.isNotEmpty(vo.getShouldAndMustMapList())) {
			mustAndShouldMapList = vo.getMustAndShouldMapList();

			label182:
			for (var23 = mustAndShouldMapList.iterator(); var23.hasNext(); boolQueryBuilder.should(mustBoolBuilder)) {
				map = (Map) var23.next();
				mustBoolBuilder = QueryBuilders.boolQuery();
				var24 = map.entrySet().iterator();

				while (true) {
					while (true) {
						if (!var24.hasNext()) {
							continue label182;
						}

						entry = (Map.Entry) var24.next();
						String key = ((String) entry.getKey()).split("-")[0];
						if (entry.getValue() instanceof Collection) {
							c = (Collection) entry.getValue();
							isIntType = false;
							if (CollectionUtil.isNotEmpty(c)) {
								iter = c.iterator();

								while (iter.hasNext()) {
									next = iter.next();
									if (next instanceof Integer) {
										isIntType = true;
									}
								}
							}

							if (isIntType) {
								mustBoolBuilder.must(QueryBuilders.termsQuery(key, (Collection) entry.getValue()));
							} else {
								mustBoolBuilder.must(QueryBuilders.termsQuery(key + "keyword", (Collection) entry.getValue()));
							}
						} else if (entry.getValue() instanceof Object[]) {
							mustBoolBuilder.must(QueryBuilders.termsQuery(key + "keyword", (Object[]) ((Object[]) entry.getValue())));
						} else if (entry.getValue() instanceof Integer) {
							mustBoolBuilder.must(QueryBuilders.termQuery(key, entry.getValue()));
						} else {
							mustBoolBuilder.must(QueryBuilders.termQuery(key + "keyword", entry.getValue()));
						}
					}
				}
			}
		}

		return boolQueryBuilder;
	}

	private String decodeXss(String str) {
		if (StringUtils.isNotBlank(str)) {
			str = str.replaceAll("&lt;", "<");
			str = str.replaceAll("&gt;", ">");
			str = str.replaceAll("&prime;", "'");
			str = str.replaceAll("&quot;", "\"");
			str = str.replaceAll("&lt;", "<");
			str = str.replaceAll("&#61;", "=");
			str = str.replaceAll("”", "\"");
			str = str.replaceAll("：", ":");
			str = str.replaceAll("【", "[");
			str = str.replaceAll("】", "]");
			str = str.replaceAll("￥", "\\\\");
		}
		return str;
	}

	/**
	 * 索引名称，逗号分割
	 *
	 * @param indexNames 索引列表
	 * @return
	 */
	public String getJoinedIndices(List<String> indexNames) {
		if (indexNames.size() > 0) {
			Set<String> indexNamesSet = new LinkedHashSet();
			indexNamesSet.addAll(indexNames);
			return Joiner.on(',').join(indexNamesSet);
		} else {
			return "_all";
		}
	}

	/**
	 * searchVO转换成Map条件
	 *
	 * @param searchVO
	 * @return
	 */
	public Map translateSearchVO(SearchVO searchVO) {
		Map conditions = new HashMap();
		Map shoulds = new HashMap();
		// 查询条件
		Map<String, Object> term = new HashMap();
		Map<String, Object> terms = new HashMap();
		Map<String, Object> range = new HashMap();
		Map<String, Object> match = new HashMap();
		Map<String, Object> termShould = new HashMap();
		Map<String, Object> termsShould = new HashMap();
		// TermMap
		if (CollectionUtil.isNotEmpty(searchVO.getTermMap())) {
			for (Map.Entry<String, Object> entry : searchVO.getTermMap().entrySet()) {
				if (entry.getValue() instanceof Collection) {
					terms.put(entry.getKey(), (Collection) entry.getValue());
				} else if (entry.getValue() instanceof Object[]) {
					terms.put(entry.getKey(), (Object[]) ((Object[]) entry.getValue()));
				} else {
					term.put(entry.getKey(), entry.getValue());
				}
			}

		}
		// ShouldMap
		if (CollectionUtil.isNotEmpty(searchVO.getShouldMap())) {
			for (Map.Entry<String, Object> entry : searchVO.getShouldMap().entrySet()) {
				if (entry.getValue() instanceof Collection) {
					termsShould.put(entry.getKey(), (Collection) entry.getValue());
				} else if (entry.getValue() instanceof Object[]) {
					termsShould.put(entry.getKey(), (Object[]) ((Object[]) entry.getValue()));
				} else {
					termShould.put(entry.getKey(), entry.getValue());
				}
			}
		}
		// MatchMap
		if (CollectionUtil.isNotEmpty(searchVO.getMatchMap())) {
			for (Map.Entry<String, Object> entry : searchVO.getMatchMap().entrySet()) {
				match.put(entry.getKey(), entry.getValue());
			}
		}
		// RangeMap
		if (CollectionUtil.isNotEmpty(searchVO.getRangeMap())) {
			for (Map.Entry<String, RangeObject> entry : searchVO.getRangeMap().entrySet()) {
				Map map = new HashMap();
				map.put("from", entry.getValue().getStartNum());
				map.put("to", entry.getValue().getEndNum());
				range.put(entry.getKey(), map);
			}
		}
		// 组装条件
		conditions.put("range", range);
		conditions.put("term", term);
		conditions.put("terms", terms);
		conditions.put("match", match);
		shoulds.put("term", termShould);
		shoulds.put("terms", termsShould);
		Map params = new HashMap();
		// 查询参数
		params.put("conditions", conditions);
		params.put("shoulds", shoulds);
		params.put("includes", searchVO.getIncludes());
		return params;
	}


}
