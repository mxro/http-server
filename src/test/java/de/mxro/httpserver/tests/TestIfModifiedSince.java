package de.mxro.httpserver.tests;

import org.junit.Test;

import de.mxro.httpserver.internal.services.ResourceService_DateUtil;

public class TestIfModifiedSince {

    @Test
    public void test() {
        ResourceService_DateUtil.parseDateFromHttpHeader("1412551431938");

        ResourceService_DateUtil.parseDateFromHttpHeader("Thu, 29 Mar 2012 18:19:50 GMT");
    }

}
