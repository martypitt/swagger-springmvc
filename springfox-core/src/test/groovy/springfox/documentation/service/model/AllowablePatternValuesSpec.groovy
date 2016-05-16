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

package springfox.documentation.service.model

import spock.lang.Specification
import springfox.documentation.service.AllowablePatternValues

/**
 * Created by igorsokolov on 5/16/16.
 */
class AllowablePatternValuesSpec extends Specification {
    def "Bean properties test" () {
        given:
            def sut = new AllowablePatternValues("^[+-]?[0-9]{1,3}(?:,?[0-9]{3})*\\.[0-9]{2}\$")
        expect:
            sut.regexp == "^[+-]?[0-9]{1,3}(?:,?[0-9]{3})*\\.[0-9]{2}\$"
    }
}
