package dbtest.core;

import java.lang.reflect.InvocationTargetException;

class BeanPropertyAccess<B> {

	/**
	 * 
	 */
	private final SqlBeanClassDescriptor<B> beanClassDescription;
	private B bean;

	public BeanPropertyAccess(SqlBeanClassDescriptor<B> beanClassDescription) {
		this.beanClassDescription = beanClassDescription;
	}

	public BeanPropertyAccess(SqlBeanClassDescriptor<B> beanClassDescription,
			B bean) {
		this.beanClassDescription = beanClassDescription;
		setBean(bean);
	}

	public void setBean(B b) {
		this.bean = b;
	}

	public B getBean() {
		if (bean == null) {
			throw new IllegalStateException("bean is null");
		}
		return bean;
	}

	public Object getPropertyByDatabaseName(String name) {
		return extractValue(this.beanClassDescription
				.getSaveFieldDescriptorByDatabaseFieldName(name));
	}

	public void setPropertyByDatabaseName(String name, Object param) {
		setValue(
				this.beanClassDescription
						.getSaveFieldDescriptorByDatabaseFieldName(name),
				param);
	}

	public Object getPropertyByPropertyName(String name) {
		return extractValue(this.beanClassDescription
				.getSaveFieldDescriptorByPropertyName(name));
	}

	public void setPropertyByByPropertyName(String name, Object param) {
		setValue(
				this.beanClassDescription
						.getSaveFieldDescriptorByPropertyName(name),
				param);
	}

	public Object getIdPropertyValue() {
		return getPropertyByDatabaseName(this.beanClassDescription
				.getIdColumn());
	}

	protected void setIdPropertyValue(Object id) {
		setPropertyByDatabaseName(beanClassDescription.getIdColumn(), id);
	}

	private Object extractValue(SqlFieldDescriptor descriptor) {
		try {
			return descriptor.getPropertyDescriptor().getReadMethod()
					.invoke(getBean());
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private void setValue(SqlFieldDescriptor descriptor, Object param) {
		try {
			descriptor.getPropertyDescriptor().getWriteMethod()
					.invoke(getBean(), param);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}