package com.wordplay.unit.starter.bbosses.constant;

/**
 * @author zhuangpf
 * @date 2023-04-11
 */
public class ElasticSearchConstant {

	public static final int SEARCH_MAX_SIZE = 10000;
	public static final int AGGREGATION_SIZE = 1000;
	public static final String AGGREGATION_FIELD_KEY = "key";
	public static final String AGGREGATION_FIELD_VALUE = "value";
	public static final String AGGREGATION_FIELD_DOC_COUNT = "doc_count";
	public static final String ES_RETURN_FIELD_ERRORS = "errors";
	public static final String ES_RETURN_FIELD_TOTAL = "total";
	public static final String ES_RETURN_FIELD_UPDATED = "updated";
	public static final String ES_RETURN_FIELD_DELETED = "deleted";
	public static final String INDEX_STATUS_OPEN = "open";
	public static final String SCROLL_SIZE_KEY = "scroll_size";
	public static final String SCROLL_SLICES_SIZE_KEY = "slices";
	public static final String CONFLICTS_KEY = "conflicts";
	public static final String WAIT_FOR_COMPLETION_KEY = "wait_for_completion";
	public static final int DELETE_BY_QUERY_SCROLL_SIZE = 10000;
	public static final Integer DELETE_BY_QUERY_SCROLL_SLICES = 5;
	public static final String CONFLICTS = "proceed";
	public static final boolean WAIT_FOR_COMPLETION_TRUE = true;
	public static final boolean WAIT_FOR_COMPLETION_FALSE = false;
	public static final int UPDATE_BY_QUERY_SCROLL_SIZE = 10000;
	public static final Integer UPDATE_BY_QUERY_SCROLL_SLICES = 5;
	public static final String SCROLL_TIME = "1m";
	public static final int SCROLL_SLICE_MAX = 5;
	public static final int SCROLL_SIZE = 100;
	public static final int TOPN_DEFAULT = 10;
	public static final String KEYWORD = "keyword";
	
}
