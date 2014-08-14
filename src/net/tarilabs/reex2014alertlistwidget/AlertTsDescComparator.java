package net.tarilabs.reex2014alertlistwidget;

import java.util.Comparator;

public class AlertTsDescComparator implements Comparator<Alert<?>> {

	@Override
	public int compare(Alert<?> arg0, Alert<?> arg1) {
		Long l0 = Long.valueOf(arg0.getTs());
		Long l1 = Long.valueOf(arg1.getTs());
		// notice we want the opposite indeed.
		return l1.compareTo(l0);
	}

}
