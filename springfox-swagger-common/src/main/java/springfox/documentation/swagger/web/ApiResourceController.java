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
package springfox.documentation.swagger.web;

import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.swagger.util.SwaggerUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@ApiIgnore
@RequestMapping("/swagger-resources")
public class ApiResourceController {


  @Autowired(required = false)
  private SecurityConfiguration securityConfiguration;
  @Autowired(required = false)
  private UiConfiguration uiConfiguration;

  private final SwaggerResourcesProvider swaggerResources;

  @Autowired
  public ApiResourceController(SwaggerResourcesProvider swaggerResources) {
    this.swaggerResources = swaggerResources;
  }

  @RequestMapping(value = "/configuration/security")
  @ResponseBody
  public ResponseEntity<SecurityConfiguration> securityConfiguration() {
    return new ResponseEntity<SecurityConfiguration>(
        Optional.fromNullable(securityConfiguration).or(SecurityConfiguration.DEFAULT), HttpStatus.OK);
  }

  @RequestMapping(value = "/configuration/ui")
  @ResponseBody
  public ResponseEntity<UiConfiguration> uiConfiguration(HttpServletRequest request) {
    UiConfiguration uiConfig = Optional.fromNullable(uiConfiguration).or(UiConfiguration.DEFAULT);
    return new ResponseEntity<UiConfiguration>(uiConfig, HttpStatus.OK);
  }

  @RequestMapping
  @ResponseBody
  public ResponseEntity<List<SwaggerResource>> swaggerResources(HttpServletRequest request) {
    List<SwaggerResource> resources = swaggerResources.get();
    SwaggerUtils.queryPropagation(request,resources);
    return new ResponseEntity<List<SwaggerResource>>(resources, HttpStatus.OK);
  }

}
