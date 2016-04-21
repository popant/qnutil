package com.channelsoft.qnutil.base.kq;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KqResult{
	private String resultName;
	private int resultCount = 0;
	private List<Date> happenTimeList = new ArrayList<Date>();
	private List<Date> dateList = new ArrayList<Date>();
	private String dateStr = null;
	private long accumulationTime = 0l;
	public String getResultName() {
		return resultName;
	}
	public void setResultName(String resultName) {
		this.resultName = resultName;
	}
	
	public int getResultCount() {
		return resultCount;
	}
	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
	public List<Date> getHappenTimeList() {
		return happenTimeList;
	}
	public void setHappenTimeList(List<Date> happenTimeList) {
		this.happenTimeList = happenTimeList;
	}
	public List<Date> getDateList() {
		return dateList;
	}
	public void setDateList(List<Date> dateList) {
		this.dateList = dateList;
	}
	public long getAccumulationTime() {
		return accumulationTime;
	}
	public void setAccumulationTime(long accumulationTime) {
		this.accumulationTime = accumulationTime;
	}
	public String getDateStr() {
		return dateStr;
	}
	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	
}