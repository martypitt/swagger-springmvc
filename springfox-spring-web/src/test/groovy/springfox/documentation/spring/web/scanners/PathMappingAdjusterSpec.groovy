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

package springfox.documentation.spring.web.scanners

import spock.lang.Specification
import springfox.documentation.spi.service.contexts.DocumentationContext
import springfox.documentation.spring.web.paths.PathMappingAdjuster

class PathMappingAdjusterSpec extends Specification {
  def "Adjust path mapping according to servlet registration" () {
    given:
      def context = Mock(DocumentationContext)
    and:
      context.pathMapping >> Optional.ofNullable(pathMapping)
    when:
      def sut = new PathMappingAdjuster(context)
    then:
      sut.adjustedPath(path) == expected

    where:
      pathMapping   | path    | expected
      "/"           | "/v1"   | "/v1"
      "/path"       | "/v1"   | "/path/v1"
      ""            | "/v1"   | "/v1"
      null          | "/v1"   | "/v1"

  }
}
