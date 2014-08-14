package net.tarilabs.reex2014alertlistwidget;

public class Alert<T> {
	
	private long ts;
	private String condition;
	private AlertType type;
	
	private T ref;
	
	public Alert(long ts, String condition,	AlertType type, T ref) {
		super();
		this.ts = ts;
		this.condition = condition;
		this.type = type;
		this.ref = ref;
	}

	public long getTs() {
		return ts;
	}
	
	public String getCondition() {
		return condition;
	}

	public AlertType getType() {
		return type;
	}
	
	public T getRef() {
		return ref;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Alert [");
		if (condition != null)
			builder.append("condition=").append(condition).append(", ");
		if (type != null)
			builder.append("type=").append(type).append(", ");
		builder.append("ts=").append(ts);
		builder.append("]");
		return builder.toString();
	}
}
