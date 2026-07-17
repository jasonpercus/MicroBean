package com.jasonpercus.microbean.cucumber.jdt.context;

/*
 * Copyright (c) 2026 JasonPercus
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for more information.
 */

import com.jasonpercus.microbean.api.Primary;
import com.jasonpercus.microbean.api.Scope;
import com.jasonpercus.microbean.api.Service;

public final class C_Fixtures {

    private C_Fixtures() {
    }

    public interface Contract {
    }

    @Service
    public static class SimpleService {
    }

    @Service(scope = Scope.PROTOTYPE)
    public static class PrototypeService {
    }

    @Service
    public static class SecondaryService implements Contract {
    }

    @Primary
    @Service
    public static class PrimaryService implements Contract {
    }

    @Service
    public static class NoPrimaryOneService implements Contract {
    }

    @Service
    public static class NoPrimaryTwoService implements Contract {
    }
}
