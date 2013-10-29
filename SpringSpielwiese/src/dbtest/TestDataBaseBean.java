package dbtest;

import dbtest.annotation.ADatabaseFieldName;
import dbtest.annotation.ADatabaseIgnore;
import dbtest.annotation.ADatabaseKey;
import dbtest.annotation.ADatabaseTable;

@ADatabaseKey("id")
@ADatabaseTable("tests")
public class TestDataBaseBean {
	private long id;
	private String someKey;
	private double someNmbr;
	@ADatabaseIgnore
	private String notMatched;
	private boolean aBo;
	
	@ADatabaseFieldName("fremd")
	private String someOtherKey;

	public TestDataBaseBean(long id, String someKey, double someNmbr,
			String notMatched, boolean aBo, String someOtherKey) {
		super();
		this.id = id;
		this.someKey = someKey;
		this.someNmbr = someNmbr;
		this.notMatched = notMatched;
		this.aBo = aBo;
		this.someOtherKey = someOtherKey;
	}

	public TestDataBaseBean() {
	}

	@Override
	public String toString() {
		return "DataBaseBean [id=" + id + ", someKey=" + someKey
				+ ", someNmbr=" + someNmbr + ", notMatched=" + notMatched
				+ ", aBo=" + aBo + ", someOtherKey=" + someOtherKey + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (aBo ? 1231 : 1237);
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((notMatched == null) ? 0 : notMatched.hashCode());
		result = prime * result + ((someKey == null) ? 0 : someKey.hashCode());
		long temp;
		temp = Double.doubleToLongBits(someNmbr);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((someOtherKey == null) ? 0 : someOtherKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestDataBaseBean other = (TestDataBaseBean) obj;
		if (aBo != other.aBo)
			return false;
		if (id != other.id)
			return false;
		if (notMatched == null) {
			if (other.notMatched != null)
				return false;
		} else if (!notMatched.equals(other.notMatched))
			return false;
		if (someKey == null) {
			if (other.someKey != null)
				return false;
		} else if (!someKey.equals(other.someKey))
			return false;
		if (Double.doubleToLongBits(someNmbr) != Double
				.doubleToLongBits(other.someNmbr))
			return false;
		if (someOtherKey == null) {
			if (other.someOtherKey != null)
				return false;
		} else if (!someOtherKey.equals(other.someOtherKey))
			return false;
		return true;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSomeKey() {
		return someKey;
	}

	public void setSomeKey(String someKey) {
		this.someKey = someKey;
	}

	public double getSomeNmbr() {
		return someNmbr;
	}

	public void setSomeNmbr(double someNmbr) {
		this.someNmbr = someNmbr;
	}

	public String getNotMatched() {
		return notMatched;
	}

	public void setNotMatched(String notMatched) {
		this.notMatched = notMatched;
	}

	public boolean isaBo() {
		return aBo;
	}

	public void setaBo(boolean aBo) {
		this.aBo = aBo;
	}

	public String getSomeOtherKey() {
		return someOtherKey;
	}

	public void setSomeOtherKey(String someOtherKey) {
		this.someOtherKey = someOtherKey;
	}
	
	public boolean isB() {
		return false;
	}
	@ADatabaseIgnore
	public void setB(boolean b) {};
	
}
