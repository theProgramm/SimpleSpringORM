package dbtest.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dbtest.annotation.ADatabaseIgnore;
import dbtest.annotation.ADatabaseKey;
import dbtest.annotation.ADatabaseTable;
import dbtest.exception.ClassIsNoSqlBeanException;
import dbtest.exception.ClassIsNoSqlBeanReasonException;
import dbtest.exception.FieldIsNoBeanSqlPropertyException;

class SqlBeanClassDescriptor<B> {

	private static final boolean hack = System.getProperty("run.coverage") != null;
	private final Class<B> wrappedClass;
	private final SqlFieldDescriptor[] fieldDescriptors;
	private final String idColumn;
	private final String sourceTable;
	private final List<ClassIsNoSqlBeanReasonException> exceptions = new ArrayList<>();
	private String[] cachedDatabaseFieldNames;
	private final Constructor<B> constructor;

	public SqlBeanClassDescriptor(Class<B> wrappedObject) {
		this.wrappedClass = wrappedObject;
		fieldDescriptors = generateFieldDescriptors();
		idColumn = generatePrimKeyColumnName();
		sourceTable = generateSourceTable();
		constructor = generateConstructor();
		if (!exceptions.isEmpty()) {
			if (!hack) {
				throw new RuntimeException(
						new ClassIsNoSqlBeanException(
								exceptions
										.toArray(new ClassIsNoSqlBeanReasonException[exceptions
												.size()])));
			}
		}
	}

	private String generatePrimKeyColumnName() {
		if (wrappedClass.isAnnotationPresent(ADatabaseKey.class)) {
			return wrappedClass.getAnnotation(ADatabaseKey.class).value();
		} else {
			exceptions.add(new ClassIsNoSqlBeanReasonException(
					"missing database key"));
			return "--no key--";
		}
	}

	private SqlFieldDescriptor[] generateFieldDescriptors() {
		List<SqlFieldDescriptor> l = new ArrayList<SqlFieldDescriptor>();
		for (Field f : wrappedClass.getDeclaredFields()) {
			if (!f.isAnnotationPresent(ADatabaseIgnore.class)) {
				try {
					l.add(new SqlFieldDescriptor(f));
				} catch (FieldIsNoBeanSqlPropertyException e) {
					exceptions.add(e);
				}
			}
		}
		SqlFieldDescriptor[] result = l
				.toArray(new SqlFieldDescriptor[l.size()]);
		return result;
	}

	private String generateSourceTable() {
		if (wrappedClass.isAnnotationPresent(ADatabaseTable.class)) {
			return wrappedClass.getAnnotation(ADatabaseTable.class).value();
		} else {
			return wrappedClass.getSimpleName();

		}
	}

	private Constructor<B> generateConstructor() {
		Constructor<B> con = null;
		try {
			con = wrappedClass.getDeclaredConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			exceptions.add(new ClassIsNoSqlBeanReasonException(
					"missing no args constructor"));
		}
		return con;
	}

	public SqlFieldDescriptor[] getFieldDescriptors() {
		return fieldDescriptors;
	}

	public SqlFieldDescriptor getSaveFieldDescriptorByPropertyName(
			String propertyName) {
		for (SqlFieldDescriptor d : getFieldDescriptors()) {
			if (d.getPropertyDescriptor().getName().equals(propertyName)) {
				return d;
			}
		}
		throw new IllegalArgumentException("no property descriptor with name ["
				+ propertyName + "] found");
	}

	public SqlFieldDescriptor getSaveFieldDescriptorByDatabaseFieldName(
			String databaseFieldName) {
		for (SqlFieldDescriptor d : getFieldDescriptors()) {
			if (d.getDatabasefieldName().equals(databaseFieldName)) {
				return d;
			}
		}
		throw new IllegalArgumentException();
	}

	public Class<B> getWrappedClass() {
		return wrappedClass;
	}

	public String getIdColumn() {
		return idColumn;
	}

	public String getSourceTable() {
		return sourceTable;
	}

	public String[] getDatabaseFieldNames() {
		if (cachedDatabaseFieldNames == null) {
			cachedDatabaseFieldNames = new String[fieldDescriptors.length];
			for (int i = 0; i < fieldDescriptors.length; i++) {
				cachedDatabaseFieldNames[i] = fieldDescriptors[i]
						.getDatabasefieldName();
			}
		}
		return cachedDatabaseFieldNames;
	}

	public B newInstance() {
		try {
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public BeanPropertyAccess<B> createProeprtyAccess() {
		return new BeanPropertyAccess<>(this);
	}

	public BeanPropertyAccess<B> createProeprtyAccess(B bean) {
		return new BeanPropertyAccess<>(this, bean);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(fieldDescriptors);
		result = prime * result
				+ ((idColumn == null) ? 0 : idColumn.hashCode());
		result = prime * result
				+ ((sourceTable == null) ? 0 : sourceTable.hashCode());
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
		SqlBeanClassDescriptor<?> other = (SqlBeanClassDescriptor<?>) obj;
		if (!Arrays.equals(fieldDescriptors, other.fieldDescriptors))
			return false;
		if (idColumn == null) {
			if (other.idColumn != null)
				return false;
		} else if (!idColumn.equals(other.idColumn))
			return false;
		if (sourceTable == null) {
			if (other.sourceTable != null)
				return false;
		} else if (!sourceTable.equals(other.sourceTable))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SqlBeanClassDescriptor [wrappedClass="
				+ wrappedClass.getSimpleName() + ", idColumn=" + idColumn
				+ ", sourceTable=" + sourceTable + ", fieldDescriptors="
				+ Arrays.toString(fieldDescriptors) + "]";
	}

}
