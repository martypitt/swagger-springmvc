/*
 *
 *  Copyright 2015-2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger2.mappers;

import static springfox.documentation.schema.Collections.collectionElementType;
import static springfox.documentation.schema.Collections.isContainerType;
import static springfox.documentation.schema.Types.isVoid;
import static springfox.documentation.swagger2.mappers.EnumMapper.maybeAddAllowableValues;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.fasterxml.classmate.ResolvedType;

import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BaseIntegerProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.ByteArrayProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DateTimeProperty;
import io.swagger.models.properties.DecimalProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.FileProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;
import io.swagger.models.properties.UUIDProperty;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.util.Strings;

class Properties {
  private static final Map<String, Function<String, ? extends Property>> typeFactory = new HashMap<String, Function<String, ? extends Property>>();
  static {
    typeFactory.put("int", newInstanceOf(IntegerProperty.class));
    typeFactory.put("long", newInstanceOf(LongProperty.class));
    typeFactory.put("float", newInstanceOf(FloatProperty.class));
    typeFactory.put("double", newInstanceOf(DoubleProperty.class));
    typeFactory.put("string", newInstanceOf(StringProperty.class));
    typeFactory.put("boolean", newInstanceOf(BooleanProperty.class));
    typeFactory.put("date", newInstanceOf(DateProperty.class));
    typeFactory.put("date-time", newInstanceOf(DateTimeProperty.class));
    typeFactory.put("bigdecimal", newInstanceOf(DecimalProperty.class));
    typeFactory.put("biginteger", newInstanceOf(BaseIntegerProperty.class));
    typeFactory.put("uuid", newInstanceOf(UUIDProperty.class));
    typeFactory.put("object", newInstanceOf(ObjectProperty.class));
    typeFactory.put("byte", bytePropertyFactory());
    typeFactory.put("__file", filePropertyFactory());
  };

  private Properties() {
    throw new UnsupportedOperationException();
  }

  public static Property property(final String typeName) {
    String safeTypeName = Strings.nullToEmpty(typeName);
    return typeFactory.getOrDefault(safeTypeName.toLowerCase(), voidOrRef(safeTypeName)).apply(safeTypeName);
  }

  public static Property property(final ModelReference modelRef) {
    if (modelRef.isMap()) {
      return new MapProperty(property(modelRef.itemModel().get()));
    } else if (modelRef.isCollection()) {
      if ("byte".equals(modelRef.itemModel().map(toTypeName()).orElse(""))) {
        return new ByteArrayProperty();
      }
      return new ArrayProperty(
          maybeAddAllowableValues(itemTypeProperty(modelRef.itemModel().get()), modelRef.getAllowableValues()));
    }
    return property(modelRef.getType());
  }

  private static Function<? super ModelReference, String> toTypeName() {
    return new Function<ModelReference, String>() {
      @Override
      public String apply(ModelReference input) {
        return input.getType();
      }
    };
  }

  public static Property itemTypeProperty(ModelReference paramModel) {
    if (paramModel.isCollection()) {
      return new ArrayProperty(
          maybeAddAllowableValues(itemTypeProperty(paramModel.itemModel().get()), paramModel.getAllowableValues()));
    }
    return property(paramModel.getType());
  }

  private static <T extends Property> Function<String, T> newInstanceOf(final Class<T> clazz) {
    return new Function<String, T>() {
      @Override
      public T apply(String input) {
        try {
          return clazz.newInstance();
        } catch (Exception e) {
          //This is bad! should never come here
          throw new IllegalStateException(e);
        }
      }
    };
  }

  static Comparator<String> defaultOrdering(Map<String, ModelProperty> properties) {
    return byPosition(properties).thenComparing(byName());
  }

  private static Function<String, ? extends Property> voidOrRef(final String typeName) {
    return new Function<String, Property>() {
      @Override
      public Property apply(String input) {
        if (typeName.equalsIgnoreCase("void")) {
          return null;
        }
        return new RefProperty(typeName);
      }
    };
  }

  private static Function<String, ? extends Property> bytePropertyFactory() {
    return new Function<String, Property>() {
      @Override
      public Property apply(String input) {
        StringProperty byteArray = new StringProperty();
        byteArray.setFormat("byte");
        return byteArray;
      }
    };
  }

  private static Function<String, ? extends Property> filePropertyFactory() {
    return new Function<String, Property>() {
      @Override
      public Property apply(String input) {
        return new FileProperty();
      }
    };
  }

  private static Comparator<String> byName() {
    return new Comparator<String>() {
      @Override
      public int compare(String first, String second) {
        return first.compareTo(second);
      }
    };
  }

  private static Comparator<String> byPosition(final Map<String, ModelProperty> modelProperties) {
    return new Comparator<String>() {
      @Override
      public int compare(String first, String second) {
        ModelProperty p1 = modelProperties.get(first);
        ModelProperty p2 = modelProperties.get(second);
        return Integer.compare(p1.getPosition(), p2.getPosition());
      }
    };
  }

  static Predicate<Map.Entry<String, ModelProperty>> voidProperties() {
    return new Predicate<Map.Entry<String, ModelProperty>>() {
      @Override
      public boolean test(Map.Entry<String, ModelProperty> input) {
        return isVoid(input.getValue().getType())
            || collectionOfVoid(input.getValue().getType())
            || arrayTypeOfVoid(input.getValue().getType().getArrayElementType());
      }
    };
  }

  private static boolean arrayTypeOfVoid(ResolvedType arrayElementType) {
    return arrayElementType != null && isVoid(arrayElementType);
  }

  private static boolean collectionOfVoid(ResolvedType type) {
    return isContainerType(type) && isVoid(collectionElementType(type));
  }
}
