package com.jasonpercus.microbean.infrastructure.run.processor;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Condition;
import com.jasonpercus.microbean.api.Service;

@Service
@Condition(P_ConditionAlwaysFalse.class)
public class P_ServiceConditionFalse {

}
