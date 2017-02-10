/*
 *
 *  Copyright 2015-2106 the original author or authors.
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

package springfox.documentation.schema

import com.fasterxml.classmate.TypeResolver
import com.google.common.collect.ImmutableSet
import org.springframework.http.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport, AlternateTypesSupport])
class ModelProviderSpec extends Specification {

  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  def "dependencies provider respects ignorables"() {
    given:
      ModelProvider sut = defaultModelProvider()
      def context = inputParam(
        modelType,
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        ImmutableSet.builder().build())
      context.seen(new TypeResolver().resolve(HttpHeaders))
      def modelContexts = sut.modelsFor(context)
      def dependentTypeNames = modelContexts.collect() {
        it.builder.build().getName()
      }.unique()
          .sort()
      
    expect:
      dependencies == dependentTypeNames - rootModelName

    where:
      modelType                      | dependencies                                                   | rootModelName
      genericClassWithGenericField() | ["ResponseEntityAlternative«SimpleType»", "SimpleType"].sort() | "GenericType«ResponseEntityAlternative«SimpleType»»"
  }

  @Unroll
  def "dependencies are inferred correctly by the model provider"() {
    given:
      ModelProvider provider = defaultModelProvider()
      def modelContexts = provider.modelsFor(
        inputParam(
            modelType,
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            ImmutableSet.builder().build()))
      def dependentTypeNames = modelContexts.collect() {
          it.builder.build().getName()
      }.unique()
          .sort()

    expect:
      dependencies == dependentTypeNames - rootModelName

    where:
      modelType                      | dependencies                                                         | rootModelName
      simpleType()                   | []                                                                   | "SimpleType"
      complexType()                  | ["Category"]                                                         | "ComplexType"
      inheritedComplexType()         | ["Category"]                                                         | "InheritedComplexType"
      typeWithLists()                | ["Category", "ComplexType", "Substituted"].sort()                    | "ListsContainer"
      typeWithSets()                 | ["Category", "ComplexType"].sort()                                   | "SetsContainer"
      typeWithArrays()               | ["Category", "ComplexType", "Substituted"]                           | "ArraysContainer"
      genericClass()                 | ["SimpleType"]                                                       | "GenericType«SimpleType»"
      genericClassWithListField()    | ["SimpleType"]                                                       | "GenericType«List«SimpleType»»"
      genericClassWithGenericField() | ["ResponseEntityAlternative«SimpleType»", "SimpleType"].sort()       | "GenericType«ResponseEntityAlternative«SimpleType»»"
      genericClassWithDeepGenerics() | ["ResponseEntityAlternative«List«SimpleType»»", "SimpleType"].sort() | "GenericType«ResponseEntityAlternative«List«SimpleType»»»"
      genericCollectionWithEnum()    | ["Collection«string»"]                                               | "GenericType«Collection«string»»"
      recursiveType()                | ["SimpleType"]                                                       | "RecursiveType"
  }
}
