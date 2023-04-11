package com.wordplay.unit.starter.bbosses.client;

import com.alibaba.fastjson.JSONObject;
import com.wordplay.unit.starter.api.response.ResponseResult;
import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	public ResponseResult<String> deleteByQueryTemplate(String indexName, String esmapper, String templateName, Map<String, Object> params, boolean waitForCompletion) {
		ClientInterface clientUtil = this.esConfigClient(esmapper);
		String path = indexName + "/_delete_by_query?&" + "scroll_size" + "=" + 10000 + "&" + "slices" + "=" + ElasticSearchConstant.DELETE_BY_QUERY_SCROLL_SLICES + "&" + "conflicts" + "=" + "proceed" + "&" + "wait_for_completion" + "=" + waitForCompletion;
		String result = clientUtil.deleteByQuery(path, templateName, params);
		JSONObject json = JSONObject.parseObject(result);
		boolean exist = json.getInteger("deleted") != null && json.getInteger("deleted") > 0 || !waitForCompletion;
		return exist ? ResponseResult.success(result) : ResponseResult.fail(result);
	}
	

}
