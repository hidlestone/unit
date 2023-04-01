package com.wordplay.unit.starter.data.mp.interceptor;

import com.wordplay.unit.starter.data.mp.UserThreadLocal;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * MybatisPlus拦截器保存/更新自动设置基础字段信息
 *
 * @author zhuangpf
 */
@Intercepts(value = {@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MybatisAutoSetUserIdInterceptor implements Interceptor {

	// 创建人
	private static final String CREATEUSERID = "createUserId";
	// 创建时间
	private static final String GMTCREATE = "gmtCreate";
	// 修改人
	private static final String MODIFYUSERID = "modifyUserId";
	// 修改时间
	private static final String GMTMODIFIED = "gmtModified";

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		return invokeUpdate(invocation);
	}

	private Object invokeUpdate(Invocation invocation) throws Exception {
		// 获取第一个参数
		MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
		SqlCommandType sqlCommandType = ms.getSqlCommandType();
		// 获取参数
		Object args = invocation.getArgs()[1];
		// 判断参数类型，是不是MapperMethod.ParamMap，是就循环更改，不是就是对象直接更改 
		if (args instanceof MapperMethod.ParamMap) {
			MapperMethod.ParamMap<Object> mapObj = (MapperMethod.ParamMap<Object>) invocation.getArgs()[1];
			for (Map.Entry<String, Object> obj : mapObj.entrySet()) {
				Object paramObj = mapObj.get(obj.getKey());
				Field[] fields = paramObj.getClass().getDeclaredFields();
				if (paramObj == null || fields == null) {
					return invocation.proceed();
				}
				// 如果 insert 语句  则添加创建时间创建人 修改时间和修改人
				if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
					this.setAllParams(fields, paramObj, CREATEUSERID, CREATEUSERID);
				} else if (ms.getSqlCommandType() == SqlCommandType.UPDATE) {
					// 如果是update语句  则添加修改时间修改人
					this.setAllParams(fields, paramObj, MODIFYUSERID, MODIFYUSERID);
				}
			}
		} else {
			Field[] fields = args.getClass().getDeclaredFields();
			if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
				this.setAllParams(fields, args, CREATEUSERID, CREATEUSERID);
			} else if (ms.getSqlCommandType() == SqlCommandType.UPDATE) {
				// 如果是update预计  则添加修改时间修改人
				this.setAllParams(fields, args, MODIFYUSERID, MODIFYUSERID);
			}
		}
		return invocation.proceed();
	}

	/**
	 * 根据传递参数放进行修改
	 *
	 * @param fields   反射存在的参数
	 * @param obj      需要改变的对象
	 * @param valueKey 变更的字段
	 * @param valObj   变更参数类型
	 */
	private void setAllParams(Field[] fields, Object obj, String valueKey, Object valObj) {
		Map<String, String> userInfoMap = this.getUserInfoMap();
		if (null != userInfoMap && !StringUtils.isEmpty(userInfoMap.get("userId"))) {
			for (int i = 0; i < fields.length; i++) {
				if (valueKey.toLowerCase().equals(fields[i].getName().toLowerCase())) {
					try {
						/*if (valObj instanceof Date) {
							fields[i].setAccessible(true);
							fields[i].set(obj, new Date());
							fields[i].setAccessible(false);
						}*/
						if (valObj instanceof String) {
							fields[i].setAccessible(true);
							fields[i].set(obj, userInfoMap.get("userId"));
							fields[i].setAccessible(false);
						}
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 从本地线程中获取用户信息
	 */
	private Map<String, String> getUserInfoMap() {
		return UserThreadLocal.get();
	}

}
