/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.spring.web.authorization

import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Specification
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator

class AuthorizationContextSpec extends Specification implements AuthSupport {

  def "Authorizations work as expected"() {
    given:
    SecurityContext authorizationContext = SecurityContext.builder()
        .forPaths(PathSelectors.any())
        .forHttpMethods { true }
        .operationSelector { o -> o.httpMethod() == HttpMethod.GET }
        .securityReferences(auth)
        .build()
    expect:
    authorizationContext.securityForOperation(operationContext()).size() == expected

    where:
    auth          | expected
    defaultAuth() | 1
    []            | 0
  }

  private OperationContext operationContext() {
    new OperationContext(new OperationBuilder(
        new CachingOperationNameGenerator()),
        RequestMethod.GET,
        Mock(RequestMappingContext),
        1)
  }

}
