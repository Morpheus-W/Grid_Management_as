package com.cn7782.management.android.activity.bean;
import java.util.List;

public class LocateRequestBean
{
	public String startStrTime;
	
	public String endStrTime;
	
	public String length;
	
	public String duration;
	
	public String pathType;
	
	public List<LocateRequest> locList;

	public String getStartStrTime()
	{
		return startStrTime;
	}

	public void setStartStrTime(String startStrTime)
	{
		this.startStrTime = startStrTime;
	}

	public String getEndStrTime()
	{
		return endStrTime;
	}

	public void setEndStrTime(String endStrTime)
	{
		this.endStrTime = endStrTime;
	}

	public String getLength()
	{
		return length;
	}

	public void setLength(String length)
	{
		this.length = length;
	}

	public String getDuration()
	{
		return duration;
	}

	public void setDuration(String duration)
	{
		this.duration = duration;
	}

	public String getPathType()
	{
		return pathType;
	}

	public void setPathType(String pathType)
	{
		this.pathType = pathType;
	}

	public List<LocateRequest> getLocList() {
		return locList;
	}

	public void setLocList(List<LocateRequest> locList) {
		this.locList = locList;
	}

}
