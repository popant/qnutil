package com.channelsoft.qnutil.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.channelsoft.qnutil.util.WorkingDayUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.channelsoft.qnutil.base.kq.KqEntity;
import com.channelsoft.qnutil.base.kq.KqResult;
import com.channelsoft.qnutil.util.CharsetConstant;
import com.channelsoft.qnutil.util.CoreDateUtils;
import com.channelsoft.qnutil.util.CoreFetcherUtils;

@Controller
@RequestMapping("/kq")
public class KqController{
	public static final Integer nomalWorkMoney = 40;
	public static final Integer holidayWorkMoney = 100;
	public static final String loginUrl = "http://kq.channelsoft.com:49527/iclock/accounts/login/";
	public static final String indexUrl = "http://kq.channelsoft.com:49527/iclock/staff/";
	public static final String standerdDateUrl = "http://kq.channelsoft.com:49527/iclock/staff/USER_OF_RUN/?UserID__id__exact=%s&fromTime=%s&toTime=%s";
	public static final String trueDateUrl = "http://kq.channelsoft.com:49527/iclock/staff/transaction/?UserID__id__exact=%s&fromTime=%s&toTime=%s";
	public static final String specDateUrl = "http://kq.channelsoft.com:49527/iclock/staff/USER_SPEDAY/?UserID__id__exact=%s&fromTime=%s&toTime=%s";
	private final static Logger logger = LoggerFactory.getLogger(KqController.class.getName());
	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request , String userName, String password,String fromDate , String endDate){
		boolean result = handleParam(request, userName, password, fromDate, endDate);
		try {
			if(result){
				String session = login(userName, password);
				String uid = getUid(session);
				TreeMap<String, KqEntity> trueMap = getTrueDate(session, uid, fromDate, endDate);
				TreeMap<String, KqEntity> standerdMap = WorkingDayUtil.getStanderdDate(fromDate, endDate);
				TreeMap<String, KqEntity> specDateMap = getSpecDate(session, uid, fromDate, endDate);
				List<String> kqDocList = new ArrayList<String>();
				mergerTrueSpecMap(trueMap, specDateMap);//合并请假等的时间
				LinkedHashMap<String, KqResult> compareDate = compareDate(trueMap, standerdMap);
				for (Map.Entry<String, KqResult> entry : compareDate.entrySet()) {
					List<Date> dateList = entry.getValue().getDateList();
					StringBuilder sb = new StringBuilder();
					Collections.sort(dateList, new Comparator<Date>() {
						@Override
						public int compare(Date o1, Date o2) {
							return (int) (o1.getTime() - o2.getTime());
						}
					});
					for (Date date : dateList) {
						String dateStr = CoreDateUtils.formatDate(date);
						sb.append(dateStr).append(",");
					}
					StringBuilder holidaySb = new StringBuilder();
					StringBuilder nomalSb = new StringBuilder();
					handleMesg(trueMap, kqDocList, entry, dateList, holidaySb, nomalSb);
					entry.getValue().setDateStr(sb.toString());
				}
				request.setAttribute("kqDocList", kqDocList);
				request.setAttribute("kqResultMap", compareDate);
				request.setAttribute("kqDateMap", trueMap);
			}
		} catch (Exception e) {
			request.setAttribute("errorMsg", "登陆失败!(可能不是你的原因)");
			logger.error(e.getMessage(),e);
		}
		return "kq/kqList";
	}

	private void handleMesg(TreeMap<String, KqEntity> trueMap, List<String> kqDocList, Map.Entry<String, KqResult> entry, List<Date> dateList, StringBuilder holidaySb, StringBuilder nomalSb) {
		for (Date date : dateList) {
            String dateStr = CoreDateUtils.formatDate(date);
            if("加班".equals(entry.getKey())){
                KqEntity kqEntity = trueMap.get(dateStr);
                kqDocList.add(kqEntity.getWorkTimeDoc());
            }else if("假日加班".equals(entry.getKey())){
                KqEntity kqEntity = trueMap.get(dateStr);
                kqDocList.add(kqEntity.getNonWorkTimeDoc());
            }
            if("加班".equals(entry.getKey())){
                KqEntity kqEntity = trueMap.get(dateStr);
                Calendar startTimeC = kqEntity.getStartTimeC();
//                nomalSb.append(String.format("%s.%s.%s", startTimeC.get(Calendar.YEAR),startTimeC.get(Calendar.MONTH)+1,startTimeC.get(Calendar.DATE))).append("/");
                nomalSb.append(String.format("%s.%s", startTimeC.get(Calendar.MONTH)+1,startTimeC.get(Calendar.DATE))).append("/");
            }else if("假日加班".equals(entry.getKey())){
                KqEntity kqEntity = trueMap.get(dateStr);
                Calendar startTimeC = kqEntity.getStartTimeC();
//                holidaySb.append(String.format("%s.%s.%s", startTimeC.get(Calendar.YEAR),startTimeC.get(Calendar.MONTH)+1,startTimeC.get(Calendar.DATE))).append("/");
                holidaySb.append(String.format("%s.%s", startTimeC.get(Calendar.MONTH)+1,startTimeC.get(Calendar.DATE))).append("/");
            }
        }
		if("加班".equals(entry.getKey()) && nomalSb.length() > 0){
			kqDocList.add("---------------华丽的分割线------------------");
			kqDocList.add("日常加班日期：    "+ nomalSb.substring(0, nomalSb.length() - 1));
			kqDocList.add("日常加班天数：    "+ entry.getValue().getDateList().size());
			kqDocList.add("日常加班工作餐费: "+ entry.getValue().getDateList().size()*nomalWorkMoney);
		}
		if("假日加班".equals(entry.getKey()) && holidaySb.length() > 0){
			kqDocList.add("---------------华丽的分割线------------------");
			kqDocList.add("周末加班日期：    "+ holidaySb.substring(0, holidaySb.length() - 1));
			kqDocList.add("周末加班天数：    "+ entry.getValue().getDateList().size());
			kqDocList.add("周末加班工作餐费: "+ entry.getValue().getDateList().size()*holidayWorkMoney);
		}
	}

	private void handleDetailDoc(TreeMap<String, KqEntity> trueMap, List<String> kqDocList, Map.Entry<String, KqResult> entry, String dateStr) {

	}

	public void mergerTrueSpecMap(TreeMap<String, KqEntity> trueMap,TreeMap<String, KqEntity> specDateMap){
		for (String key : specDateMap.keySet()) {
			KqEntity specKqEntity = specDateMap.get(key);
			KqEntity trueKqEntity = trueMap.get(key);
			if(trueKqEntity == null){
				trueMap.put(key, specKqEntity);
			}else{
				Date specStartTime = specKqEntity.getStartTime();
				Date specEndTime = specKqEntity.getEndTime();
				Date trueStartTime = trueKqEntity.getStartTime();
				Date trueEndTime = trueKqEntity.getEndTime();
				if(specStartTime.before(trueStartTime)){
					trueKqEntity.setStartTime(specStartTime);
				}
				if(specEndTime.after(trueEndTime)){
					trueKqEntity.setEndTime(specEndTime);
				}
			}
		}
	}
	public boolean handleParam(HttpServletRequest request , String userName, String password,String fromDate , String endDate){
		boolean result = true;
		if(fromDate == null || endDate == null || fromDate.isEmpty() || endDate.isEmpty()){
			Calendar calendar=Calendar.getInstance();
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 20);
 			endDate = CoreDateUtils.formatDate(calendar.getTime());
 			request.setAttribute("endDate", endDate);
 			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)-1, 21);
 			fromDate = CoreDateUtils.formatDate(calendar.getTime());
 			request.setAttribute("fromDate", fromDate);
 			result = false;
		}
		if(userName == null || password == null || userName.isEmpty() || password.isEmpty()){
			result = false;
		}
		request.setAttribute("userName", userName);
		request.setAttribute("password", password);
		request.setAttribute("fromDate", fromDate);
		request.setAttribute("endDate", endDate);
		return result;
	}
	public static LinkedHashMap<String,KqResult> compareDate(Map<String, KqEntity> trueMap,Map<String, KqEntity> standerdMap){
		LinkedHashMap<String,KqResult> result = new LinkedHashMap<String,KqResult>();
		long overTime = 0;
		//处理考勤天数
		KqResult kqDayResult = new KqResult();
		kqDayResult.setResultName("考勤天数");
		kqDayResult.setResultCount(standerdMap.size());
		result.put("考勤天数", kqDayResult);
		for (String key : trueMap.keySet()) {
			KqEntity trueKqEntity = trueMap.get(key);
			KqEntity stKqEntity = standerdMap.remove(key);
			Date trueStartTime = trueKqEntity.getStartTime();
			Date trueEndTime = trueKqEntity.getEndTime();
			long tsl = trueStartTime.getTime();
			long tel = trueEndTime.getTime();
			//处理假日加班
			if(stKqEntity == null){
				long newOverTime = tel - tsl;
				overTime += handleResultAddTime(result, trueStartTime,newOverTime,"假日加班");
				continue;
			}
			//处理迟到
			//处理忘打卡
			Date stStartTime = stKqEntity.getStartTime();
			long stsl = stStartTime.getTime();
			if((stsl + 59000) < tsl && (stsl + 659000 ) >=  tsl){//轻微迟到
				handleResult(result, trueStartTime,"轻微迟到");
			}else if((stsl + 659000 ) <  tsl && (stsl +3659000) >= tsl){//迟到
				handleResult(result, trueStartTime,"迟到");
			}else if((stsl + 3659000) < tsl ){//忘记打卡 属于旷工状态
				handleResult(result, trueStartTime,"旷工");
			}
			
			//处理早退
			//处理忘打卡
			//处理加班
			Date stEndTime = stKqEntity.getEndTime();
			long stel = stEndTime.getTime();
			if((stel - tel) >0 && (stel - tel) <=3600000){
				handleResult(result, trueStartTime,"早退");
			}else if((stel - tel) > 3600000){//忘打卡
				handleResult(result, trueStartTime,"旷工");
			}else if(tel - stel >= 9000000){//处理加班
				overTime += handleResultAddTime(result, trueStartTime,tel - stel,"加班");
			}
		}
		//处理旷工
		for (String key : standerdMap.keySet()) {
			KqEntity kqEntity = standerdMap.get(key);
			Date startTime = kqEntity.getStartTime();
			handleResult(result, startTime,"旷工");
		}
		KqResult kqResult = new KqResult();
		kqResult.setResultName("总加班时长");
		kqResult.setAccumulationTime(overTime);
		result.put("总加班时长", kqResult);
		return result;
	}
	private static long handleResultAddTime(LinkedHashMap<String, KqResult> result, Date trueStartTime, long newOverTime , String type) {
		if(newOverTime > 3600000){//只有超过1小时的才算是加班
			KqResult kqResult = result.get(type);
			if(kqResult == null){
				kqResult = new KqResult();
				kqResult.setResultName(type);
				result.put(type, kqResult);
			}
			kqResult.setAccumulationTime(kqResult.getAccumulationTime()+newOverTime);
			kqResult.setResultCount(kqResult.getResultCount()+1);
			kqResult.getDateList().add(trueStartTime);
			return newOverTime;
		}
		return 0;
	}
	private static void handleResult(LinkedHashMap<String, KqResult> result,
			Date trueStartTime , String type) {
		KqResult kqResult = result.get(type);
		if(kqResult == null){
			kqResult = new KqResult();
			kqResult.setResultName(type);
			result.put(type, kqResult);
		}
		kqResult.setResultCount(kqResult.getResultCount()+1);
		kqResult.getDateList().add(trueStartTime);
	}
	public static TreeMap<String,KqEntity> getSpecDate(String session , String uid,String startTime ,String endTime){
		TreeMap<String,KqEntity> resultMap = new TreeMap<String, KqEntity>();
		HashMap<String, String> headerParams = new HashMap<String,String>();
		headerParams.put("Cookie", session);
		String url = String.format(specDateUrl, uid,startTime, endTime);
		String content = URLPostWithHeaderAndBodyParams(url, headerParams, new HashMap<String, String>(), new HashMap<String, String>(), CharsetConstant.CHARSET_DEFAULT);
		JSONArray array = JSONArray.fromObject(content);
		for (int i = 0,j = array.size(); i < j; i++) {
			JSONObject obj = array.getJSONObject(i);
			String state = obj.getString("State");
			if(!state.equals("已接受")){
				continue;
			}
			String startTimeStr = obj.getString("StartSpecDay");
			String endTimeStr = obj.getString("EndSpecDay");
			Date st = CoreDateUtils.parseDate(startTimeStr, CoreDateUtils.DATETIME);
			Date et = CoreDateUtils.parseDate(endTimeStr, CoreDateUtils.DATETIME);
			while(st.before(et)){
				String stStr = CoreDateUtils.formatDate(st);
				String etStr = CoreDateUtils.formatDate(et);
				if(stStr != etStr){
					KqEntity kn = new KqEntity();
					kn.setStartTime(st);
					kn.setEndTime(CoreDateUtils.parseDate(stStr+" 18:00:00",CoreDateUtils.DATETIME));
					resultMap.put(stStr, kn);
					st = CoreDateUtils.addDays(st, 1);
					st = CoreDateUtils.parseDate(CoreDateUtils.formatDate(st)+" 09:00:00");
				}else{
					KqEntity kn = new KqEntity();
					kn.setStartTime(st);
					kn.setEndTime(et);
					resultMap.put(stStr, kn);
				}
			}
		}
		return resultMap;
	}
	public static TreeMap<String,KqEntity> getStanderdDate(String session , String uid,String startTime ,String endTime){
		TreeMap<String,KqEntity> resultMap = new TreeMap<String, KqEntity>();
		HashMap<String, String> headerParams = new HashMap<String,String>();
		headerParams.put("Cookie", session);
		String url = String.format(standerdDateUrl, uid,startTime, endTime);
		String content = URLPostWithHeaderAndBodyParams(url, headerParams, new HashMap<String, String>(), new HashMap<String, String>(), CharsetConstant.CHARSET_DEFAULT);
		JSONArray array = JSONArray.fromObject(content);
		for (int i = 0,j = array.size(); i < j; i++) {
			JSONObject obj = array.getJSONObject(i);
			String startTimeStr = obj.getString("StartTime");
			String endTimeStr = obj.getString("EndTime");
			Date st = CoreDateUtils.parseDate(startTimeStr, CoreDateUtils.DATETIME);
			Date et = CoreDateUtils.parseDate(endTimeStr, CoreDateUtils.DATETIME);
			KqEntity kn = new KqEntity();
			kn.setStartTime(st);
			kn.setEndTime(et);
			String key = CoreDateUtils.formatDate(st);
			resultMap.put(key, kn);
		}
		return resultMap;
	}
	public static TreeMap<String,KqEntity> getTrueDate(String session , String uid,String startTime ,String endTime){
		TreeMap<String,KqEntity> resultMap = new TreeMap<String, KqEntity>();
		HashMap<String, String> headerParams = new HashMap<String,String>();
		headerParams.put("Cookie", session);
		String url = String.format(trueDateUrl, uid,startTime, endTime);
		String content = URLPostWithHeaderAndBodyParams(url, headerParams, new HashMap<String, String>(), new HashMap<String, String>(), CharsetConstant.CHARSET_DEFAULT);
		JSONArray array = JSONArray.fromObject(content);
		for (int i = 0,j = array.size(); i < j; i++) {
			JSONObject obj = array.getJSONObject(i);
			String time = obj.getString("TTime");
			Date st = CoreDateUtils.parseDate(time, CoreDateUtils.DATETIME);
			String key = CoreDateUtils.formatDate(st);
			KqEntity kqEntity = resultMap.get(key);
			if(kqEntity == null){
				kqEntity = new KqEntity();
				resultMap.put(key, kqEntity);
			}
			long t = st.getTime();
			Date s = kqEntity.getStartTime();
			Date e = kqEntity.getEndTime();
			if(s == null){
				kqEntity.setStartTime(st);
			}else{
				long sl = s.getTime();
				if(t<sl){
					kqEntity.setStartTime(st);
				}
			}
			if(e == null){
				kqEntity.setEndTime(st);
			}else{
				long el = e.getTime();
				if(t>el){
					kqEntity.setEndTime(st);
				}
			}
			kqEntity.getTimeList().add(st);
		}
		return resultMap;
	}
	public static String getUid(String session){
		HashMap<String, String> headerParams = new HashMap<String, String>();
		headerParams.put("Cookie", session);
		String content = URLGetWithHeaderParams(indexUrl, headerParams, new HashMap<String, String>(), CharsetConstant.CHARSET_DEFAULT);
		Pattern pa = Pattern.compile("uid=\"(\\d+)\"");
		Matcher ma = pa.matcher(content);
		if(ma.find()){
			return ma.group(1);
		}else{
			return null;
		}
	}
	public static String login(String userName ,String passward){
		Map<String ,String> param  = new HashMap<String, String>();
		param.put("username", userName);
		param.put("password", passward);
		param.put("this_is_the_login_form", "1");
		Map<String, String> urlPost = URLPost(loginUrl, param, CharsetConstant.CHARSET_DEFAULT);
		String setCookie = urlPost.get("Set-Cookie");
		String session = setCookie.split(";")[0];
		String result = urlPost.get("resultContent");
		if(!result.contains("result=2")){
			throw new RuntimeException("登陆失败！");
		}
		return session;
	}
	public static Map<String,String> URLPost(String url, Map<String, String> params,String enc) {
		String responseData = null;
		PostMethod postMethod = null;
		HttpClient client = new HttpClient();
		Map<String,String> responseMap = new HashMap<String, String>();
		try {
			postMethod = new PostMethod(url);
			postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,enc);
			postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
			if(params!=null){
				// 将表单的值放入postMethod中
				Set<String> keySet = params.keySet();
				for (String key : keySet) {
					String value = params.get(key);
					postMethod.addParameter(key, value);
				}
			}
			logger.debug("POST请求URL = " + url.toString());
			// 执行postMethod
			int statusCode = client.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK) {
				String responseEncoding = postMethod.getResponseCharSet();
				if(responseEncoding==null||responseEncoding.trim().length()==0){
					responseEncoding = enc;
				}
				// 如果响应数据编码为gb2312，则强制转换为gbk（页面数据有可能含有big5，会导致乱码）
				else if(responseEncoding.toLowerCase().equals(CharsetConstant.CHARSET_GB2312)){
					responseEncoding = CharsetConstant.CHARSET_GBK;
				}
				InputStream is = postMethod.getResponseBodyAsStream();  
				//这里的编码规则要与上面的相对应  
				BufferedReader br = new BufferedReader(new InputStreamReader(is,responseEncoding));  
				String tempbf = null;;  
				StringBuffer sb = new StringBuffer();  
				while (true) {
					tempbf = br.readLine();
					if (tempbf == null) {
						break;
					} else {
						sb.append(tempbf);  
					}
				}
				Header[] responseHeaders = postMethod.getResponseHeaders();
				if(responseHeaders != null && responseHeaders.length > 0){
					for (Header header : responseHeaders) {
						responseMap.put(header.getName(), header.getValue());
					}
				}
				responseData = sb.toString();
				logger.debug("POST请求响应内容："+responseData);
				responseMap.put("resultContent", responseData);
				br.close();
				responseEncoding = null;
				
			} else {
				logger.error("POST请求失败,请求地址:"+url+",响应状态码 :" + postMethod.getStatusCode());
			}
		} catch (HttpException e) {
			logger.error("发生致命的异常，可能是协议不对或者返回的内容有问题,post请求url:"+url);
		} catch (IOException e) {
			logger.error("发生网络异常,get请求url:"+url);
		}
		if (postMethod != null) {
			postMethod.releaseConnection();
			postMethod = null;
		}
		return responseMap;
	}
	public static String URLGetWithHeaderParams(String url, Map<String, String> headerParams,Map<String, String> params,String enc) {
		String responseData = null;
		GetMethod getMethod = null;
		StringBuffer strtTotalURL = new StringBuffer();
		String tmpParam = null;
		tmpParam = CoreFetcherUtils.getUrl(params, enc);
		if (url.indexOf("?") == -1) {
			if(tmpParam!=null&&tmpParam.trim().length()>0){
				strtTotalURL.append(url).append("?").append(tmpParam);
			}else{
				strtTotalURL.append(url);
			}
		} else {
			if(tmpParam!=null&&tmpParam.trim().length()>0){
				strtTotalURL.append(url).append("&").append(CoreFetcherUtils.getUrl(params, enc));
			}else{
				strtTotalURL.append(url);
			}
		}
		logger.debug("GET请求URL = " + strtTotalURL.toString());

		try {
			getMethod = new GetMethod(strtTotalURL.toString());
			getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,enc);
			getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
			//为了防止被抓取网站屏蔽，伪造一些信息
	        if(headerParams != null){
	            Iterator<String> itheader = headerParams.keySet().iterator();
	            while (itheader.hasNext()) {
	            	String headName = itheader.next();
	    			getMethod.addRequestHeader(headName,headerParams.get(headName).toString()); 
	            }
	        }
			// 执行getMethod
			int statusCode = new HttpClient().executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
				String responseEncoding = getMethod.getResponseCharSet();
				if(responseEncoding==null||responseEncoding.trim().length()==0){
					responseEncoding = enc;
				}
				// 如果响应数据编码为gb2312，则强制转换为gbk（页面数据有可能含有big5，会导致乱码）
				else if(responseEncoding.toLowerCase().equals(CharsetConstant.CHARSET_GB2312)){
					responseEncoding = CharsetConstant.CHARSET_GBK;
				}
				InputStream in = getMethod.getResponseBodyAsStream();  
				//这里的编码规则要与页面的相对应  
				BufferedReader br = new BufferedReader(new InputStreamReader(in,responseEncoding));  
				String tempbf = null;;  
				StringBuffer sb = new StringBuffer(100);  
				while (true) {
					tempbf = br.readLine();
					if (tempbf == null) {
						break;
					} else {
						sb.append(tempbf);  
					}
				}
				responseData = sb.toString();
				logger.debug("GET请求响应内容："+responseData);
				br.close();
				responseEncoding = null;
			} else {
				logger.error("GET请求失败,请求地址:"+strtTotalURL.toString()+",响应状态码:" + getMethod.getStatusCode());
			}
		} catch (HttpException e) {
			logger.error("发生致命的异常，可能是协议不对或者返回的内容有问题,get请求url:"+strtTotalURL.toString(),e);
		} catch (IOException e) {
			logger.error("发生网络异常,get请求url:"+strtTotalURL.toString(),e);
		}
		if (getMethod != null) {
			getMethod.releaseConnection();
			getMethod = null;
			tmpParam = null;
		}
		return responseData;
	}
	 public static String URLPostWithHeaderAndBodyParams(String url, Map<String, String> headerParams, Map<String, String> bodyParams, Map<String, String> params,String enc) {
	        String responseData = null;
	        PostMethod postMethod = null;
	        try {
	            postMethod = new PostMethod(url);
	            postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,enc);
	            postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
	            if(params!=null){
	                // 将表单的值放入postMethod中
	                Set<String> keySet = params.keySet();
	                for (String key : keySet) {
	                    String value = params.get(key);
	                    postMethod.addParameter(key, value);
	                }
	            }
	            logger.debug("POST请求URL = " + url.toString());
	            //为了防止被抓取网站屏蔽，伪造一些信息
	            if(headerParams != null){
	                Iterator<String> itheader = headerParams.keySet().iterator();
	                while (itheader.hasNext()) {
	                    String headName = itheader.next();
	                    postMethod.addRequestHeader(headName,headerParams.get(headName).toString());
	                }
	            }

	            if (bodyParams != null) {
	                NameValuePair[] bodyParamPairs = new NameValuePair[bodyParams.size()];
	                Iterator<String> itbody = bodyParams.keySet().iterator();
	                int i = 0;
	                while (itbody.hasNext()) {
	                    String bodyName = itbody.next();
	                    bodyParamPairs[i ++] = new NameValuePair(bodyName, bodyParams.get(bodyName));
	                }
	                postMethod.setRequestBody(bodyParamPairs);
	            }

	            // 执行postMethod
	            int statusCode = new HttpClient().executeMethod(postMethod);
	            if (statusCode == HttpStatus.SC_OK) {
	                String responseEncoding = postMethod.getResponseCharSet();
	                if(responseEncoding==null||responseEncoding.trim().length()==0){
	                    responseEncoding = enc;
	                }
	                // 如果响应数据编码为gb2312，则强制转换为gbk（页面数据有可能含有big5，会导致乱码）
	                else if(responseEncoding.toLowerCase().equals(CharsetConstant.CHARSET_GB2312)){
	                    responseEncoding = CharsetConstant.CHARSET_GBK;
	                }
	                InputStream is = postMethod.getResponseBodyAsStream();
	                //这里的编码规则要与上面的相对应
	                BufferedReader br = new BufferedReader(new InputStreamReader(is,responseEncoding));
	                String tempbf = null;;
	                StringBuffer sb = new StringBuffer();
	                while (true) {
	                    tempbf = br.readLine();
	                    if (tempbf == null) {
	                        break;
	                    } else {
	                        sb.append(tempbf);
	                    }
	                }
	                responseData = sb.toString();
	                logger.debug("POST请求响应内容："+responseData);
	                br.close();
	                responseEncoding = null;
	            } else {
	                logger.error("POST请求失败,请求地址:"+url+",响应状态码 :" + postMethod.getStatusCode());
	            }
	        } catch (HttpException e) {
	            logger.error("发生致命的异常，可能是协议不对或者返回的内容有问题,post请求url:"+url);
	        } catch (IOException e) {
	            logger.error("发生网络异常,get请求url:"+url);
	        }
	        if (postMethod != null) {
	            postMethod.releaseConnection();
	            postMethod = null;
	        }
	        return responseData;
	    }
//	 public static void main(String[] args) {
//		int i = 12292;
//		while (12292 > 0){
//			new MyThread(i).start();
//			try {
//				Thread.sleep(300l);
//			} catch (InterruptedException e) {
//			}
//			i--;
//		}
//	}
}
class MyThread extends Thread{
	private int i = 0;
	public MyThread(int i){
		this.i = i;
	}
	@Override
	public void run() {
		Map<String ,String> param = new HashMap<String, String>();
		param.put("userName", String.valueOf(i));
		param.put("password", String.valueOf(i));
		param.put("fromDate", "2014-03-21");
		param.put("endDate", "2014-04-20");
//		String urlPost = CoreFetcherUtils.URLPost("http://10.130.41.47:8081/db/kq/index", param, CharsetConstant.CHARSET_DEFAULT);
		String urlPost = CoreFetcherUtils.URLPost("http://10.130.40.102:8081/db/kq/index", param, CharsetConstant.CHARSET_DEFAULT);
		System.out.println(urlPost);
	}
}



