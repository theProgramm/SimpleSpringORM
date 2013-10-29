package dbtest.core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.springframework.beans.BeanUtils;

import dbtest.annotation.ADatabaseFieldName;
import dbtest.annotation.ADatabaseIgnore;
import dbtest.exception.FieldIsNoBeanSqlPropertyException;

class SqlFieldDescriptor {
	
	private final String databaseFieldName;
	private final PropertyDescriptor propertyDescriptor;
	private final Field field;

	public SqlFieldDescriptor(Field field) throws FieldIsNoBeanSqlPropertyException {
		if(field == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		if(field.isAnnotationPresent(ADatabaseIgnore.class)) {
			throw new FieldIsNoBeanSqlPropertyException(field.getName());
		}
		this.field = field;
		this.databaseFieldName = generateDabataseFieldName();
		this.propertyDescriptor = generatePopertyDescriptor();
	}

	
	private String generateDabataseFieldName() {
		if(field.isAnnotationPresent(ADatabaseFieldName.class)) {
			return field.getAnnotation(ADatabaseFieldName.class).value();
		}else{
			return field.getName();
		}
	}
	
	private PropertyDescriptor generatePopertyDescriptor() throws FieldIsNoBeanSqlPropertyException {
		PropertyDescriptor ret = BeanUtils.getPropertyDescriptor(field.getDeclaringClass(), field.getName());
		if(ret == null || ret.getReadMethod() == null || ret.getWriteMethod() == null) {
			throw new FieldIsNoBeanSqlPropertyException(field.getName());
		}
		return ret;
	}


	public String getDatabasefieldName() {
		return databaseFieldName;
	}




	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((databaseFieldName == null) ? 0 : databaseFieldName
						.hashCode());
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime
				* result
				+ ((propertyDescriptor == null) ? 0 : propertyDescriptor
						.hashCode());
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
		SqlFieldDescriptor other = (SqlFieldDescriptor) obj;
		if (databaseFieldName == null) {
			if (other.databaseFieldName != null)
				return false;
		} else if (!databaseFieldName.equals(other.databaseFieldName))
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (propertyDescriptor == null) {
			if (other.propertyDescriptor != null)
				return false;
		} else if (!propertyDescriptor.equals(other.propertyDescriptor))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "SqlFieldDescriptor [databasefieldName=" + databaseFieldName
				+ ", propertyDescriptor=" + propertyDescriptor.getDisplayName() + "]";
	}
	
}
