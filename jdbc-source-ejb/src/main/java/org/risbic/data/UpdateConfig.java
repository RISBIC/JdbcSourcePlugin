package org.risbic.data;

import java.util.concurrent.TimeUnit;

public class UpdateConfig {
	private int frequency;
	private TimeUnit timeUnit;
	private int batchSize;

	public UpdateConfig() {
	}

	public UpdateConfig(int frequency, TimeUnit timeUnit, int batchSize) {
		this.frequency = frequency;
		this.timeUnit = timeUnit;
		this.batchSize = batchSize;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		UpdateConfig that = (UpdateConfig) o;

		if (batchSize != that.batchSize) {
			return false;
		}
		if (frequency != that.frequency) {
			return false;
		}
		if (timeUnit != that.timeUnit) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = frequency;
		result = 31 * result + (timeUnit != null ? timeUnit.hashCode() : 0);
		result = 31 * result + batchSize;
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("UpdateConfig{");
		sb.append("frequency=").append(frequency);
		sb.append(", timeUnit=").append(timeUnit);
		sb.append(", batchSize=").append(batchSize);
		sb.append('}');
		return sb.toString();
	}
}
