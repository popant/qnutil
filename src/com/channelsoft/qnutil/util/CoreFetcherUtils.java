package com.channelsoft.qnutil.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抓取工具类
 * @author leiming
 *
 */
public class CoreFetcherUtils {
	private static final Logger logger = LoggerFactory.getLogger(CoreFetcherUtils.class.getName());
	/**
	 * 定义编码格式 UTF-8
	 */
	public static final String URL_PARAM_CHARSET_UTF8 = "UTF-8";
	/**
	 * 定义编码格式 GBK
	 */
	public static final String URL_PARAM_CHARSET_GBK = "GBK";
	private static final String URL_PARAM_CONNECT_FLAG = "&";
	private static final String EMPTY = "";
	public static final String PLOT_REMOTE_FETCH_ERROR = "ERROR";

	private static MultiThreadedHttpConnectionManager connectionManager = null;

	private static int connectionTimeOut = 30000;
	private static int socketTimeOut = 30000;
	private static int maxConnectionPerHost = 20;
	private static int maxTotalConnections = 20;

	private static HttpClient client;

	static {
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setConnectionTimeout(connectionTimeOut);
		connectionManager.getParams().setSoTimeout(socketTimeOut);
		connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionPerHost);
		connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);
		client = new HttpClient(connectionManager);
	}

	/**
	 * POST方式提交数据
	 * @param url 待请求的URL
	 * @param params 要提交的数据
	 * @param enc 编码
	 * @return 响应结果
	 * @throws IOException IO异常
	 */
	public static String URLPost(String url, Map<String, String> params,String enc) {
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

	/**
	 * GET方式提交数据
	 * @param url 待请求的URL
	 * @param params 要提交的数据
	 * @param enc 编码
	 * @return 响应结果
	 * @throws IOException IO异常
	 */
	public static String URLGet(String url, Map<String, String> params,String enc) {
		String responseData = null;
		GetMethod getMethod = null;
		StringBuffer strtTotalURL = new StringBuffer(EMPTY);
		String tmpParam = null;
		tmpParam = getUrl(params, enc);
		if (url.indexOf("?") == -1) {
			if(tmpParam!=null&&tmpParam.trim().length()>0){
				strtTotalURL.append(url).append("?").append(tmpParam);
			}else{
				strtTotalURL.append(url);
			}
		} else {
			if(tmpParam!=null&&tmpParam.trim().length()>0){
				strtTotalURL.append(url).append("&").append(getUrl(params, enc));
			}else{
				strtTotalURL.append(url);
			}
		}
		logger.debug("GET请求URL = " + strtTotalURL.toString());

		try {
			getMethod = new GetMethod(strtTotalURL.toString());
			getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,enc);
			getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30000);
			// 执行getMethod
			int statusCode = client.executeMethod(getMethod);
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

    /**
     * 带参数头的POST方式提交数据
     * @param url 待请求的URL
     * @param headerParams 请求头的参数
     * @param params 要提交的数据
     * @param enc 编码
     * @return 响应结果
     * @throws IOException IO异常
     */
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

	/**
	 * 带参数头的POST方式提交数据
	 * @param url 待请求的URL
	 * @param headerParams 请求头的参数
	 * @param params 要提交的数据
	 * @param enc 编码
	 * @return 响应结果
	 * @throws IOException IO异常
	 */
	public static String URLPostWithHeaderParams(String url, Map<String, String> headerParams, Map<String, String> params,String enc) {
        return URLPostWithHeaderAndBodyParams(url, headerParams, null, params, enc);
    }
	/**
	 * 带参数头的GET方式提交数据,伪造抓取身份
	 * @param url 待请求的URL
	 * @param headerParams 消息头参数
	 * @param params 要提交的数据
	 * @param enc 编码
	 * @return 响应结果
	 * @throws IOException IO异常
	 */
	public static String URLGetWithHeaderParams(String url, Map<String, String> headerParams,Map<String, String> params,String enc) {
		String responseData = null;
		GetMethod getMethod = null;
		StringBuffer strtTotalURL = new StringBuffer(EMPTY);
		String tmpParam = null;
		tmpParam = getUrl(params, enc);
		if (url.indexOf("?") == -1) {
			if(tmpParam!=null&&tmpParam.trim().length()>0){
				strtTotalURL.append(url).append("?").append(tmpParam);
			}else{
				strtTotalURL.append(url);
			}
		} else {
			if(tmpParam!=null&&tmpParam.trim().length()>0){
				strtTotalURL.append(url).append("&").append(getUrl(params, enc));
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
			int statusCode = client.executeMethod(getMethod);
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

	/**
	 * 据Map生成URL字符串
	 * @param map Map
	 * @param valueEnc URL编码
	 * @return URL
	 */
	public static String getUrl(Map<String, String> map, String valueEnc) {
		if (null == map || map.keySet().size() == 0) {
			return (EMPTY);
		}
		StringBuffer url = new StringBuffer();
		Set<String> keys = map.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String key = it.next();
			if (map.containsKey(key)) {
				String val = map.get(key);
				String str = val != null ? val : EMPTY;
				try {
					url.append(URLEncoder.encode(key, valueEnc));
					url.append("=");
					url.append(URLEncoder.encode(str, valueEnc));
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage(), e);
					e.printStackTrace();
					continue;
				}
				url.append(URL_PARAM_CONNECT_FLAG);
			}
		}
		String strURL = EMPTY;
		strURL = url.toString();
		if (URL_PARAM_CONNECT_FLAG.equals(EMPTY
				+ strURL.charAt(strURL.length() - 1))) {
			strURL = strURL.substring(0, strURL.length() - 1);
		}
		return (strURL);
	}
	/**
	 * 解析htmlData内第tableIndexNumber个表格的第tableRow行所有的列指定格式(1txt | 0html)内容
	 * @param htmlData html格式的数据
	 * @param tableIndexNumber 从0开始
	 * @param tableRow 从0开始
	 * @param encoding
	 * @param txtFlag 1为文本 0为html
	 * @return
	 */
	public static String[] getContentFromTableRow(String htmlData,int tableIndexNumber,int tableRow,String encoding,int txtFlag){
		
		Parser parser = null;
		String result[] = null;
		try {
			parser = Parser.createParser(htmlData, encoding);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		} 
		NodeFilter tableFilter = new TagNameFilter("table");
		
		try{
			NodeList tableNodeList = parser.parse(tableFilter);
			TableTag catchTableTag=new TableTag();
			catchTableTag = (TableTag)tableNodeList.elementAt(tableIndexNumber);
			if(catchTableTag!=null){
				TableRow[] catchRows = catchTableTag.getRows();
				TableColumn[] catchColumn = catchRows[tableRow].getColumns();
				if(catchColumn.length>0){
					result = new String[catchColumn.length];
					for(int i = 0;i<catchColumn.length;i++){
						if(txtFlag==1){
							result[i] = catchColumn[i].toPlainTextString().trim();
						}else{
							result[i] = catchColumn[i].toHtml();
						}
						
					}
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			result = null;
		}
		return result;
	}
	/**
	 * 解析htmlData内第tableIndexNumber个表格的第tableRow行所有的列指定格式(1txt | 0html)内容
	 * 必须正规m * n的正规表格
	 * @param htmlData html格式的数据
	 * @param tableIndexNumber 从0开始
	 * @param encoding
	 * @param txtFlag 1为文本 0为html
	 * @return
	 */
	public static String[][] getTableContentFromHtmlTable(String htmlData,int tableIndexNumber,String encoding,int txtFlag){
		
		Parser parser = null;
		String result[][] = null;
		try {
			parser = Parser.createParser(htmlData, encoding);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		} 
		NodeFilter tableFilter = new TagNameFilter("table");
		try{
			NodeList tableNodeList = parser.parse(tableFilter);
			TableTag catchTableTag=new TableTag();
			catchTableTag = (TableTag)tableNodeList.elementAt(tableIndexNumber);
			if(catchTableTag!=null){
				TableRow[] catchRows = catchTableTag.getRows();
				TableColumn[] catchColumn = catchRows[0].getColumns();
				result = new String[catchRows.length][catchColumn.length];
				for(int k =0;k<catchRows.length;k++ ){
					catchColumn = catchRows[k].getColumns();
					for(int i = 0;i<catchColumn.length;i++){
						if(txtFlag==1){
							result[k][i] = catchColumn[i].toPlainTextString().trim();
						}else{
							result[k][i] = catchColumn[i].toHtml();
						}
					}
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			result = null;
		}
		return result;
	}
	/**
	 * 解析htmlData内表格第tableColumn列内容为keyValue的所在行的所有列数据
	 * @param htmlData
	 * @param tableIndexNumber
	 * @param tableColumn 从0开始
	 * @param keyValue
	 * @param encoding
	 * @return
	 */
	public static String[] getHtmlTableRowDataByColumnKey(String htmlData,int tableIndexNumber,int tableColumn,String keyValue,String encoding){
		Parser parser = null;
		String result[] = null;
		try {
			parser = Parser.createParser(htmlData, encoding);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
		NodeFilter tableFilter = new TagNameFilter("table");
		
		try{
			NodeList tableNodeList = parser.parse(tableFilter);
			TableTag catchTableTag=new TableTag();
			catchTableTag = (TableTag)tableNodeList.elementAt(tableIndexNumber);
			if(catchTableTag!=null){
				TableRow[] catchRows = catchTableTag.getRows();
				TableColumn[] catchColumn = null;
				if (catchRows.length > 0) {
					for (int i = 0; i < catchRows.length; i++) {
						catchColumn = catchRows[i].getColumns();
						if(catchColumn.length>0&&tableColumn<=catchColumn.length&&catchColumn[tableColumn].toPlainTextString().trim().equals(keyValue)){
							result = new String[catchColumn.length];
							for(int j = 0;j<catchColumn.length;j++){
								result[j] = catchColumn[j].toPlainTextString().trim();
							}
							break;
						}
					}
				}
			}//end 表格元素非空
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			result = null;
		}
		return result;
	}
	/**
	 * 获得html内容里的li数据内容，返回数组
	 * @param htmlData
	 * @param encoding
	 * @return
	 */
	public static String[] getLiDataByHtml(String htmlData,String encoding){
		Parser parser = null;
		String result[] = null;
		try {
			parser = Parser.createParser(htmlData, encoding);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
		NodeFilter liFilter = new TagNameFilter("li");
		
		try{
			NodeList liNodeList = parser.parse(liFilter);
			if(liNodeList!=null&&liNodeList.size()>0){
				result = new String[liNodeList.size()];
				for(int i=0;i<liNodeList.size();i++){
					result[i] = liNodeList.elementAt(i).toPlainTextString().trim();
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			result = null;
		}
		return result;
	}
	/**
	 * 获得html内容里的被选中的option
	 * @param htmlData
	 * @param encoding
	 * @return
	 */
	public static OptionTag getSelectedOptionByHtml(String htmlData,String encoding){
		Parser parser = null;
		OptionTag optionNode = null;
		OptionTag selectedOptionNode = null;
		OptionTag firstOptionNode = null;
		try {
			parser = Parser.createParser(htmlData, encoding);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return null;
		}
		NodeFilter optionFilter = new TagNameFilter("option");
		try{
			NodeList optionNodeList = parser.parse(optionFilter);
			if(optionNodeList!=null&&optionNodeList.size()>0){
				for(int i=0;i<optionNodeList.size();i++){
					if(optionNodeList.elementAt(i) instanceof OptionTag){
						if(i==0){
							firstOptionNode = (OptionTag)optionNodeList.elementAt(0);;
						}
						optionNode = (OptionTag)optionNodeList.elementAt(i);
						if(optionNode.getAttributeEx("selected")!=null){
							selectedOptionNode = optionNode; 
							break;
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("解析option发生错误"+e.getMessage(),e);
			optionNode = null;
		}
		//不存在默认选择选项默认选择第一个
		if(selectedOptionNode==null){
			selectedOptionNode = firstOptionNode;
		}
		return selectedOptionNode;
	}
	/**
	 * 根据500万详细页面首行解析彩票名称，彩期号和开奖时间
	 * @param data 带解析数据
	 * @param logHeader 日志头
	 * @param phase 彩期号，当前期为null
	 * @return String[] 0:lotteryName,1:lotteryPhase,2:timeDraw
	 */
	public static String[] parser500WanDrawInfo(String data,String logHeader,String phase){
		String[] result = null;
		if (data!=null&&(!data.isEmpty())) {
			String lotteryName = null;
			String lotteryPhase = null;
			String timeDraw = null;
			if(data.indexOf("期")>0){
				String lotteryPhaseBody = data.split("期")[0];
				if(lotteryPhaseBody.indexOf("第")>0){
					lotteryName = lotteryPhaseBody.split("第")[0].trim();
					lotteryPhase = lotteryPhaseBody.split("第")[1].trim();
					if(lotteryPhase==null||lotteryPhase.isEmpty()){
						logger.error(logHeader+"解析的彩期号不存在，返回null==");
						return null;
					}
					if(phase!=null&&phase.trim().length()>0&&(!lotteryPhase.equals(phase))){
						logger.error(logHeader+"指定抓取的彩期号({})与页面抓取的彩期号({})不一致,返回null==",phase,lotteryPhase);
						return null;
					}
				}else{
					logger.error(logHeader+"解析期号发生错误，页面无'第'字==");
					return null;
				}
			}else{
				logger.error(logHeader+"解析期号发生错误，页面无'期'字==");
				return null;
			}
			logger.info(logHeader+"解析"+lotteryName+"期号为:"+lotteryPhase);
			//开奖时间   备注：500万双色球是YYYY年MM月DD日格式 其余格式是2008-8-8
			if(data.indexOf("兑奖截止")>-1){
				String timeDrawBody = data.split("兑奖截止")[0];
				if(timeDrawBody.indexOf("开奖日期：")>-1){
					timeDraw = timeDrawBody.split("开奖日期：")[1].trim();
				}else{
					logger.error(logHeader+"解析开奖日期发生错误，页面无'开奖日期：'字==");
					return null;
				}
			}else{
				logger.error(logHeader+"解析开奖日期发生错误，页面无'兑奖截止'字==");
				return null;
			}
			logger.info(logHeader+"解析开奖日期为:"+timeDraw);
			result = new String[3];
			result[0] = lotteryName;
			result[1] = lotteryPhase;
			result[2] = timeDraw;
		}
		return result;
	}
	/**
	 * 格式化金钱字符串，形如：￥8,162,781.00的字符串为8162781
	 * @param moneyStr
	 * @return
	 */
	public static String formatMoneyString(String moneyStr){
		String money = null;
		if(moneyStr!=null){
			moneyStr = moneyStr.replaceAll("￥", "");
			if(moneyStr.indexOf(".")>-1){
				money = moneyStr.split("\\.")[0];
			}else{
				money = moneyStr;
			}
			money = money.replaceAll(",", "");
		}
		return money;
	}
	/**
	 * 格式化奖期，去掉字符串中的"期"字
	 * @param term
	 * @return
	 */
	public static String formatLotteryTerm(String term){
		if(term!=null){
			term = term.replaceAll("期", "");
			return term;
		}else{
			return null;
		}
	}
	/**
	 * 以指定字符合并数组内容
	 * @param arrayStr
	 * @param symbol
	 * @return
	 */
	public static String mergeStringArray(String[] arrayStr,String symbol){
		if(arrayStr==null||arrayStr.length==0||symbol==null){
			return null;
		}
		StringBuffer sb = new StringBuffer("");
		for(int i = 0;i<arrayStr.length;i++){
			sb.append(arrayStr[i]);
			if(i<(arrayStr.length-1)){
				sb.append(symbol);
			}
		}
		return sb.toString();
	}
	/**
	 * 格式化日期，
	 * 如果是yyyy年MM月dd日格式 转化为 yyyy-MM-dd
	 * 否则原样返回
	 * @param timeDraw
	 * @return
	 */
	public static String formatTimeDraw(String timeDraw){
		if(timeDraw==null){
			return null;
		}
		String tmp = timeDraw;
		if(timeDraw.indexOf("年")>0&&timeDraw.indexOf("月")>0&&timeDraw.indexOf("日")>0){
			DateFormat shortFormat = new SimpleDateFormat("yyyy年MM月dd日");
			DateFormat normalFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try{
				date = shortFormat.parse(timeDraw);
				tmp = normalFormat.format(date);
			}catch(Exception e){
				logger.error("转换日期字符串"+timeDraw+"错误,原样返回",e);
				return tmp;
			}
		}
		return tmp;
	}
}
