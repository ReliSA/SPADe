package com.assembla.service;

import java.time.LocalDate;
import java.util.List;

import com.assembla.StandupReport;

public interface StandupReportResource {
	/**
	 * Get all stand up reports between two dates
	 * 
	 * @param from
	 *            start date, will return all stand up reports after this date,
	 *            not null, must be less than or equal {@code to}
	 * @param to
	 *            end date, will return all stand up reports before this date,
	 *            not null, must be greater than or equal to {@code from}
	 * @return list of stand up reports between {@code from} and {@code to}
	 */
	List<StandupReport> getAll(LocalDate from, LocalDate to);

	/**
	 * Get stand up report for a date for current user
	 * 
	 * @param date
	 *            date for which stand up report will be returned for, for
	 *            current user, not null
	 * @return stand up report for date {@code date} for current user
	 * 
	 */
	StandupReport get(LocalDate date);

	/**
	 * Create a new stand up report for current user
	 * 
	 * @param report
	 *            report to create, not null
	 * @param date
	 *            date of report, not null
	 * @return newly created stand up report
	 */
	StandupReport create(StandupReport report, LocalDate date);

	/**
	 * Update existing stand up report
	 * 
	 * @param report
	 *            report to update, not null
	 * @param date
	 *            date for which stand up report is for, not null
	 * @return newly updated stand up report
	 */
	StandupReport update(StandupReport report, LocalDate date);

}
