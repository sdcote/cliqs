/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial API and implementation
 */
package coyote.cli;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * The Metric class models a basic metric.
 */
public class Metric {
	private String _name = null;

	private String _units = "ms";
	private long _minValue = 0;
	private long _maxValue = 0;
	private int _samples = 0;
	private long _total = 0;
	private long _sumOfSquares = 0;

	static private final String NONE = "";
	static private final String TOTAL = "Total";
	static private final String MIN = "Min Value";
	static private final String MAX = "Max Value";
	static private final String SAMPLES = "Samples";
	static private final String AVG = "Avg";
	static private final String STANDARD_DEVIATION = "Std Dev";




	/**
	 * 
	 */
	public Metric(final String name) {
		_name = name;
	}




	public Metric(final String name, String units) {
		this(name);
		this._units = units;
	}




	/**
	 * Create a deep copy of this counter.
	 */
	@Override
	public Object clone() {
		final Metric retval = new Metric(this._name);
		retval._units = this._units;
		retval._minValue = this._minValue;
		retval._maxValue = this._maxValue;
		retval._samples = this._samples;
		retval._total = this._total;
		retval._sumOfSquares = this._sumOfSquares;
		return retval;
	}




	/**
	 * @return The currently set name of this object.
	 */
	public String getName() {
		return _name;
	}




	/**
	 * @return The number of times the value was updated.
	 */
	public long getSamplesCount() {
		return _samples;
	}




	/**
	 * Included for balance but it should not be used by the uninitiated.
	 * 
	 * @param name The new name to set.
	 */
	void setName(final String name) {
		_name = name;
	}




	/**
	 * @return Returns the maximum value the counter ever represented.
	 */
	public long getMaxValue() {
		synchronized (_name) {
			return _maxValue;
		}
	}




	/**
	 * @return Returns the minimum value the counter ever represented.
	 */
	public long getMinValue() {
		synchronized (_name) {
			return _minValue;
		}
	}




	/**
	 * @return Returns the units the counter measures.
	 */
	public String getUnits() {
		return _units;
	}




	/**
	  * Increase the time by the specified amount of milliseconds.
	  * 
	  * <p>This is the method that keeps track of the various statistics being 
	  * tracked.</p>
	  *
	  * @param value the amount to increase the accrued value.
	  */
	public synchronized void sample(final long value) {
		// Increment the number of samples
		_samples++;

		// calculate min
		if (value < _minValue) {
			_minValue = value;
		}

		// calculate max
		if (value > _maxValue) {
			_maxValue = value;
		}

		// calculate total i.e. sumofX's
		_total += value;

		_sumOfSquares += value * value;
	}




	/**
	 * Set the current, update count and Min/Max values to zero.
	 * 
	 * <p>The return value will represent a copy of the counter prior to the 
	 * reset and is useful for applications that desire delta values. These delta
	 * values are simply the return values of successive reset calls.</p>
	 * 
	 * @return a counter representing the state prior to the reset.
	 */
	public Metric reset() {
		synchronized (_name) {
			final Metric retval = (Metric) clone();

			_minValue = 0;
			_maxValue = 0;
			_samples = 0;
			_total = 0;
			_sumOfSquares = 0;

			return retval;
		}
	}




	/**
	 * Sets the units the counter measures.
	 * 
	 * @param units The units to set.
	 */
	public void setUnits(final String units) {
		synchronized (_name) {
			_units = units;
		}
	}




	/**
	 * Access the current standard deviation for all samples using the Sum of Squares algorithm.
	 *
	 * @return The amount of one standard deviation of all the sample values. 
	 */
	public long getStandardDeviation() {
		long stdDeviation = 0;
		if (_samples != 0) {
			final long sumOfX = _total;
			final int n = _samples;
			final int nMinus1 = (n <= 1) ? 1 : n - 1; // avoid 0 divides;

			final long numerator = _sumOfSquares - ((sumOfX * sumOfX) / n);
			stdDeviation = (long) java.lang.Math.sqrt(numerator / nMinus1);
		}

		return stdDeviation;
	}




	/**
	 * Method convertToString
	 *
	 * @param value
	 *
	 * @return the given long value displayed as a ("#,###") formatted string
	 */
	private String convertToString(final long value) {
		final DecimalFormat numberFormat = (DecimalFormat) NumberFormat.getNumberInstance();
		numberFormat.applyPattern("#,###");

		return numberFormat.format(value);
	}




	/**
	 * Method getDisplayString
	 *
	 * @param type
	 * @param value
	 * @param units
	 *
	 * @return the given type, value and units in a string suitable for display
	 */
	protected String getDisplayString(final String type, final String value, final String units) {
		return type + "=" + value + " " + units + "  ";
	}




	private long getAverage() {
		if (_samples == 0) {
			return 0;
		} else {
			return _total / _samples;
		}
	}




	@Override
	public String toString() {
		final StringBuffer message = new StringBuffer(_name);
		message.append(": ");
		message.append(getDisplayString(SAMPLES, convertToString(_samples), NONE));

		if (_samples > 0) {
			message.append(getDisplayString(AVG, convertToString(getAverage()), _units));
			message.append(getDisplayString(TOTAL, convertToString(_total), _units));
			message.append(getDisplayString(STANDARD_DEVIATION, convertToString(getStandardDeviation()), _units));
			message.append(getDisplayString(MIN, convertToString(_minValue), _units));
			message.append(getDisplayString(MAX, convertToString(_maxValue), _units));
		}

		return message.toString();
	}
}