/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.error;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class SingleElementNavigatorOnlyMethodException extends GebException {

    SingleElementNavigatorOnlyMethodException(Method singleElementMethod, int navigatorSize) {
        super(buildMessage(singleElementMethod, navigatorSize));
    }

    private static String buildMessage(Method singleElementMethod, int navigatorSize) {
        return format(
                "Method %s can only be called on single element navigators but it was called on a navigator with size %d. " +
                        "Please use the spread operator to call this method on all elements of this navigator or change the selector used to create this navigator to only match a single element.",
                methodDescription(singleElementMethod),
                navigatorSize
        );
    }

    private static String methodDescription(Method method) {
        List<String> parameterTypeNames = new ArrayList<>();
        for (Class<?> parameterType : method.getParameterTypes()) {
            parameterTypeNames.add(parameterType.getName());
        }
        return format("%s(%s)", method.getName(), String.join(", ", parameterTypeNames));
    }
}