package com.assembla.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.assembla.StandupReport;
import com.assembla.client.AssemblaClient;
import com.assembla.client.AssemblaRequest;
import com.assembla.utils.ValidationUtils;

public class StandupAwayReportService extends AbstractBaseService implements StandupAwayReportResource {

	public StandupAwayReportService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.StandupAwayReportResource#getAll(java.time.LocalDate)
	 */
	@Override
	public List<StandupReport> getAll(LocalDate date) {
		ValidationUtils.notNull(date, "from == null");

		String uri = String.format("/spaces/%s/away_standup_reports.json", this.spaceId);
		LocalDateTime fromDT = LocalDateTime.of(date, LocalTime.MIDNIGHT);
		DateRange range = new DateRange(fromDT, fromDT, DateTimeFormatter.ISO_LOCAL_DATE);

		AssemblaRequest request = new AssemblaRequest(uri, StandupReport[].class);
		request.addParam("date", range.getToString());
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.StandupAwayReportResource#get(java.time.LocalDate)
	 */
	@Override
	public StandupReport get(LocalDate date) {
		ValidationUtils.notNull(date, "date == null");
		String uri = String.format("/spaces/%s/away_standup_report.json", this.spaceId);
		LocalDateTime asDateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
		DateRange range = new DateRange(asDateTime, asDateTime, DateTimeFormatter.ISO_LOCAL_DATE);

		AssemblaRequest request = new AssemblaRequest(uri, StandupReport.class);
		request.addParam("date", range.getFromString());
		return super.get(request, String.format("No StandupReport found for date (%s)", range.getFromString()));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.StandupAwayReportResource#create(com.assembla.StandupReport, java.time.LocalDate)
	 */
	@Override
	public StandupReport create(StandupReport report, LocalDate date) {
		ValidationUtils.notNull(report, "report == null");
		ValidationUtils.notNull(date, "date == null");

		String uri = String.format("/spaces/%s/away_standup_reports.json", this.spaceId);
		LocalDateTime asDateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
		DateRange range = new DateRange(asDateTime, asDateTime, DateTimeFormatter.ISO_LOCAL_DATE);

		AssemblaRequest request = new AssemblaRequest(uri, StandupReport.class);
		request.withBody(report);
		request.addParam("date", range.getFromString());
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.StandupAwayReportResource#update(com.assembla.StandupReport, java.time.LocalDate)
	 */
	@Override
	public StandupReport update(StandupReport report, LocalDate date) {
		ValidationUtils.notNull(report, "report == null");
		ValidationUtils.notNull(date, "date == null");
		LocalDateTime asDateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
		DateRange range = new DateRange(asDateTime, asDateTime, DateTimeFormatter.ISO_LOCAL_DATE);
		String uri = String.format("/spaces/%s/away_standup_report.json", this.spaceId);

		AssemblaRequest request = new AssemblaRequest(uri, StandupReport.class);
		request.withBody(report);
		request.addParam("date", range.getFromString());
		return super.post(request);
	}
}
