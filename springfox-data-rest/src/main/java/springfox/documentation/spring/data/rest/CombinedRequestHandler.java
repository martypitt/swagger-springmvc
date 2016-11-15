/*
 *
 *  Copyright 2016 the original author or authors.
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
package springfox.documentation.spring.data.rest;

import com.google.common.base.Optional;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.RequestHandler;
import springfox.documentation.RequestHandlerKey;
import springfox.documentation.service.ResolvedMethodParameter;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.*;

class CombinedRequestHandler implements RequestHandler {
  private final RequestHandler first;
  private final RequestHandler second;

  CombinedRequestHandler(RequestHandler first, RequestHandler second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public Class<?> declaringClass() {
    return first.declaringClass();
  }

  @Override
  public boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
    return first.isAnnotatedWith(annotation) || second.isAnnotatedWith(annotation);
  }

  @Override
  public PatternsRequestCondition getPatternsCondition() {
    Set<String> patterns = newHashSet(first.getPatternsCondition().getPatterns());
    patterns.addAll(second.getPatternsCondition().getPatterns());
    return new PatternsRequestCondition(patterns.toArray(new String[patterns.size()]));
  }

  @Override
  public String groupName() {
    return first.groupName();
  }

  @Override
  public String getName() {
    return first.getName();
  }

  @Override
  public Set<RequestMethod> supportedMethods() {
    Set<RequestMethod> requestMethods = newHashSet(first.supportedMethods());
    requestMethods.addAll(second.supportedMethods());
    return requestMethods;
  }

  @Override
  public Set<? extends MediaType> produces() {
    Set<MediaType> mediaTypes = newHashSet(first.produces());
    mediaTypes.addAll(second.produces());
    return mediaTypes;
  }

  @Override
  public Set<? extends MediaType> consumes() {
    Set<MediaType> mediaTypes = newHashSet(first.consumes());
    mediaTypes.addAll(second.consumes());
    return mediaTypes;
  }

  @Override
  public Set<NameValueExpression<String>> headers() {
    return first.headers();
  }

  @Override
  public Set<NameValueExpression<String>> params() {
    return first.params();
  }

  @Override
  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    return first.findAnnotation(annotation).or(second.findAnnotation(annotation));
  }

  @Override
  public RequestHandlerKey key() {
      return new RequestHandlerKey(
          getPatternsCondition().getPatterns(),
          supportedMethods(),
          consumes(),
          produces());
  }

  @Override
  public List<ResolvedMethodParameter> getParameters() {
    return first.getParameters();
  }

  @Override
  public ResolvedMethodParameter getReturnParameter() {
    return first.getReturnParameter();
  }

  @Override
  public <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
    return first.findAnnotation(annotation).or(second.findAnnotation(annotation)) ;
  }

  @Override
  public RequestMappingInfo getRequestMapping() {
    return first.getRequestMapping();
  }

  @Override
  public HandlerMethod getHandlerMethod() {
    return second.getHandlerMethod();
  }

  @Override
  public RequestHandler combine(RequestHandler other) {
    return new CombinedRequestHandler(this, other);
  }
}
