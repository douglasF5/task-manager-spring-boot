package com.douglasf.taskmanagerapp.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {

  public static void mergeNonNullProperties(Object source, Object target) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
  }

  public static String[] getNullPropertyNames(Object sourceInputObject) {
    final BeanWrapper source = new BeanWrapperImpl(sourceInputObject);

    PropertyDescriptor[] propertyDescriptors = source.getPropertyDescriptors();

    Set<String> nullProperties = new HashSet<>();

    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
      Object propertyValue = source.getPropertyValue(propertyDescriptor.getName());
      if (propertyValue == null) {
        nullProperties.add(propertyDescriptor.getName());
      }
    }

    String[] result = new String[nullProperties.size()];
    return nullProperties.toArray(result);
  }

}
