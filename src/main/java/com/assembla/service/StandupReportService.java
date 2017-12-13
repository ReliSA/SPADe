package com.assembla.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.assembla.StandupReport;
import com.assembla.client.AssemblaClient;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.utils.ValidationUtils;

public class StandupReportService extends AbstractBaseService implements StandupReportResource {
	public StandupReportService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.StandupReportResource#getAll(java.time.LocalDate, java.time.LocalDate)
	 */
	@Override
	public List<StandupReport> getAll(LocalDate from, LocalDate to) {
		ValidationUtils.notNull(from, "from == null");
		ValidationUtils.notNull(to, "to == null");

		String uri = String.format("/spaces/%s/standup_reports.json", this.spaceId);
		LocalDateTime fromDT = LocalDateTime.of(from, LocalTime.MIDNIGHT);
		LocalDateTime toDT = LocalDateTime.of(to, LocalTime.MIDNIGHT);
		DateRange range = new DateRange(fromDT, toDT, DateTimeFormatter.ISO_LOCAL_DATE);

		AssemblaRequest request = new AssemblaRequest(uri, StandupReport[].class);
		request.addParam("from", range.getFromString());
		request.addParam("to", range.getToString());
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.StandupReportResource#get(java.time.LocalDate)
	 */
	@Override
	public StandupReport get(LocalDate date) {
		ValidationUtils.notNull(date, "date == null");

		String uri = String.format("/spaces/%s/standup_report.json", this.spaceId);
		LocalDateTime asDateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
		DateRange range = new DateRange(asDateTime, asDateTime, DateTimeFormatter.ISO_LOCAL_DATE);

		AssemblaRequest request = new AssemblaRequest(uri, StandupReport.class);
		request.addParam("date", range.getFromString());
		return super.get(request, String.format("No StandupReport found for date (%s)", range.getFromString()));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.StandupReportResource#create(com.assembla.StandupReport, java.time.LocalDate)
	 */
	@Override
	public StandupReport create(StandupReport report, LocalDate date) {
		ValidationUtils.notNull(report, "report == null");
		ValidationUtils.notNull(date, "date == null");

		String uri = String.format("/spaces/%s/standup_reports.json", this.spaceId);
		LocalDateTime asDateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
		DateRange range = new DateRange(asDateTime, asDateTime, DateTimeFormatter.ISO_LOCAL_DATE);

		AssemblaRequest request = new AssemblaRequest(uri, StandupReport.class);
		request.withBody(report);
		request.addParam("date", range.getFromString());
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.StandupReportResource#update(com.assembla.StandupReport, java.time.LocalDate)
	 */
	@Override
	public StandupReport update(StandupReport report, LocalDate date) {
		ValidationUtils.notNull(report, "report == null");
		ValidationUtils.notNull(date, "date == null");

		String uri = String.format("/spaces/%s/standup_reports.json", this.spaceId);
		LocalDateTime asDateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
		DateRange range = new DateRange(asDateTime, asDateTime, DateTimeFormatter.ISO_LOCAL_DATE);

		AssemblaRequest request = new AssemblaRequest(uri, StandupReport.class);
		request.withBody(report);
		request.addParam("date", range.getFromString());
		return super.post(request);
	}

}
