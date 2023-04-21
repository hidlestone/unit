package com.wordplay.unit.starter.bbosses.model;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhuangpf
 * @date 2023-04-12
 */
@Getter
@Setter
public class SearchVO {

	private Long begin;
	private Long end;
	private String queryStr;
	private List<String> indexNames = new ArrayList();
	private List<String> typeNames = new ArrayList();
	private Integer logBigType;
	private Integer pageSize;
	private Integer pageNo;
	private String logType;
	private Map<String, Object> termMap = new HashMap();
	private Map<String, String> wildcardMap = new HashMap();
	private Map<String, Object> termNotMap = new HashMap();
	private Map<String, Object> matchMap = new HashMap();
	private Map<String, RangeObject> rangeMap = new HashMap();
	private Map<String, String> prefixMap = new HashMap();
	private Map<String, Object> shouldMap = new HashMap();
	private Integer searchType;
	private String aggregationField;
	private String cardinalityField;
	private Integer aggregationSize;
	private String subAggregationField;
	private String subCardinalityField;
	private Integer subAggregationSize;
	private Class aclass;
	private String sortField;
	private SortOrder sortOrder;
	private List<String> assetIds;
	private Long interval;
	private Boolean filterFlag = false;
	private String containField;
	private String notContainField;
	private Boolean shouldFlag = false;
	private Integer minimumShouldMatch = 1;
	private Integer maxResultSize;
	private Integer topN;
	private String[] includes;
	private String[] excludes;
	private List<SortOrderObject> sortOrderObjects = new ArrayList();
	private List<Map<String, Object>> mustAndShouldMapList;
	private List<Map<String, Object>> shouldAndMustMapList;
	private List<String> groupFields = new ArrayList();
	private List<SortBuilder<?>> sorts;
	private BoolQueryBuilder oneClickSearchParam;
	private Boolean serverIdListFlag;
	private List<Integer> serverIdList;
	private Map<String, String> regexpQueryMap;
	private Map<String, String> regexpQueryNotMap;
	private Map<String, String> wildcardNotMap = new HashMap();
	private Map<String, String> shouldLikeMap = new HashMap();
	private Map<String, List<String>> oneFiledShouldMap = new HashMap();
	private Map<String, Object> expandMap;
	private String nestedPath;
	private Map<String, Float> queryStrFields = new HashMap();
	private List<SearchVO> nestedSearchVO = new ArrayList();
	private String collapseField;

}
