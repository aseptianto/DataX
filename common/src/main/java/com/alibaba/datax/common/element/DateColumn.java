package com.alibaba.datax.common.element;

import com.alibaba.datax.common.exception.CommonErrorCode;
import com.alibaba.datax.common.exception.DataXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by jingxing on 14-8-24.
 */
public class DateColumn extends Column {

    private static final Logger LOG = LoggerFactory.getLogger(DateColumn.class);

    private int rawNano;

	private DateType subType = DateType.DATETIME;

	public static enum DateType {
		DATE, TIME, DATETIME
	}

	/**
	 * 构建值为null的DateColumn，使用Date子类型为DATETIME
	 * */
	public DateColumn() {
		this((Long)null);
	}

	/**
	 * 构建值为stamp(Unix时间戳)的DateColumn，使用Date子类型为DATETIME
	 * 实际存储有date改为long的ms，节省存储
	 * */
	public DateColumn(final Long stamp) {
		super(stamp, Column.Type.DATE, (null == stamp ? 0 : 8));
	}

	/**
	 * 构建值为date(java.util.Date)的DateColumn，使用Date子类型为DATETIME
	 * */
	public DateColumn(final Date date) {
		this(date == null ? null : date.getTime());
	}

	/**
	 * 构建值为date(java.sql.Date)的DateColumn，使用Date子类型为DATE，只有日期，没有时间
	 * */
	public DateColumn(final java.sql.Date date) {
		this(date == null ? null : date.getTime());
		this.setSubType(DateType.DATE);
	}

	/**
	 * 构建值为time(java.sql.Time)的DateColumn，使用Date子类型为TIME，只有时间，没有日期
	 * */
	public DateColumn(final java.sql.Time time) {
		this(time == null ? null : time.getTime());
		this.setSubType(DateType.TIME);
	}

	/**
	 * 构建值为ts(java.sql.Timestamp)的DateColumn，使用Date子类型为DATETIME
	 * */
	public DateColumn(final java.sql.Timestamp ts) {
		this(ts == null ? null : ts.getTime());
		this.setRawNano(ts == null ? 0 : ts.getNanos());
		this.setSubType(DateType.DATETIME);
		LOG.warn("DateColumn(Timestamp) ts-> " + (null != ts ? ts.toString() : "NULL"));
		LOG.warn("DateColumn(Timestamp) rawNano-> " + this.getRawNano());
	}

	@Override
	public Long asLong() {

		return (Long)this.getRawData();
	}

	@Override
	public String asString() {
		try {
			return ColumnCast.date2String(this);
		} catch (Exception e) {
			throw DataXException.asDataXException(
					CommonErrorCode.CONVERT_NOT_SUPPORT,
					String.format("Date[%s]类型不能转为String .", this.toString()));
		}
	}

	@Override
	public Date asDate() {
		if (null == this.getRawData()) {
			return null;
		}

		java.sql.Timestamp timestamp = new java.sql.Timestamp((Long) this.getRawData());
		timestamp.setNanos(this.getRawNano());
		LOG.warn("DateColumn.asDate() timestamp-> " + timestamp);
		return timestamp;
    }

	@Override
	public byte[] asBytes() {
		throw DataXException.asDataXException(
				CommonErrorCode.CONVERT_NOT_SUPPORT, "Date类型不能转为Bytes .");
	}

	@Override
	public Boolean asBoolean() {
		throw DataXException.asDataXException(
				CommonErrorCode.CONVERT_NOT_SUPPORT, "Date类型不能转为Boolean .");
	}

	@Override
	public Double asDouble() {
		throw DataXException.asDataXException(
				CommonErrorCode.CONVERT_NOT_SUPPORT, "Date类型不能转为Double .");
	}

	@Override
	public BigInteger asBigInteger() {
		throw DataXException.asDataXException(
				CommonErrorCode.CONVERT_NOT_SUPPORT, "Date类型不能转为BigInteger .");
	}

	@Override
	public BigDecimal asBigDecimal() {
		throw DataXException.asDataXException(
				CommonErrorCode.CONVERT_NOT_SUPPORT, "Date类型不能转为BigDecimal .");
	}

	public DateType getSubType() {
		return subType;
	}

    public void setSubType(DateType subType) {
        this.subType = subType;
    }

    public int getRawNano() {
        return this.rawNano;
    }

    public void setRawNano(int rawNano) {
        this.rawNano = rawNano;
    }
}