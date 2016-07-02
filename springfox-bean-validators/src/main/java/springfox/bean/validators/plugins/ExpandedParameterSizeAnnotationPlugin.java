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
package springfox.bean.validators.plugins;

import static springfox.bean.validators.plugins.BeanValidators.validatorFromParameterExpansionField;

import java.lang.reflect.Field;

import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

import springfox.bean.validators.util.SizeUtil;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
@Component
@Order(BeanValidators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class ExpandedParameterSizeAnnotationPlugin implements ExpandedParameterBuilderPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(ExpandedParameterSizeAnnotationPlugin.class);

    /**
     * support all documentationTypes
     */
    @Override
    public boolean supports(DocumentationType delimiter) {
        // we simply support all documentationTypes!
        return true;
    }

    /** 
     * read Size annotation
     */
    @Override
    public void apply(ParameterExpansionContext context) {

        Field myfield = context.getField();
        LOG.debug("expandedparam.myfield: " + myfield.getName());

        Optional<Size> size = extractAnnotation(context);

        if (size.isPresent()) {
            AllowableRangeValues values = SizeUtil.createAllowableValuesFromSizeForStrings(size.get());
            LOG.debug("adding allowable Values: " + values.getMin() + "-" + values.getMax());

            values = new AllowableRangeValues(values.getMin(), values.getMax());
            context.getParameterBuilder().allowableValues(values);

        }
    }

    /**
     * extract Size from bean or field
     * @param context
     * @return
     */
    @VisibleForTesting
    Optional<Size> extractAnnotation(ParameterExpansionContext context) {
        return validatorFromParameterExpansionField(context, Size.class);
    }


}
