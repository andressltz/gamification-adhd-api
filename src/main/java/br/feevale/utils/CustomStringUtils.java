package br.feevale.utils;

import com.microsoft.applicationinsights.core.dependencies.google.common.base.Strings;
import io.micrometer.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class CustomStringUtils {

	private static final Logger LOG = LogManager.getLogger();

	//	public static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
//	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public static final String DDMMYYYYHHMM = "ddMMyyyyHHmm";

	private CustomStringUtils() {
	}

	public static String formatPhone(String stringPhone) {
		if (stringPhone == null) {
			return null;
		}
		return stringPhone.replaceFirst("(\\d{2})(\\d{5})(\\d+)", "($1) $2-$3");
	}

	public static Date stringToDate(String date, String pattern) {
		try {
			if (date != null && pattern != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				sdf.setLenient(false);
				sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/Sao_Paulo")));
				return sdf.parse(date);
			}
		} catch (final ParseException e) {
			LOG.error(String.format("Error on convert date string %s with pattern %s to Date", date, pattern), e);
		}
		return null;
	}

	public static String dateToString(Date date, String pattern) {
		if (date != null && !Strings.isNullOrEmpty(pattern)) {
			return new SimpleDateFormat(pattern).format(date);
		}
		return null;
	}

	public static String sanitizeNumeric(String string) {
		if (string == null) {
			return null;
		}
		return string.replaceAll("[^0-9]", "");
	}

	public static String numberOrNull(String string) {
		if (string == null) {
			return null;
		}
		string = sanitizeNumeric(string);
		if (StringUtils.isEmpty(string)) {
			return null;
		}
		return string;
	}

	public static int numberOrZero(String string) {
		string = CustomStringUtils.numberOrNull(string);
		int newInt = 0;
		if (string != null) {
			newInt = Integer.parseInt(string);
		}
		return newInt;
	}

	public static String getDurationFormatted(Long totalDuration) {
		if (totalDuration != null) {
			Duration duration = Duration.ofMinutes(totalDuration);
			StringBuilder formattedDuration = new StringBuilder();
			int hours = duration.toHoursPart();
			int minutes = duration.toMinutesPart();
//			formattedDuration.append(hours).append(":").append(minutes);
//			return formattedDuration.toString();
			if (hours > 0) {
				formattedDuration.append(hours).append("h ");
			}
			if (minutes > 0) {
				formattedDuration.append(minutes).append("min");
			}
			return formattedDuration.toString();
		}
		return null;
	}

	public static String getDurationFormatted(String timeToDoString) {
		if (timeToDoString != null) {
			int timeToDo = CustomStringUtils.numberOrZero(timeToDoString);
			if (timeToDo > 0 && timeToDoString.length() == 4) {
				StringBuilder formattedDuration = new StringBuilder();
				int hours = Integer.parseInt(timeToDoString.substring(0, 2));
				int minutes = Integer.parseInt(timeToDoString.substring(2, 4));
				if (hours > 0) {
					formattedDuration.append(hours).append("h ");
				}
				if (minutes > 0) {
					formattedDuration.append(minutes).append("min");
				}
				return formattedDuration.toString();
			}
		}
		return null;
	}

}
