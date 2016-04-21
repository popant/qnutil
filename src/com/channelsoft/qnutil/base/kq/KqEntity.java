package com.channelsoft.qnutil.base.kq;

import com.channelsoft.qnutil.util.Arith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class KqEntity{
	private Date startTime;
	private Date endTime;
	private List<Date> timeList = new ArrayList<Date>();
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public List<Date> getTimeList() {
		return timeList;
	}
	public void setTimeList(List<Date> timeList) {
		this.timeList = timeList;
	}

	public String getWorkTimeDoc(){
		Calendar startTimeC = getStartTimeC();
		Calendar endTimeC = getEndTimeC();
		double endTimeExt = Arith.div((double) endTimeC.get(Calendar.MINUTE), 60d, BigDecimal.ROUND_HALF_UP);
		double endTime = Arith.add((double)endTimeC.get(Calendar.HOUR_OF_DAY),endTimeExt);
		return String.format("加班时间：%s年%s月%s日 %s 时 至 %s 时总共 %s  小时（倒休：○是，●否）",
				startTimeC.get(Calendar.YEAR),
				startTimeC.get(Calendar.MONTH)+1,
				startTimeC.get(Calendar.DATE),
				18,
				endTime,
				Arith.sub(endTime , 18)
		);
	}
	public String getNonWorkTimeDoc(){
		Calendar startTimeC = getStartTimeC();
		Calendar endTimeC = getEndTimeC();
		double startTimeExt = Arith.div((double) startTimeC.get(Calendar.MINUTE), 60d, BigDecimal.ROUND_HALF_UP);
		double startTime = Arith.add((double) startTimeC.get(Calendar.HOUR_OF_DAY), startTimeExt);
		double endTimeExt = Arith.div((double) endTimeC.get(Calendar.MINUTE), 60d, BigDecimal.ROUND_HALF_UP);
		double endTime = Arith.add((double)endTimeC.get(Calendar.HOUR_OF_DAY),endTimeExt);
//		double startTime = startTimeC.get(Calendar.HOUR_OF_DAY) + (double)startTimeC.get(Calendar.MINUTE);
//		double endTime = endTimeC.get(Calendar.HOUR_OF_DAY) + (double)endTimeC.get(Calendar.MINUTE) / 60;
		return String.format("加班时间：%s年%s月%s日 %s 时 至 %s 时总共 %s  小时（倒休：○是，●否）",
				startTimeC.get(Calendar.YEAR),
				startTimeC.get(Calendar.MONTH)+1,
				startTimeC.get(Calendar.DATE),
				startTime,
				endTime,
				Arith.sub(endTime,startTime)
		);
	}
	public Calendar getStartTimeC(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startTime);
		return calendar;
	}
	public Calendar getEndTimeC(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endTime);
		return calendar;
	}
}