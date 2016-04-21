package com.channelsoft.qnutil.util;

import com.channelsoft.qnutil.base.kq.KqEntity;
import com.channelsoft.qnutil.controller.KqController;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * <dl>
 * <dd>Description:</dd>
 * <dd>Copyright: Copyright (C) 2006</dd>
 * <dd>Company: 青牛（北京）技术有限公司</dd>
 * <dd>CreateDate: 2015/7/9</dd>
 * </dl>
 *
 * @author 安宁
 */
public class WorkingDayUtil {
    public static final String JUDGE_URL = "http://www.easybots.cn/api/holiday.php?d=%s";

    public static TreeMap<String, KqEntity> getStanderdDate(String startTime, String endTime){
        TreeMap<String,KqEntity> resultMap = new TreeMap<String, KqEntity>();
        Calendar p_start = Calendar.getInstance();
        p_start.setTime(CoreDateUtils.parseDate(startTime));
        Calendar p_end = Calendar.getInstance();
        p_end.setTime(CoreDateUtils.parseDate(endTime));
        List<Date> dates = getDates(p_start, p_end);
        for (Date d : dates){
            String day = CoreDateUtils.formatDate(d);
            String startTimeStr = day + " 09:00:00";
            String endTimeStr = day + " 18:00:00";
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
    public static List<Date> getDates(Calendar p_start, Calendar p_end) {
        List<Date> result = new ArrayList<Date>();
        while (p_start.before(p_end)) {
            result.add(p_start.getTime());
            p_start.add(Calendar.DAY_OF_YEAR, 1);
        }
        result.add(p_start.getTime());
        return getTrueList(result);
    }
    public static List<Date> getTrueList(List<Date> dateList){
        Map<String, Date> map = new HashMap<String, Date>();
        for(Date date : dateList){
            String dStr = CoreDateUtils.formatDate(date,"yyyyMMdd");
            map.put(dStr,date);
        }
        Set<String> dStrSet = map.keySet();
        String join = StringUtils.join(dStrSet, ",");
        String result = KqController.URLGetWithHeaderParams(String.format(JUDGE_URL, join), null, null, "GBK");
        System.out.print(result);
        JSONObject jsonObject = JSONObject.fromObject(result);
        for (Object key : jsonObject.keySet()){
            String value = jsonObject.getString(String.valueOf(key));
            if(!"0".equals(value)){
                map.remove(key);
            }
        }
        return new ArrayList<Date>(map.values());
    }
}
