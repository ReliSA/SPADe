package com.assembla.service;

import java.time.LocalDate;
import java.util.List;

import com.assembla.StandupReport;

public interface StandupAwayReportResource {
	/**
	 * Get stand up away reports for a date
	 * 
	 * @param date
	 *            date of stand up away reports
	 * @return list of stand up away reports which occured on date {@code date}
	 */
	List<StandupReport> getAll(LocalDate date);

	/**
	 * Get stand up away report for a date for the current user
	 * 
	 * @param date
	 *            date which stand up away report was created for
	 * @return stand up away report for date {@code date}
	 */
	StandupReport get(LocalDate date);

	/**
	 * Create a new stand up away report for a date for current user
	 * 
	 * @param report
	 *            report to create, not null
	 * @param date
	 *            , not null date for report
	 * @return newly created stand up report
	 */
	StandupReport create(StandupReport report, LocalDate date);

	/**
	 * Update an existing stand up away report for a date for current user
	 * 
	 * @param report
	 *            stand up away report to update, not null
	 * @param date
	 *            date to update stand up report, not null
	 * @return newly updated stand up away report
	 */
	StandupReport update(StandupReport report, LocalDate date);

}
