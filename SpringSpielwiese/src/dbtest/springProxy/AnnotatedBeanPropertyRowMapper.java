package dbtest.springProxy;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import dbtest.annotation.ADatabaseFieldName;
import dbtest.annotation.ADatabaseIgnore;

public class AnnotatedBeanPropertyRowMapper<T> extends BeanPropertyRowMapper<T> {
	

	private final static boolean hack = System.getProperty("run.coverage") != null;
	/**
	 * calls the super.initialize method and changes the mapped fields
	 */
	protected void initialize(Class<T> mappedClass) {
		super.initialize(mappedClass);
		HashMap<String, PropertyDescriptor> mappedFields = addAnnotationBasedNames();
		removeIgnoredFieldsAndDescriptros(mappedFields);
	}

	private HashMap<String, PropertyDescriptor> addAnnotationBasedNames() {
		HashMap<String, PropertyDescriptor> mappedFields = optainSuperMap();
		List<AnnotatedField<ADatabaseFieldName>> annotatedFields = getAnnotatedFields(ADatabaseFieldName.class);
		for (AnnotatedField<ADatabaseFieldName> annoField : annotatedFields) {
			if (annoField.fieldNameAnno.value().isEmpty()) {
				throw new RuntimeException("the data base field of "
						+ getMappedClass().getCanonicalName() + "."
						+ annoField.field.getName() + " name must not be empty");
			}
			String fieldName = annoField.field.getName();
			if (mappedFields.containsKey(fieldName.toLowerCase())) {
				replaceMappedFieldName(mappedFields, fieldName,
						annoField.fieldNameAnno.value());

			}
		}
		return mappedFields;
	}

	private void removeIgnoredFieldsAndDescriptros(
			HashMap<String, PropertyDescriptor> mappedFields) {

		HashSet<String> mappedProperties = removeMappedPropertiesFromIgnoreFields();
		removeMappedPropertiesFromIgnoreDescriptors(mappedProperties,
				mappedFields);
	}

	private HashSet<String> removeMappedPropertiesFromIgnoreFields() {
		HashSet<String> mappedProperties = optainMappedPropperties();
		List<AnnotatedField<ADatabaseIgnore>> annotatedFields = getAnnotatedFields(ADatabaseIgnore.class);
		for (AnnotatedField<ADatabaseIgnore> annoField : annotatedFields) {
			String name = annoField.field.getName();
			if (mappedProperties.contains(name)) {
				mappedProperties.remove(name);
			}
		}
		return mappedProperties;
	}

	private void removeMappedPropertiesFromIgnoreDescriptors(
			HashSet<String> mappedProperties,
			HashMap<String, PropertyDescriptor> mappedFields) {
		for (PropertyDescriptor descriptor : mappedFields.values()) {
			if (descriptor.getWriteMethod().isAnnotationPresent(
					ADatabaseIgnore.class)) {
				mappedProperties.remove(descriptor.getName());
			}
		}

	}

	private HashMap<String, PropertyDescriptor> optainSuperMap() {
		return optainPrivateField("mappedFields");
	}

	private HashSet<String> optainMappedPropperties() {
		return optainPrivateField("mappedProperties");
	}

	private <F> F optainPrivateField(String name) {
		F ret = null;
		try {
			ret = reflectPrivateField(name);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private <F> F reflectPrivateField(String name) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = BeanPropertyRowMapper.class.getDeclaredField(name);
		field.setAccessible(true);
		F actualField = (F) field.get(this);
		return actualField;
	}

	private <A extends Annotation> List<AnnotatedField<A>> getAnnotatedFields(
			Class<A> anno) {
		Field[] allFields = super.getMappedClass().getDeclaredFields();
		List<AnnotatedField<A>> annotatedFields = new ArrayList<AnnotatedBeanPropertyRowMapper.AnnotatedField<A>>(
				allFields.length);
		for (Field f : allFields) {
			A fieldNameAno = (A) f.getAnnotation(anno);
			if (fieldNameAno != null) {
				annotatedFields.add(new AnnotatedField<A>(f, fieldNameAno));
			}
		}
		return annotatedFields;
	}

	private void replaceMappedFieldName(
			HashMap<String, PropertyDescriptor> mappedFields, String oldName,
			String newName) {
		PropertyDescriptor descriptor = mappedFields.get(oldName.toLowerCase());
		mappedFields.remove(oldName.toLowerCase());
		mappedFields.remove(underscoreName(oldName));
		mappedFields.put(newName, descriptor);
	}

	private String underscoreName(String s) {
		String ret = s;
		try {
			Method m = BeanPropertyRowMapper.class.getDeclaredMethod(
					"underscoreName", String.class);
			m.setAccessible(true);
			ret = (String) m.invoke(this, s);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	public static <T> BeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
	
		BeanPropertyRowMapper<T> newInstance = new AnnotatedBeanPropertyRowMapper<T>();
		newInstance.setCheckFullyPopulated(!hack);
		newInstance.setMappedClass(mappedClass);
		return newInstance;
	}

	private static class AnnotatedField<A extends Annotation> {
		Field field;
		A fieldNameAnno;

		public AnnotatedField(Field field, A fieldNameAnno) {
			if (field == null || fieldNameAnno == null) {
				throw new IllegalArgumentException();
			}
			this.field = field;
			this.fieldNameAnno = fieldNameAnno;
		}

	}
}
